output "atlas_srv_address" {
  description = "Atlas DNS address to use in MONGODB_URI."
  value       = mongodbatlas_advanced_cluster.franquicias.connection_strings[0].standard_srv
}

output "database_name" {
  value = "franquicias"
}
