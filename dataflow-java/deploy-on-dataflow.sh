#!/bin/bash
mvn compile exec:java -Dexec.mainClass=de.bdoepf.Main \
     -Dexec.args="--runner=DataflowRunner \
                  --jobName=gcp-pipeline \
                  --project=${GCP_PROJECT} \
                  --region=${GCP_REGION} \
                  --subscription=${SUBSCRIPTION} \
                  --gcpTempLocation=gs://${PIPELINE_BUCKET}/dataflow/tmp \
                  --enrichmentPath=gs://${PIPELINE_BUCKET}/${ENRICHMENT_CSV_PATH} \
                  --output=gs://${PIPELINE_BUCKET}/dataflow/data/"