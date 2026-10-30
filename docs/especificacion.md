# Especificación de requisitos: Registraduría

Esta especificación es **la referencia** del taller. Si el código y este documento no dicen lo mismo, el defecto está en el código. Cada requisito tiene un identificador (`RF-01`…) para que los defectos se puedan **trazar**: un reporte que no dice qué requisito incumple no se puede validar al cerrarlo.

Si encuentra una ambigüedad en este documento, también es un hallazgo: repórtela como issue con la etiqueta `pregunta`, no como defecto.

---

## Inscripción de votantes (`Registry`)

| ID | Requisito |
|---|---|
| **RF-01** | Una solicitud es inválida (`INVALID`) si no hay persona, si el nombre está vacío o en blanco, o si el documento no es un entero **mayor que cero**. |
| **RF-02** | Una persona fallecida no se inscribe: `DEAD`. |
| **RF-03** | La edad válida va de **0 a 120 años**, ambos incluidos. Fuera de ese rango: `INVALID_AGE`. |
| **RF-04** | Una persona menor de 18 años no se inscribe: `UNDERAGE`. Quien tiene **18 años cumplidos o más** es apto. |
| **RF-05** | Un documento que ya está inscrito no se inscribe de nuevo: `DUPLICATED`. |
| **RF-06** | Las reglas se evalúan **en este orden**: RF-01, RF-02, RF-03, RF-04, RF-05. La primera que se cumpla determina el resultado. Si no se cumple ninguna, el resultado es `VALID`. |
| **RF-07** | Solo quedan inscritas las personas con resultado `VALID`. Una solicitud rechazada **no deja rastro**: la persona puede volver a solicitar cuando cumpla las condiciones. |

## Comprobante (`Comprobante`)

| ID | Requisito |
|---|---|
| **RF-08** | El comprobante de una inscripción válida es un documento oficial. Su primera línea es exactamente `Inscripción exitosa`, y luego incluye el nombre (`Votante: ...`), el documento (`Documento: ...`) y la fecha en formato **`dd/MM/aaaa`** (`Fecha: 25/03/2026`). El comprobante de un rechazo es `Inscripción rechazada: <RESULTADO>`. |

## Importación masiva (`ImportadorCsv`)

| ID | Requisito |
|---|---|
| **RF-09** | La importación recibe un archivo CSV con una línea de encabezado y luego líneas `nombre;documento;edad;genero;vivo`. Se **toleran espacios** alrededor de cada valor. Cada línea bien formada se procesa con las reglas RF-01 a RF-07. Una línea mal formada (campos faltantes o sobrantes, documento o edad no numéricos, género desconocido) se **reporta** con su número de línea y su motivo, y se **omite**: la importación continúa con las demás líneas. |

## Estadísticas (`Estadisticas`)

| ID | Requisito |
|---|---|
| **RF-10** | El porcentaje de aprobados de una jornada es `VALID / total × 100`, **redondeado al entero más cercano** (2 de 3 son 67 %). Sin solicitudes, es 0. |
