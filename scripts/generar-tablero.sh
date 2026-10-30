#!/usr/bin/env bash
# Exporta los issues del repositorio actual y genera el tablero de metricas.
# Requiere GitHub CLI autenticado (gh auth login), Java 17 y Maven.
#
#   ./scripts/generar-tablero.sh                          # corte: ahora
#   ./scripts/generar-tablero.sh 2026-11-20T23:59:00Z     # corte fijo (entrega)
#
# Deja metricas/issues.json (los datos) y metricas/TABLERO.md (el tablero).
# Los dos se versionan: el tablero de la entrega debe poder regenerarse a
# partir de los datos de la entrega.
set -euo pipefail
cd "$(dirname "$0")/.."

mkdir -p metricas
gh issue list --state all --limit 1000 \
  --json number,title,state,labels,createdAt,closedAt,body > metricas/issues.json

(cd registraduria && mvn -B -q compile exec:java \
  -Dexec.args="../metricas/issues.json ../metricas/TABLERO.md ${1:-}")
