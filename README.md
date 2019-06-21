# Serverless data pipeline on GCP
Used Services:
* Pub/Sub
* Apache Beam & Dataflow
* Cloud Storage 

## How-To
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
cd dataflow
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
