package edu.unisabana.tyvs.registry.domain.service;

import edu.unisabana.tyvs.registry.domain.model.Person;
import edu.unisabana.tyvs.registry.domain.model.RegisterResult;

/**
 * Caso de uso: inscribir a una persona como votante.
 *
 * Las reglas estan en docs/especificacion.md (RF-01 a RF-07). Esa
 * especificacion es la referencia: si este codigo y la especificacion no
 * dicen lo mismo, el que esta mal es el codigo.
 */
public class Registry {

    public static final int MIN_AGE = 18;
    public static final int MAX_AGE = 120;

    private final RegistroDeVotantes registro;

    public Registry(RegistroDeVotantes registro) {
        this.registro = registro;
    }

    public RegisterResult registerVoter(Person p) {
        if (p == null || p.name() == null || p.name().isBlank() || p.id() < 0) {
            return RegisterResult.INVALID;
        }

        RegisterResult resultado = evaluarReglas(p);
        if (resultado == RegisterResult.VALID && registro.existe(p.id())) {
            return RegisterResult.DUPLICATED;
        }

        registro.guardar(p);
        return resultado;
    }

    private RegisterResult evaluarReglas(Person p) {
        if (p.age() < 0 || p.age() > MAX_AGE) {
            return RegisterResult.INVALID_AGE;
        }
        if (p.age() <= MIN_AGE) {
            return RegisterResult.UNDERAGE;
        }
        if (!p.alive()) {
            return RegisterResult.DEAD;
        }
        return RegisterResult.VALID;
    }
}
