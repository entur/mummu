
terraform {
  required_version = ">= 0.12"
}

provider "kubernetes" {
  version = "~> 1.13.3"
  load_config_file = var.load_config_file
}

resource "kubernetes_secret" "ror-mummu-client-secrets" {
  metadata {
    name      = "${var.labels.team}-${var.labels.app}-secrets"
    namespace = var.kube_namespace
  }

  data = {
    "KAFKAUSERNAME"    = var.ror-mummu-kafka-username
    "KAFKAPASSWORD"    = var.ror-mummu-kafka-password
  }
}