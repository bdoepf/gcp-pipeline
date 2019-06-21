resource "random_id" "pipeline-bucket" {
  byte_length = 1
}

resource "google_storage_bucket" "pipeline-bucket" {
  name     = "data-pipeline-${random_id.pipeline-bucket.dec}"
  location = "${var.region}"
  project = "${var.project}"
  storage_class = "REGIONAL"
}