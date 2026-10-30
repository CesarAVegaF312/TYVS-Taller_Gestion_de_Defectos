package edu.unisabana.tyvs.tablero;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * Genera el tablero de metricas de defectos en Markdown, con graficos Mermaid
 * que GitHub dibuja directamente (en el repositorio y en el Wiki).
 *
 * Uso (desde registraduria/):
 *
 * <pre>
 * mvn -q compile exec:java -Dexec.args="../metricas/issues.json ../metricas/TABLERO.md"
 * </pre>
 *
 * Un tercer argumento opcional fija la fecha de corte (ISO-8601, por ejemplo
 * 2026-11-20T23:59:00Z). Sin el, se usa el instante actual. Con fecha de corte
 * el tablero de una entrega se puede regenerar y dar exactamente lo mismo.
 *
 * Los numeros se escriben con Locale.ROOT: el tablero no puede cambiar segun
 * el idioma del sistema operativo de quien lo genera (es el mismo defecto que
 * encontro el taller de Economia de las Pruebas en su analizador).
 */
public final class GeneradorDeTablero {

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm 'UTC'").withZone(ZoneOffset.UTC);

    private GeneradorDeTablero() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2 || args.length > 3) {
            System.err.println("Uso: GeneradorDeTablero <issues.json> <salida.md> [fecha-de-corte ISO-8601]");
            System.exit(2);
        }
        String json = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);
        Instant corte = args.length == 3 ? Instant.parse(args[2]) : Instant.now();
        String tablero = generar(LectorDeIssues.leer(json), corte);
        Path salida = Path.of(args[1]);
        if (salida.getParent() != null) {
            Files.createDirectories(salida.getParent());
        }
        Files.writeString(salida, tablero, StandardCharsets.UTF_8);
        System.out.println("Tablero generado en " + salida.toAbsolutePath());
    }

    /** Separado de main para poder probarlo con datos y una fecha fijos. */
    public static String generar(List<Defecto> defectos, Instant ahora) {
        MetricasDeDefectos m = new MetricasDeDefectos(defectos);
        StringBuilder md = new StringBuilder();

        md.append("# Tablero de defectos\n\n");
        md.append("Corte: ").append(FECHA.format(ahora))
          .append(" a partir de ").append(m.total()).append(" issues con la etiqueta `defecto`.\n\n");

        md.append("## Resumen\n\n");
        md.append("| Métrica | Valor | Pregunta que responde |\n|---|---|---|\n");
        md.append("| Defectos registrados | ").append(m.total()).append(" | ¿Cuántos encontramos? |\n");
        md.append("| Abiertos / cerrados | ").append(m.abiertos()).append(" / ").append(m.cerrados())
          .append(" | ¿Cuánto trabajo queda? |\n");
        md.append("| Tiempo medio de resolución (MTTR) | ").append(dias(m.tiempoMedioDeResolucionDias()))
          .append(" | ¿Cuánto tardamos en cerrar un defecto? |\n");
        md.append("| Tasa de reapertura | ").append(porcentaje(m.tasaDeReapertura()))
          .append(" | ¿Nuestras correcciones son confiables? |\n");
        md.append("| Críticos y altos abiertos | ").append(criticosYAltosAbiertos(defectos))
          .append(" | ¿Podemos entregar? |\n\n");

        md.append("## Defectos por severidad\n\n");
        md.append(pie("Defectos por severidad", m.porSeveridad()));
        md.append(tabla("Severidad", m.porSeveridad()));

        md.append("## Abiertos y cerrados\n\n");
        Map<String, Long> estado = new LinkedHashMap<>();
        estado.put("abiertos", m.abiertos());
        estado.put("cerrados", m.cerrados());
        md.append(pie("Estado", estado));

        md.append("## Defectos por módulo\n\n");
        md.append(barras("Defectos por módulo", m.porModulo()));
        md.append(tabla("Módulo", m.porModulo()));

        md.append("## Defectos por requisito\n\n");
        md.append(m.porRequisito().isEmpty()
                ? "Ningún defecto traza a un requisito.\n\n"
                : tabla("Requisito", m.porRequisito()));

        md.append("## Severidad y prioridad\n\n");
        md.append("| Severidad \\ Prioridad | ").append(String.join(" | ", MetricasDeDefectos.PRIORIDADES))
          .append(" |\n|---|").append("---|".repeat(MetricasDeDefectos.PRIORIDADES.size())).append('\n');
        m.matrizSeveridadPrioridad().forEach((sev, fila) -> md.append("| ").append(sev).append(" | ")
                .append(fila.values().stream().map(String::valueOf).collect(Collectors.joining(" | ")))
                .append(" |\n"));
        md.append('\n');

        md.append("## Backlog priorizado (abiertos)\n\n");
        List<Defecto> backlog = m.backlogPriorizado();
        if (backlog.isEmpty()) {
            md.append("No hay defectos abiertos.\n\n");
        } else {
            md.append("| # | Título | Prioridad | Severidad | Días abierto |\n|---|---|---|---|---|\n");
            backlog.forEach(d -> md.append("| #").append(d.numero()).append(" | ").append(escapar(d.titulo()))
                    .append(" | ").append(valor(d.prioridad())).append(" | ").append(valor(d.severidad()))
                    .append(" | ").append(MetricasDeDefectos.diasAbierto(d, ahora)).append(" |\n"));
            md.append('\n');
        }

        md.append("## Calidad del registro\n\n");
        md.append(advertencia(m.sinClasificar(), "sin severidad o sin prioridad", "no se pueden priorizar"));
        md.append(advertencia(m.sinTrazabilidad(), "sin requisito trazado", "no se puede validar su cierre"));
        return md.toString();
    }

    private static long criticosYAltosAbiertos(List<Defecto> defectos) {
        return defectos.stream()
                .filter(Defecto::abierto)
                .filter(d -> "critica".equals(d.severidad()) || "alta".equals(d.severidad()))
                .count();
    }

    private static String pie(String titulo, Map<String, Long> datos) {
        StringBuilder sb = new StringBuilder("```mermaid\npie title ").append(titulo).append('\n');
        // Mermaid dibuja mal las porciones en cero: se omiten.
        datos.forEach((k, v) -> {
            if (v > 0) {
                sb.append("    \"").append(k).append("\" : ").append(v).append('\n');
            }
        });
        return sb.append("```\n\n").toString();
    }

    private static String barras(String titulo, Map<String, Long> datos) {
        if (datos.isEmpty()) {
            return "";
        }
        long maximo = datos.values().stream().mapToLong(Long::longValue).max().orElse(1);
        return "```mermaid\nxychart-beta\n"
                + "    title \"" + titulo + "\"\n"
                + "    x-axis [" + datos.keySet().stream().map(k -> "\"" + k + "\"").collect(Collectors.joining(", ")) + "]\n"
                + "    y-axis \"Defectos\" 0 --> " + (maximo + 1) + "\n"
                + "    bar [" + datos.values().stream().map(String::valueOf).collect(Collectors.joining(", ")) + "]\n"
                + "```\n\n";
    }

    private static String tabla(String columna, Map<String, Long> datos) {
        StringBuilder sb = new StringBuilder("| ").append(columna).append(" | Defectos |\n|---|---|\n");
        datos.forEach((k, v) -> sb.append("| ").append(k).append(" | ").append(v).append(" |\n"));
        return sb.append('\n').toString();
    }

    private static String advertencia(List<Defecto> defectos, String problema, String consecuencia) {
        if (defectos.isEmpty()) {
            return "- Ningún defecto " + problema + ".\n";
        }
        return "- **" + defectos.size() + "** defecto(s) " + problema + " (" + consecuencia + "): "
                + defectos.stream().map(d -> "#" + d.numero()).collect(Collectors.joining(", ")) + ".\n";
    }

    private static String dias(OptionalDouble valor) {
        return valor.isPresent() ? String.format(Locale.ROOT, "%.1f días", valor.getAsDouble()) : "sin cerrados";
    }

    private static String porcentaje(OptionalDouble valor) {
        return valor.isPresent() ? String.format(Locale.ROOT, "%.0f%%", valor.getAsDouble() * 100) : "sin datos";
    }

    private static String valor(String v) {
        return v == null ? MetricasDeDefectos.SIN_CLASIFICAR : v;
    }

    private static String escapar(String texto) {
        return texto.replace("|", "\\|");
    }
}
