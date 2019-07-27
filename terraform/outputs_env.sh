#!/bin/bash
export GCP_REGION=$(terraform output region)
export SUBSCRIPTION=$(terraform output pubsub-subscription-path)
export PIPELINE_BUCKET=$(terraform output bucket-name)
export TOPIC_NAME=$(terraform output topic-name)
export ENRICHMENT_CSV_PATH=$(terraform output enrichment-csv-path)