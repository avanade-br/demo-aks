#/bin/bash

# Instala o client do linkerd, se necessario
if [ `find "${HOME}" -name linkerd | wc -l` -ne '1' ]; then
   echo "curl -sL https://run.linkerd.io/install | sh"
fi

# Adiciona o linkerd no Path
if [ `echo ${PATH} | grep -o linkerd | wc -l` -eq '0' ]; then
   echo "export PATH=$PATH:${HOME}/.linkerd2/bin"
fi

# Pre-check
linkerd check --pre

# Instala o linkerd no cluster
linkerd install | kubectl apply -f-

# Pos check
linkerd check

# Instala o adapter do Prometheus (para ativar Autoscaling por metricas custom)
helm install prometheus-adapter \
    --namespace kube-system \
    --set prometheus.url=http://linkerd-prometheus.linkerd.svc.cluster.local \
    --set prometheus.port=9090 \
    --set rules.default=true \
    stable/prometheus-adapter
