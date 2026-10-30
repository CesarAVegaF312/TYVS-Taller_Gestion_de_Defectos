package edu.unisabana.tyvs.registry.domain.model;

/** Persona que solicita la inscripcion como votante. */
public record Person(String name, int id, int age, Gender gender, boolean alive) {
}
