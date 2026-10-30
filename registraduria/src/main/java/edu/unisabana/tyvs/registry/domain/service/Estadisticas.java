package edu.unisabana.tyvs.registry.domain.service;

import edu.unisabana.tyvs.registry.domain.model.RegisterResult;

import java.util.List;

/** Estadisticas de una jornada de inscripcion (RF-10). */
public class Estadisticas {

    public int porcentajeDeAprobados(List<RegisterResult> resultados) {
        if (resultados.isEmpty()) {
            return 0;
        }
        long validos = resultados.stream().filter(r -> r == RegisterResult.VALID).count();
        return (int) (validos / resultados.size() * 100);
    }
}
