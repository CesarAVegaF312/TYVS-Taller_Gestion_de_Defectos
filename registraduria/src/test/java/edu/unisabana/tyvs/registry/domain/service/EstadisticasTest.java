package edu.unisabana.tyvs.registry.domain.service;

import edu.unisabana.tyvs.registry.domain.model.RegisterResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EstadisticasTest {

    private final Estadisticas estadisticas = new Estadisticas();

    @Test
    void sinSolicitudesElPorcentajeEsCero() {
        assertEquals(0, estadisticas.porcentajeDeAprobados(List.of()));
    }

    @Test
    void siTodasSonValidasElPorcentajeEsCien() {
        List<RegisterResult> jornada = List.of(RegisterResult.VALID, RegisterResult.VALID);

        assertEquals(100, estadisticas.porcentajeDeAprobados(jornada));
    }

    @Test
    void siNingunaEsValidaElPorcentajeEsCero() {
        List<RegisterResult> jornada = List.of(RegisterResult.DEAD, RegisterResult.UNDERAGE);

        assertEquals(0, estadisticas.porcentajeDeAprobados(jornada));
    }
}
