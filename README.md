# Serverless data pipeline on Google Cloud Platform (GCP)
This pipeline ingests data by using PubSub. 
Dataflow is used for processing and enriching. Cloud Storage as data lake sink. 

Used Services:
* Pub/Sub
* Apache Beam & Dataflow
* Cloud Storage 

## Usage:
### Pre-requirements:
* terraform installed
* python3 installed
* maven installed
* Environment variable `GCP_PROJECT` must be set
* Environment variables `GOOGLE_APPLICATION_CREDENTIALS` and `GOOGLE_CREDENTIALS` must point to server account json file

### Deploy infrastructure via terraform
```
bash
cd terraform
terraform apply
source outputs_env.sh
cd ..
```

### Deploy Dataflow pipeline
```
cd dataflow-java
./deploy-on-dataflow.sh
cd ..
```

### Publish test messages
```
cd pubsub-publisher 
pip3 install -r requirements.txt
python3 main.py
cd ..
```

### Check results
Check if Dataflow writes the file in 1min batched files into `gs://data-pipeline-{random-id}/dataflow/data/`

## Outlook / Extensions
* Cloud IoT as serverless proxy for MQTT in front of PubSub
* Dataflow:
    * Avro / Parquet instead of json new line delimited files for storing files on Cloud Storage
    * BigQuery beside Cloud Storage as second streaming sink for structured data
* ...
