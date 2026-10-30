package edu.unisabana.tyvs.registry.infrastructure;

import edu.unisabana.tyvs.registry.domain.model.Person;
import edu.unisabana.tyvs.registry.domain.service.RegistroDeVotantes;

import java.util.HashMap;
import java.util.Map;

/** Adaptador en memoria del puerto de persistencia. */
public class RegistroEnMemoria implements RegistroDeVotantes {

    private final Map<Integer, Person> votantes = new HashMap<>();

    @Override
    public boolean existe(int id) {
        return votantes.containsKey(id);
    }

    @Override
    public void guardar(Person persona) {
        votantes.put(persona.id(), persona);
    }

    @Override
    public int total() {
        return votantes.size();
    }
}
