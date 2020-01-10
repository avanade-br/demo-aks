#/bin/bash
kubectl get deploy -o yaml | linkerd inject - | kubectl apply -f -
