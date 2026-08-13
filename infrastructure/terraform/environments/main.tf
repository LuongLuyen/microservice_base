terraform {
  required_version = ">= 1.5"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "ap-southeast-1"
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "cluster_name" {
  description = "EKS cluster name"
  type        = string
  default     = "eazybank"
}

# EKS cluster (simplified — actual implementation needs VPC, subnets, node groups)
module "eks" {
  source       = "../modules/eks"
  cluster_name = "${var.cluster_name}-${var.environment}"
  environment  = var.environment
}

# RDS PostgreSQL for each service
module "rds_accounts" {
  source          = "../modules/rds"
  database_name   = "accounts"
  environment     = var.environment
  instance_class  = var.environment == "prod" ? "db.t3.medium" : "db.t3.small"
}

module "rds_cards" {
  source          = "../modules/rds"
  database_name   = "cards"
  environment     = var.environment
  instance_class  = var.environment == "prod" ? "db.t3.medium" : "db.t3.small"
}

module "rds_loans" {
  source          = "../modules/rds"
  database_name   = "loans"
  environment     = var.environment
  instance_class  = var.environment == "prod" ? "db.t3.medium" : "db.t3.small"
}

# MSK (Managed Kafka) — simplified
module "msk" {
  source      = "../modules/msk"
  cluster_name = "${var.cluster_name}-${var.environment}-kafka"
  environment = var.environment
}

# ElastiCache Redis
module "redis" {
  source      = "../modules/elasticache"
  cluster_id = "${var.cluster_name}-${var.environment}-redis"
  environment = var.environment
}

# HashiCorp Vault
module "vault" {
  source       = "../modules/vault"
  environment  = var.environment
}
