package de.bdoepf;

import com.google.gson.Gson;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.AvroIO;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.windowing.AfterWatermark;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;

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


    }

    public static void main(String[] args) {

        // set up pipeline options
        PipelineOptionsFactory.register(Options.class);
        Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
        options.setStreaming(true);
        Pipeline pipeline = Pipeline.create(options);

        // read messages from PubSub
        PCollection<PubsubMessage> messages = pipeline
                .apply(PubsubIO.readMessages().fromSubscription(options.getSubscription()));

        // output portion of the pipeline
        messages
                .apply("To String", ParDo.of(new DoFn<PubsubMessage, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) throws Exception {
                        String message = new String(c.element().getPayload());
                        c.output(message);
                    }
                }))

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
