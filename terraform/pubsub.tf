resource "google_pubsub_topic" "pipeline-ingest" {
  name = "pipeline-ingest"
  project = var.project
}

resource "google_pubsub_subscription" "dataflow-subscription" {
  name  = "dataflow-subscription"
  topic = google_pubsub_topic.pipeline-ingest.name
  project = var.project

  ack_deadline_seconds = 20
}