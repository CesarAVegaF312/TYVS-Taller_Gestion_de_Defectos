#!/usr/bin/env bash
# Crea (o actualiza) las etiquetas del flujo de defectos en el repositorio
# actual. Requiere GitHub CLI autenticado: gh auth login
#
#   ./scripts/crear-etiquetas.sh
#
# --force actualiza la etiqueta si ya existe, asi que se puede ejecutar
# varias veces sin errores.
set -euo pipefail

etiqueta() { gh label create "$1" --color "$2" --description "$3" --force; }

etiqueta "defecto"            "d73a4a" "Comportamiento que no cumple la especificacion"
etiqueta "pregunta"           "d876e3" "Ambiguedad de la especificacion: se aclara, no se corrige"

# Severidad: que tan grave es tecnicamente (lo decide quien prueba).
etiqueta "severidad:critica"  "b60205" "Bloquea una funcion esencial, corrompe datos o no tiene alternativa"
etiqueta "severidad:alta"     "d93f0b" "Funcion importante incorrecta, con alternativa costosa"
etiqueta "severidad:media"    "fbca04" "Funcion secundaria incorrecta o resultado erroneo acotado"
etiqueta "severidad:baja"     "c2e0c6" "Cosmetico o de texto; no altera resultados"

# Prioridad: que tan urgente es corregirlo (lo decide el lider con negocio).
etiqueta "prioridad:alta"     "0e8a16" "Se corrige en esta iteracion"
etiqueta "prioridad:media"    "5319e7" "Se corrige en la siguiente iteracion"
etiqueta "prioridad:baja"     "c5def5" "Se corrige cuando haya capacidad"

# Estado del ciclo de vida (abierto/cerrado lo da GitHub).
etiqueta "estado:nuevo"         "ededed" "Reportado, sin clasificar"
etiqueta "estado:asignado"      "bfdadc" "Clasificado y con responsable"
etiqueta "estado:en-progreso"   "1d76db" "En correccion"
etiqueta "estado:solucionado"   "0052cc" "Corregido; pendiente de validar por otra persona"
etiqueta "estado:en-validacion" "006b75" "Quien reporto esta verificando la correccion"
etiqueta "reabierto"            "e99695" "La correccion no resolvio el defecto. No se quita al volver a cerrar"

# Modulos de la Registraduria del taller. En el proyecto, cambielos por los de su caso.
for m in registro comprobante importacion estadisticas; do
  etiqueta "modulo:$m" "f9d0c4" "Modulo $m"
done
