variable "database_name" {
  description = "Database name"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "instance_class" {
  description = "RDS instance class"
  type        = string
}

output "database_endpoint" {
  description = "RDS endpoint"
  value       = "placeholder"
}

output "database_name" {
  description = "Database name"
  value       = var.database_name
}
