package edu.unisabana.tyvs.tablero;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.TreeMap;

/**
 * Las metricas minimas del taller. Cada una responde una pregunta concreta;
 * una metrica que no responde ninguna pregunta es decoracion.
 */
public class MetricasDeDefectos {

    public static final List<String> SEVERIDADES = List.of("critica", "alta", "media", "baja");
    public static final List<String> PRIORIDADES = List.of("alta", "media", "baja");
    public static final String SIN_CLASIFICAR = "sin clasificar";

    private final List<Defecto> defectos;

    public MetricasDeDefectos(List<Defecto> defectos) {
        this.defectos = List.copyOf(defectos);
    }

    public int total() {
        return defectos.size();
    }

    /** 1. ¿Que tan graves son los defectos? */
    public Map<String, Long> porSeveridad() {
        return contarEnOrden(SEVERIDADES, Defecto::severidad);
    }

    /** 2. ¿Cuanto trabajo queda pendiente? */
    public long abiertos() {
        return defectos.stream().filter(Defecto::abierto).count();
    }

    public long cerrados() {
        return total() - abiertos();
    }

    /** 3. ¿Cuanto tarda el equipo en cerrar un defecto? (MTTR, en dias) */
    public OptionalDouble tiempoMedioDeResolucionDias() {
        return defectos.stream()
                .filter(d -> !d.abierto() && d.cerrado() != null)
                .mapToDouble(d -> d.tiempoDeResolucion().toMinutes() / (60.0 * 24))
                .average();
    }

    /**
     * 4. ¿Las correcciones son confiables? Reabiertos sobre los defectos que
     * se cerraron al menos una vez (los cerrados y los abiertos que fueron
     * reabiertos).
     */
    public OptionalDouble tasaDeReapertura() {
        long reabiertos = defectos.stream().filter(Defecto::reabierto).count();
        long cerradosAlgunaVez = defectos.stream().filter(d -> !d.abierto() || d.reabierto()).count();
        return cerradosAlgunaVez == 0 ? OptionalDouble.empty() : OptionalDouble.of((double) reabiertos / cerradosAlgunaVez);
    }

    /** 5a. ¿Donde se concentran? Por modulo. */
    public Map<String, Long> porModulo() {
        Map<String, Long> conteo = new TreeMap<>();
        defectos.forEach(d -> conteo.merge(Objects.requireNonNullElse(d.modulo(), SIN_CLASIFICAR), 1L, Long::sum));
        return conteo;
    }

    /** 5b. ¿Que requisitos fallan mas? Un defecto puede trazar a varios. */
    public Map<String, Long> porRequisito() {
        Map<String, Long> conteo = new TreeMap<>();
        defectos.forEach(d -> d.requisitos().forEach(r -> conteo.merge(r, 1L, Long::sum)));
        return conteo;
    }

    /** Severidad x prioridad: la base de la priorizacion. */
    public Map<String, Map<String, Long>> matrizSeveridadPrioridad() {
        Map<String, Map<String, Long>> matriz = new LinkedHashMap<>();
        for (String s : SEVERIDADES) {
            Map<String, Long> fila = new LinkedHashMap<>();
            PRIORIDADES.forEach(p -> fila.put(p, 0L));
            matriz.put(s, fila);
        }
        defectos.stream()
                .filter(d -> pertenece(SEVERIDADES, d.severidad()) && pertenece(PRIORIDADES, d.prioridad()))
                .forEach(d -> matriz.get(d.severidad()).merge(d.prioridad(), 1L, Long::sum));
        return matriz;
    }

    /** Abiertos en orden de atencion: prioridad, luego severidad, luego antiguedad. */
    public List<Defecto> backlogPriorizado() {
        return defectos.stream()
                .filter(Defecto::abierto)
                .sorted(Comparator.comparingInt((Defecto d) -> rango(PRIORIDADES, d.prioridad()))
                        .thenComparingInt(d -> rango(SEVERIDADES, d.severidad()))
                        .thenComparing(Defecto::creado))
                .toList();
    }

    /** Calidad del propio registro: un defecto sin clasificar no se puede priorizar. */
    public List<Defecto> sinClasificar() {
        return defectos.stream()
                .filter(d -> d.severidad() == null || d.prioridad() == null)
                .toList();
    }

    /** Un defecto que no traza a ningun requisito no se puede validar. */
    public List<Defecto> sinTrazabilidad() {
        return defectos.stream().filter(d -> d.requisitos().isEmpty()).toList();
    }

    public static long diasAbierto(Defecto d, Instant ahora) {
        return Duration.between(d.creado(), ahora).toDays();
    }

    private Map<String, Long> contarEnOrden(List<String> orden, java.util.function.Function<Defecto, String> clave) {
        Map<String, Long> conteo = new LinkedHashMap<>();
        orden.forEach(o -> conteo.put(o, 0L));
        conteo.put(SIN_CLASIFICAR, 0L);
        defectos.forEach(d -> {
            String valor = clave.apply(d);
            conteo.merge(pertenece(orden, valor) ? valor : SIN_CLASIFICAR, 1L, Long::sum);
        });
        return conteo;
    }

    private static int rango(List<String> orden, String valor) {
        return pertenece(orden, valor) ? orden.indexOf(valor) : orden.size();
    }

    /**
     * List.of(...).contains(null) lanza NullPointerException en vez de
     * devolver false, y un defecto sin clasificar tiene severidad null.
     */
    private static boolean pertenece(List<String> orden, String valor) {
        return valor != null && orden.contains(valor);
    }
}
