package edu.unisabana.tyvs.tablero;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Un defecto tal como queda registrado en GitHub Issues, reducido a lo que
 * necesitan las metricas.
 *
 * @param severidad  "critica", "alta", "media", "baja" o null si no se clasifico
 * @param prioridad  "alta", "media", "baja" o null si no se clasifico
 * @param modulo     valor de la etiqueta modulo:..., o null
 * @param requisitos identificadores trazados en el cuerpo del issue (RF-03, R4...)
 * @param cerrado    fecha del ultimo cierre, o null si esta abierto
 * @param reabierto  true si alguna vez se reabrio (etiqueta "reabierto")
 */
public record Defecto(int numero, String titulo, boolean abierto, String severidad, String prioridad,
                      String modulo, List<String> requisitos, Instant creado, Instant cerrado,
                      boolean reabierto) {

    /** Tiempo hasta el cierre; solo tiene sentido para los cerrados. */
    public Duration tiempoDeResolucion() {
        return cerrado == null ? null : Duration.between(creado, cerrado);
    }
}
