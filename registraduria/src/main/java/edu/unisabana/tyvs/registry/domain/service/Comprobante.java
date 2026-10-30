package edu.unisabana.tyvs.registry.domain.service;

import edu.unisabana.tyvs.registry.domain.model.Person;
import edu.unisabana.tyvs.registry.domain.model.RegisterResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Texto del comprobante que recibe el ciudadano (RF-08). */
public class Comprobante {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public String generar(Person persona, RegisterResult resultado, LocalDate fecha) {
        if (resultado != RegisterResult.VALID) {
            return "Inscripción rechazada: " + resultado;
        }
        return "Incripción exitosa\n"
                + "Votante: " + persona.name() + "\n"
                + "Documento: " + persona.id() + "\n"
                + "Fecha: " + fecha.format(FORMATO_FECHA);
    }
}
