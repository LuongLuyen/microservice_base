variable "cluster_id" {
  description = "Redis cluster ID"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

output "redis_endpoint" {
  description = "Redis endpoint"
  value       = "placeholder"
}
