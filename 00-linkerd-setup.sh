#/bin/bash

curl -sL https://run.linkerd.io/install | sh
export PATH=$PATH:${HOME}/.linkerd2/bin

linkerd check --pre
linkerd install | kubectl apply -f-

helm install prometheus-adapter \
    --namespace kube-system \
    --set prometheus.url=http://linkerd-prometheus.linkerd.svc.cluster.local \
    --set prometheus.port=9090 \
    --set rules.default=true \
    stable/prometheus-adapter

linkerd check