package edu.unisabana.tyvs.registry.domain.service;

import edu.unisabana.tyvs.registry.domain.model.Gender;
import edu.unisabana.tyvs.registry.domain.model.Person;
import edu.unisabana.tyvs.registry.domain.model.RegisterResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComprobanteTest {

    private final Comprobante comprobante = new Comprobante();
    private final Person ana = new Person("Ana", 100, 30, Gender.FEMALE, true);

    @Test
    void elComprobanteDeUnaInscripcionIncluyeLosDatosDelVotante() {
        String texto = comprobante.generar(ana, RegisterResult.VALID, LocalDate.of(2026, 3, 3));

        assertTrue(texto.contains("Votante: Ana"));
        assertTrue(texto.contains("Documento: 100"));
        assertTrue(texto.contains("Fecha: 03/03/2026"));
    }

    @Test
    void elComprobanteDeUnRechazoDiceElMotivo() {
        String texto = comprobante.generar(ana, RegisterResult.UNDERAGE, LocalDate.of(2026, 3, 3));

        assertEquals("Inscripción rechazada: UNDERAGE", texto);
    }
}
