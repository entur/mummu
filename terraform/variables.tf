variable "kube_namespace" {
  description = "The Kubernetes namespace"
}

variable "load_config_file" {
  description = "Do not load kube config file"
  default     = false
}

variable "labels" {
  description = "Labels used in all resources"
  type        = map(string)
  default = {
    manager = "terraform"
    team    = "ror"
    slack   = "talk-ror"
    app     = "mummu"
  }
}

variable  ror-mummu-kafka-username {
  description = "Kafka user name"
}

variable ror-mummu-kafka-password {
  description = "Kafka user password"
}