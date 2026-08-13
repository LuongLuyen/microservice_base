variable "cluster_name" {
  description = "MSK cluster name"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

output "bootstrap_brokers" {
  description = "Kafka bootstrap brokers"
  value       = "placeholder"
}
