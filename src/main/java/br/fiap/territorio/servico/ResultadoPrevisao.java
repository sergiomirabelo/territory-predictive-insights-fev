package br.fiap.territorio.servico;

import br.fiap.territorio.dominio.NivelAlerta;

/**
 * Resultado de uma previsao: valor previsto + o que ele significa.
 * "nivel" e null quando nao ha alerta.
 */
public record ResultadoPrevisao(String sensorId, String estrategia, double valorPrevisto,
                                String unidade, NivelAlerta nivel, String explicacao) {
}
