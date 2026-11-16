package br.fiap.territorio.servico;

import br.fiap.territorio.dominio.NivelAlerta;

public record ResultadoPrevisao(String sensorId, String estrategia, double valorPrevisto,
                                String unidade, NivelAlerta nivel, String explicacao) {
}
