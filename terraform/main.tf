resource "mongodbatlas_advanced_cluster" "franquicias" {
  project_id   = var.project_id
  name         = "franquicias-api"
  cluster_type = "REPLICASET"

  replication_specs = [
    {
      region_configs = [
        {
          electable_specs = {
            instance_size = "M0"
          }
          provider_name         = "TENANT"
          backing_provider_name = "AWS"
          region_name           = var.atlas_region
          priority              = 7
        }
      ]
    }
  ]
}

resource "mongodbatlas_database_user" "api" {
  project_id         = var.project_id
  username           = var.database_username
  password           = var.database_password
  auth_database_name = "admin"

  roles {
    role_name     = "readWrite"
    database_name = "franquicias"
  }
}

resource "mongodbatlas_project_ip_access_list" "api" {
  project_id = var.project_id
  cidr_block = var.api_cidr
  comment    = "Franquicias API access"
}
