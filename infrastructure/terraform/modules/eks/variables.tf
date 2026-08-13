variable "cluster_name" {
  description = "EKS cluster name"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

output "cluster_endpoint" {
  description = "EKS cluster endpoint"
  value       = "placeholder"
}

output "cluster_name" {
  description = "EKS cluster name"
  value       = var.cluster_name
}
