package edu.unisabana.tyvs.registry.domain.service;

import edu.unisabana.tyvs.registry.domain.model.Gender;
import edu.unisabana.tyvs.registry.domain.model.Person;
import edu.unisabana.tyvs.registry.domain.model.RegisterResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Importacion masiva de solicitudes desde un archivo CSV (RF-09).
 *
 * Formato: una linea de encabezado y luego
 * {@code nombre;documento;edad;genero;vivo}.
 */
public class ImportadorCsv {

    private static final int CAMPOS = 5;

    private final Registry registry;

    public ImportadorCsv(Registry registry) {
        this.registry = registry;
    }

    public ResultadoImportacion importar(List<String> lineas) {
        int inscritas = 0;
        int rechazadas = 0;
        List<String> errores = new ArrayList<>();

        for (int i = 1; i < lineas.size(); i++) {
            String[] campos = lineas.get(i).split(";");
            if (campos.length != CAMPOS) {
                errores.add("linea " + (i + 1) + ": se esperaban " + CAMPOS + " campos");
                continue;
            }

            Person persona = new Person(
                    campos[0].trim(),
                    Integer.parseInt(campos[1]),
                    Integer.parseInt(campos[2]),
                    Gender.valueOf(campos[3]),
                    Boolean.parseBoolean(campos[4]));

            if (registry.registerVoter(persona) == RegisterResult.VALID) {
                inscritas++;
            } else {
                rechazadas++;
            }
        }
        return new ResultadoImportacion(inscritas, rechazadas, errores);
    }
}
