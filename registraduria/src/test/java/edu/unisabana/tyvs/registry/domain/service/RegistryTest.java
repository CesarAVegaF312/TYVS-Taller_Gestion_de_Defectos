package edu.unisabana.tyvs.registry.domain.service;

import edu.unisabana.tyvs.registry.domain.model.Gender;
import edu.unisabana.tyvs.registry.domain.model.Person;
import edu.unisabana.tyvs.registry.domain.model.RegisterResult;
import edu.unisabana.tyvs.registry.infrastructure.RegistroEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Suite de partida. Esta en verde, y aun asi el codigo tiene defectos.
 *
 * Eso no es una contradiccion: una suite verde solo dice que los casos que
 * alguien penso funcionan. Encontrar los que nadie penso es el ejercicio.
 */
class RegistryTest {

    private Registry registry;

    @BeforeEach
    void setUp() {
        registry = new Registry(new RegistroEnMemoria());
    }

    @Test
    void personaAdultaVivaQuedaInscrita() {
        Person ana = new Person("Ana", 100, 30, Gender.FEMALE, true);

        assertEquals(RegisterResult.VALID, registry.registerVoter(ana));
    }

    @Test
    void menorDeEdadEsRechazado() {
        Person nino = new Person("Luis", 101, 10, Gender.MALE, true);

        assertEquals(RegisterResult.UNDERAGE, registry.registerVoter(nino));
    }

    @Test
    void personaFallecidaEsRechazada() {
        Person fallecido = new Person("Pedro", 102, 40, Gender.MALE, false);

        assertEquals(RegisterResult.DEAD, registry.registerVoter(fallecido));
    }

    @Test
    void edadImposibleEsRechazada() {
        Person imposible = new Person("Eva", 103, 150, Gender.FEMALE, true);

        assertEquals(RegisterResult.INVALID_AGE, registry.registerVoter(imposible));
    }

    @Test
    void documentoRepetidoEsRechazado() {
        Person ana = new Person("Ana", 104, 30, Gender.FEMALE, true);
        registry.registerVoter(ana);

        assertEquals(RegisterResult.DUPLICATED, registry.registerVoter(ana));
    }

    @Test
    void solicitudSinPersonaEsInvalida() {
        assertEquals(RegisterResult.INVALID, registry.registerVoter(null));
    }

    @Test
    void nombreEnBlancoEsInvalido() {
        Person sinNombre = new Person("  ", 105, 30, Gender.FEMALE, true);

        assertEquals(RegisterResult.INVALID, registry.registerVoter(sinNombre));
    }
}
