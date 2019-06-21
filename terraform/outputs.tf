output "pubsub-subscription-path" {
  value = "${google_pubsub_subscription.dataflow-subscription.path}"
}

output "bucket-name" {
  value = "${google_storage_bucket.pipeline-bucket.name}"
}

output "region" {
  value = "${var.region}"
}
