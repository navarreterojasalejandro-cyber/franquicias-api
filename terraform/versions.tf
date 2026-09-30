terraform {
  required_version = ">= 1.10.0"
  required_providers {
    mongodbatlas = {
      source  = "mongodb/mongodbatlas"
      version = ">= 2.0.0, < 3.0.0"
    }
  }
}

provider "mongodbatlas" {
  # Read MONGODB_ATLAS_CLIENT_ID and MONGODB_ATLAS_CLIENT_SECRET from the environment.
}
