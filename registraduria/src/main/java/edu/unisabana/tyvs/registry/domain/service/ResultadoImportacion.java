package edu.unisabana.tyvs.registry.domain.service;

import java.util.List;

/**
 * Resultado de una importacion masiva (RF-09).
 *
 * @param inscritas       lineas cuya persona quedo inscrita (VALID)
 * @param rechazadas      lineas bien formadas cuya solicitud fue rechazada por una regla
 * @param erroresDeFormato una entrada por linea mal formada: "linea N: motivo"
 */
public record ResultadoImportacion(int inscritas, int rechazadas, List<String> erroresDeFormato) {
}
