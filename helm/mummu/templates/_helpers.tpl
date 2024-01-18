{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "app.name" -}}
mummu
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "app.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}


{{/* Generate basic labels */}}
{{- define "mummu.common.labels" }}
app: {{ template "app.name" . }}
chart: {{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
release: {{ .Release.Name }}
team: ror
slack: talk-ror
namespace: {{ .Release.Namespace }}
{{- end }}

{{- define "mummu.cron-job-template" }}
spec:
 containers:
  - name: "redeploy-{{ template "app.name" . }}"
    image: eu.gcr.io/entur-system-1287/deployment-rollout-restart:0.1.2
    imagePullPolicy: IfNotPresent
    command:
      - ./redeploy_generic_deployment.sh
    env:
    - name: DEPLOYMENT
      value: {{ template "app.name" . }}
    - name: CLOUDSDK_CORE_PROJECT
      value: {{ .Values.cronJob.gcpProject }}
    envFrom:
    - secretRef:
         name: ror-slack-url
    securityContext:
      runAsNonRoot: true
      allowPrivilegeEscalation: false
      capabilities:
        drop: ["ALL"]
      seccompProfile:
        type: RuntimeDefault
    resources: {}
 dnsPolicy: ClusterFirst
 restartPolicy: Never
 schedulerName: default-scheduler
 securityContext:
   runAsGroup: 1000
   runAsNonRoot: true
   runAsUser: 1000
 serviceAccountName: application
 terminationGracePeriodSeconds: 30
{{- end }}

