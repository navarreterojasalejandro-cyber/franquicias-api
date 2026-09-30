variable "project_id" {
  description = "ID of an existing MongoDB Atlas project."
  type        = string
}

variable "api_cidr" {
  description = "Public IPv4 CIDR allowed to reach Atlas (use /32 for a single host)."
  type        = string
}

variable "database_username" {
  description = "Database user created for the API."
  type        = string
  default     = "franquicias_api"
}

variable "database_password" {
  description = "Password for the Atlas database user. Protect Terraform state and tfvars containing this value."
  type        = string
  sensitive   = true
}

variable "atlas_region" {
  description = "MongoDB Atlas region name."
  type        = string
  default     = "US_EAST_1"
}
