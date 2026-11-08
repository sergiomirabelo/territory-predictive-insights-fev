package br.fiap.territorio.dominio;

import br.fiap.territorio.excecao.FonteIndisponivelException;

import java.util.Random;

/**
 * Base comum dos sensores deste trabalho: os valores sao SIMULADOS
 * com Random, dentro de uma faixa realista. As filhas so informam
 * tipo, unidade e faixa valida (HERANCA evita repetir codigo).
 */
public abstract class SensorSimulado implements Sensor {

    private final String id;
    private final double simulacaoMin;
    private final double simulacaoMax;
    private final Random random;
    private boolean funcionando = true;

    protected SensorSimulado(String id, double simulacaoMin, double simulacaoMax, Random random) {
        this.id = id;
        this.simulacaoMin = simulacaoMin;
        this.simulacaoMax = simulacaoMax;
        this.random = random;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (!funcionando) {
            throw new FonteIndisponivelException(
                    "Sensor " + id + " esta fora de operacao: nao foi possivel ler.");
        }
        double bruto = simulacaoMin + random.nextDouble() * (simulacaoMax - simulacaoMin);
        return Math.round(bruto * 10.0) / 10.0; // 1 casa decimal
    }

    @Override
    public boolean estaFuncionando() {
        return funcionando;
    }

    @Override
    public void definirFuncionamento(boolean funcionando) {
        this.funcionando = funcionando;
    }
}
