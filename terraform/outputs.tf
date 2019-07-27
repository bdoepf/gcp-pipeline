output "pubsub-subscription-path" {
  value = google_pubsub_subscription.dataflow-subscription.path
}

output "bucket-name" {
  value = google_storage_bucket.pipeline-bucket.name
}

output "region" {
  value = var.region
}

output "topic-name" {
  value = google_pubsub_topic.pipeline-ingest.name
}

output "enrichment-csv-path" {
  value = google_storage_bucket_object.enrichment.name
}
