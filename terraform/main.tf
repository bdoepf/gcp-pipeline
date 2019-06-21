terraform {
  backend "gcs" {
    bucket  = "tf-state-2451"
    prefix  = "gcp-pipeline"
  }
}

provider "google" {
}