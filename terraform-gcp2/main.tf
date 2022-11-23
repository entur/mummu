terraform {
  required_version = ">= 0.13.2"
}

provider "kubernetes" {
  version = ">= 2.13.1"
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

resource "kubernetes_secret" "ror-slack-url" {
  metadata {
    name      = "ror-slack-url"
    namespace = var.kube_namespace
  }

  data = {
    "SLACK_URL"    = var.ror-slack-url
  }
}