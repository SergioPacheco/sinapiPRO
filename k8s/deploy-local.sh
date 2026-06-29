#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
IMAGE="ghcr.io/sinapipro/api:latest"

echo "=== SinapiPRO — Deploy Local (Minikube) ==="

# 1. Verificar pré-requisitos
for cmd in minikube kubectl docker; do
  if ! command -v "$cmd" &>/dev/null; then
    echo "❌ $cmd não encontrado. Instale antes de continuar."
    exit 1
  fi
done

# 2. Iniciar Minikube (se não estiver rodando)
if ! minikube status &>/dev/null; then
  echo "▶ Iniciando Minikube..."
  minikube start --driver=docker --memory=4096 --cpus=2
fi

# 3. Habilitar addons necessários
echo "▶ Habilitando ingress e metrics-server..."
minikube addons enable ingress
minikube addons enable metrics-server

# 4. Construir imagem no Docker do Minikube
echo "▶ Construindo imagem da API..."
eval $(minikube docker-env)
docker build -t "$IMAGE" "$PROJECT_DIR/api"

# 5. Aplicar manifests
echo "▶ Aplicando manifests Kubernetes..."
kubectl apply -f "$SCRIPT_DIR/namespace.yaml"
kubectl apply -f "$SCRIPT_DIR/configmap.yaml"
kubectl apply -f "$SCRIPT_DIR/secret.yaml"
kubectl apply -f "$SCRIPT_DIR/deployment.yaml"
kubectl apply -f "$SCRIPT_DIR/service.yaml"
kubectl apply -f "$SCRIPT_DIR/ingress.yaml"
kubectl apply -f "$SCRIPT_DIR/hpa.yaml"

# 6. Aguardar pods ficarem prontos
echo "▶ Aguardando pods ficarem prontos..."
kubectl -n sinapipro rollout status deploy/sinapipro-api --timeout=120s

# 7. Mostrar estado
echo ""
echo "=== Estado do cluster ==="
kubectl -n sinapipro get pods,svc,ingress,hpa
echo ""
echo "=== Acesso ==="
echo "  Port-forward:  kubectl -n sinapipro port-forward svc/sinapipro-api 8080:80"
echo "  Swagger:       http://localhost:8080/swagger-ui.html"
echo "  Health:        http://localhost:8080/actuator/health"
echo "  Logs:          kubectl -n sinapipro logs -f deploy/sinapipro-api"
echo ""
echo "✅ Deploy concluído!"
