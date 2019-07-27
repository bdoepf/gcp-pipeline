package de.bdoepf;

import com.google.api.services.dataflow.model.ParDoInstruction;
import com.google.gson.Gson;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.AvroIO;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.windowing.AfterWatermark;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.*;
import org.joda.time.Duration;

import java.util.Map;

public class Main {


    /**
     * Interface for program arguments
     */
    public interface Options extends PipelineOptions {
        String getTempLocation();

        void setTempLocation(String tempLocation);

        boolean isStreaming();

        void setStreaming(boolean streaming);

        String getSubscription();

        void setSubscription(String subscription);

        String getOutput();

        void setOutput(String output);

        String getEnrichmentPath();

        void setEnrichmentPath(String enrichment);
    }

    public static void main(String[] args) {

        // set up pipeline options
        PipelineOptionsFactory.register(Options.class);
        Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
        options.setStreaming(true);
        Pipeline pipeline = Pipeline.create(options);

        // Side input
        PCollectionView<Map<Integer, String>> enrichmentSideInput = pipeline
                .apply("Read enrichment csv", TextIO.read().from(options.getEnrichmentPath()))
                .apply("Split csv", MapElements
                        .into(TypeDescriptors.kvs(TypeDescriptors.integers(), TypeDescriptors.strings()))
                        .via((String line) -> {
                            String[] split = line.split(",");
                            return KV.of(Integer.parseInt(split[0]), split[1]);
                        }))
                .apply("Side input", View.asMap());


        // read messages from PubSub
        PCollection<PubsubMessage> messages = pipeline
                .apply(PubsubIO.readMessages().fromSubscription(options.getSubscription()));

        // output portion of the pipeline
        messages
                .apply("To String", MapElements
                        .into(TypeDescriptors.strings())
                        .via((PubsubMessage msg) -> new String(msg.getPayload())))

                // Decode
                .apply("Decode", MapElements
                        .into(TypeDescriptor.of(IncomingPubSubMsg.class))
                        .via(s -> new Gson().fromJson(s, IncomingPubSubMsg.class)))

                // enrich message by side input
                .apply("Enrich msgs", ParDo.of(new DoFn<IncomingPubSubMsg, String>() {
                    @ProcessElement
                    public void processElement(@Element IncomingPubSubMsg incomingPubSubMsg, OutputReceiver<String> out, ProcessContext c) {
                        String additional = c.sideInput(enrichmentSideInput).get(incomingPubSubMsg.getE());
                        additional = (additional == null) ? "no additional info" : additional;
                        out.output(incomingPubSubMsg.getA() + "," + incomingPubSubMsg.getB() + "," + incomingPubSubMsg.getE() + "," + additional);
                    }
                }).withSideInputs(enrichmentSideInput))

                // Batch events into n minute windows
                .apply("Batch Events", Window.<String>into(
                        FixedWindows.of(Duration.standardMinutes(1)))
                        .triggering(AfterWatermark.pastEndOfWindow())
                        .discardingFiredPanes()
                        .withAllowedLateness(Duration.standardMinutes(1)))

                // Save the events
                .apply("To Text", TextIO.write()
                        .withWindowedWrites()
                        .withNumShards(1) // One file per window
                        .to(options.getOutput()));

        // run the pipeline
        pipeline.run();
    }
}
