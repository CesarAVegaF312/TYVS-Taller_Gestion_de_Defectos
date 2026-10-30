# Informe técnico de validación: <nombre del sistema o caso>

> Plantilla del informe final. Cópiela como página del Wiki (o como `docs/informe-final.md`) y reemplace lo que está entre `< >`.
> Extensión orientativa: **4 a 8 páginas** exportado a PDF. Más largo no es mejor: cada sección debe poder leerse en un par de minutos.
> Las cifras salen del tablero (`metricas/TABLERO.md`) con la **misma fecha de corte**; no las copie a mano.

**Equipo:** <integrantes y rol de cada uno: quién reportó, quién corrigió, quién validó>
**Fecha de corte:** <AAAA-MM-DD>
**Repositorio y commit evaluado:** <URL> @ <commit>

---

## 1. Resumen ejecutivo

Tres a cinco frases para alguien que no va a leer el resto: ¿el sistema está listo para entregarse? ¿Qué riesgos quedan abiertos y quién debe decidir sobre ellos?

## 2. Alcance de la validación

- Qué se probó y contra qué referencia (especificación, ficha del caso, criterios de aceptación).
- Qué **no** se probó, y por qué.
- Técnicas usadas (pruebas unitarias, de integración, de carga, exploratorias...).

## 3. Resultados de las pruebas

| Indicador | Valor |
|---|---|
| Pruebas automatizadas | <n> (<n> nuevas en este ciclo) |
| Cobertura de líneas (JaCoCo) | <x %> |
| Defectos encontrados | <n> |
| Defectos cerrados y validados | <n> |

## 4. Defectos

### 4.1 Métricas

Las cinco métricas mínimas del tablero (severidad, abiertos/cerrados, MTTR, tasa de reapertura, distribución por módulo y por requisito), con los gráficos y **una interpretación de cada una**. Un número sin interpretación no aporta nada al informe.

### 4.2 Defectos relevantes

Los 3 a 5 defectos más importantes, con enlace al issue: qué pasaba, impacto, causa raíz, corrección y cómo se validó.

### 4.3 Defectos abiertos

Cada defecto abierto con su justificación: por qué no se corrigió y qué riesgo se acepta.

## 5. Verificación y validación

- **Verificación** (¿construimos el producto correctamente?): qué evidencia lo respalda.
- **Validación** (¿construimos el producto correcto?): qué ambigüedades de la especificación se encontraron y cómo se resolvieron.

## 6. Criterios de salida

| Criterio | Umbral | Valor | ¿Cumple? |
|---|---|---|---|
| Defectos críticos abiertos | 0 | <n> | <sí/no> |
| Defectos altos abiertos | <umbral del equipo> | <n> | |
| Pruebas en verde en el pipeline | 100 % | | |
| <otro criterio del plan de pruebas> | | | |

**Decisión:** <apto / apto con riesgos aceptados / no apto>, y quién la toma.

## 7. Lecciones aprendidas

¿Qué haría distinto el equipo en el próximo ciclo? Sea concreto: "probar más" no es una lección.

## Referencias

Solo si cita fuentes externas, en formato **APA 7**. La especificación, los issues y el código del propio repositorio se enlazan, no se citan.
