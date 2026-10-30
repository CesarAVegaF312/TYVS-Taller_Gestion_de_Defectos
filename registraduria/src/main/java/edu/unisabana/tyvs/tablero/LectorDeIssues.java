package edu.unisabana.tyvs.tablero;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lee la exportacion de GitHub Issues que produce:
 *
 * <pre>
 * gh issue list --state all --limit 1000 \
 *   --json number,title,state,labels,createdAt,closedAt,body
 * </pre>
 *
 * Solo cuenta los issues con la etiqueta {@code defecto}: un repositorio
 * tambien tiene tareas, preguntas y mejoras, y mezclarlos falsea todas las
 * metricas.
 */
public final class LectorDeIssues {

    /** RF-03, RNF-01, R4...: lo que el issue form pide en "Requisito o caso que incumple". */
    private static final Pattern REQUISITO = Pattern.compile("\\b(R[A-Z]*-?\\d+)\\b");

    private LectorDeIssues() {
    }

    public static List<Defecto> leer(String json) throws IOException {
        JsonNode issues = new ObjectMapper().readTree(json);
        List<Defecto> defectos = new ArrayList<>();
        for (JsonNode issue : issues) {
            List<String> etiquetas = new ArrayList<>();
            issue.path("labels").forEach(l -> etiquetas.add(l.path("name").asText()));
            if (!etiquetas.contains("defecto")) {
                continue;
            }
            defectos.add(new Defecto(
                    issue.path("number").asInt(),
                    issue.path("title").asText(),
                    "OPEN".equalsIgnoreCase(issue.path("state").asText()),
                    valorDe(etiquetas, "severidad:"),
                    valorDe(etiquetas, "prioridad:"),
                    valorDe(etiquetas, "modulo:"),
                    requisitos(issue.path("body").asText("")),
                    Instant.parse(issue.path("createdAt").asText()),
                    fecha(issue.path("closedAt")),
                    etiquetas.contains("reabierto")));
        }
        return defectos;
    }

    private static String valorDe(List<String> etiquetas, String prefijo) {
        return etiquetas.stream()
                .filter(e -> e.startsWith(prefijo))
                .map(e -> e.substring(prefijo.length()))
                .findFirst()
                .orElse(null);
    }

    private static List<String> requisitos(String cuerpo) {
        Set<String> encontrados = new LinkedHashSet<>();
        Matcher m = REQUISITO.matcher(cuerpo);
        while (m.find()) {
            encontrados.add(m.group(1));
        }
        return List.copyOf(encontrados);
    }

    private static Instant fecha(JsonNode nodo) {
        if (nodo.isMissingNode() || nodo.isNull() || nodo.asText().isBlank()) {
            return null;
        }
        // gh exporta "0001-01-01T00:00:00Z" en closedAt para los abiertos.
        Instant instante = Instant.parse(nodo.asText());
        return instante.isBefore(Instant.parse("1970-01-01T00:00:00Z")) ? null : instante;
    }
}
