package edu.unisabana.tyvs.registry.domain.model;

/** Resultados posibles de una solicitud de inscripcion (ver docs/especificacion.md). */
public enum RegisterResult {
    VALID, INVALID, DEAD, INVALID_AGE, UNDERAGE, DUPLICATED
}
