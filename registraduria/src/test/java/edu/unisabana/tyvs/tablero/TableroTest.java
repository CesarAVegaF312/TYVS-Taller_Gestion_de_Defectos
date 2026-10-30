package edu.unisabana.tyvs.tablero;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Las metricas se prueban contra una exportacion FIJA (issues-ejemplo.json),
 * nunca contra GitHub: una prueba que depende de la red y de los issues de hoy
 * no es reproducible. Los valores esperados estan calculados a mano.
 */
class TableroTest {

    private static final Instant AHORA = Instant.parse("2026-03-10T09:00:00Z");

    private static List<Defecto> defectos;
    private static MetricasDeDefectos metricas;

    @BeforeAll
    static void cargar() throws IOException {
        try (InputStream in = TableroTest.class.getResourceAsStream("/issues-ejemplo.json")) {
            defectos = LectorDeIssues.leer(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        }
        metricas = new MetricasDeDefectos(defectos);
    }

    @Test
    void soloCuentaLosIssuesConLaEtiquetaDefecto() {
        // 8 issues en el archivo; el #8 es una mejora.
        assertEquals(7, metricas.total());
    }

    @Test
    void distribuyePorSeveridadEnOrdenDeGravedad() {
        assertEquals(List.of("critica", "alta", "media", "baja", "sin clasificar"),
                List.copyOf(metricas.porSeveridad().keySet()));
        assertEquals(List.of(1L, 2L, 2L, 1L, 1L), List.copyOf(metricas.porSeveridad().values()));
    }

    @Test
    void cuentaAbiertosYCerrados() {
        assertEquals(4, metricas.abiertos());
        assertEquals(3, metricas.cerrados());
    }

    @Test
    void elTiempoMedioDeResolucionSoloUsaLosCerrados() {
        // #1: 2 dias, #2: 7 dias, #3: 0,5 dias -> 9,5 / 3
        assertEquals(9.5 / 3, metricas.tiempoMedioDeResolucionDias().getAsDouble(), 1e-9);
    }

    @Test
    void laTasaDeReaperturaIncluyeLosReabiertosQueSiguenAbiertos() {
        // Reabiertos: #2 (cerrado) y #6 (abierto). Cerrados alguna vez: #1, #2, #3 y #6.
        assertEquals(0.5, metricas.tasaDeReapertura().getAsDouble(), 1e-9);
    }

    @Test
    void elBacklogOrdenaPorPrioridadLuegoSeveridad() {
        assertEquals(List.of(4, 6, 5, 7), metricas.backlogPriorizado().stream().map(Defecto::numero).toList());
    }

    @Test
    void detectaDefectosSinClasificarYSinTrazabilidad() {
        assertEquals(List.of(7), metricas.sinClasificar().stream().map(Defecto::numero).toList());
        assertEquals(List.of(7), metricas.sinTrazabilidad().stream().map(Defecto::numero).toList());
    }

    @Test
    void unDefectoPuedeTrazarAVariosRequisitos() {
        Map<String, Long> porRequisito = metricas.porRequisito();
        assertEquals(1L, porRequisito.get("RF-02"));
        assertEquals(1L, porRequisito.get("RF-03"));
        assertEquals(7, porRequisito.size());
    }

    @Test
    void laMatrizCruzaSeveridadYPrioridad() {
        assertEquals(2L, metricas.matrizSeveridadPrioridad().get("alta").get("alta"));
        assertEquals(1L, metricas.matrizSeveridadPrioridad().get("baja").get("alta"));
    }

    @Test
    void elTableroTraeLasMetricasYLosGraficos() {
        String md = GeneradorDeTablero.generar(defectos, AHORA);

        assertTrue(md.contains("| Abiertos / cerrados | 4 / 3 |"));
        assertTrue(md.contains("| Tiempo medio de resolución (MTTR) | 3.2 días |"));
        assertTrue(md.contains("| Tasa de reapertura | 50% |"));
        assertTrue(md.contains("| Críticos y altos abiertos | 2 |"));
        assertTrue(md.contains("```mermaid\npie title Defectos por severidad"));
        assertTrue(md.contains("xychart-beta"));
        assertTrue(md.contains("| #4 | Con el parqueadero lleno, el sistema asigna el puesto 0 | alta | critica | 5 |"));
        assertTrue(md.contains("**1** defecto(s) sin severidad o sin prioridad (no se pueden priorizar): #7."));
    }

    @Test
    void lasPorcionesEnCeroNoSeDibujan() {
        String md = GeneradorDeTablero.generar(
                defectos.stream().filter(d -> "alta".equals(d.severidad())).toList(), AHORA);

        assertFalse(md.contains("\"critica\" : 0"));
    }

    @Test
    void sinDefectosElTableroNoFalla() {
        String md = GeneradorDeTablero.generar(List.of(), AHORA);

        assertTrue(md.contains("No hay defectos abiertos."));
        assertTrue(md.contains("| Tiempo medio de resolución (MTTR) | sin cerrados |"));
    }
}
