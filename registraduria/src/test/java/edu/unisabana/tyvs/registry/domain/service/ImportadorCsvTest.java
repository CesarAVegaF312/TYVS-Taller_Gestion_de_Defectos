package edu.unisabana.tyvs.registry.domain.service;

import edu.unisabana.tyvs.registry.infrastructure.RegistroEnMemoria;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImportadorCsvTest {

    private final ImportadorCsv importador = new ImportadorCsv(new Registry(new RegistroEnMemoria()));

    @Test
    void importaLasLineasBienFormadas() {
        List<String> csv = List.of(
                "nombre;documento;edad;genero;vivo",
                "Ana;200;30;FEMALE;true",
                "Luis;201;45;MALE;true");

        ResultadoImportacion resultado = importador.importar(csv);

        assertEquals(2, resultado.inscritas());
        assertEquals(0, resultado.rechazadas());
        assertEquals(List.of(), resultado.erroresDeFormato());
    }

    @Test
    void unaLineaConCamposFaltantesSeReportaYSeOmite() {
        List<String> csv = List.of(
                "nombre;documento;edad;genero;vivo",
                "Ana;200;30",
                "Luis;201;45;MALE;true");

        ResultadoImportacion resultado = importador.importar(csv);

        assertEquals(1, resultado.inscritas());
        assertEquals(1, resultado.erroresDeFormato().size());
    }
}
