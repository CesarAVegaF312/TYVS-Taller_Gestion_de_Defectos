package edu.unisabana.tyvs.registry.domain.service;

import edu.unisabana.tyvs.registry.domain.model.Person;

/** Puerto de persistencia: donde quedan los votantes inscritos. */
public interface RegistroDeVotantes {

    boolean existe(int id);

    void guardar(Person persona);

    int total();
}
