package br.fiap.territorio.dominio;

import br.fiap.territorio.excecao.DadosInvalidosException;

import java.util.List;

public abstract class EstrategiaPrevisao {

    public static final int MINIMO_LEITURAS = 5;

    private final String nome;
    protected final int janela;

    protected EstrategiaPrevisao(String nome, int janela) throws DadosInvalidosException {
        if (janela < 2) {
            throw new DadosInvalidosException("A janela da previsao deve ser de pelo menos 2 leituras: " + janela);
        }
        this.nome = nome;
        this.janela = janela;
    }

    public String getNome() {
        return nome;
    }

    public double prever(List<Double> historico) throws DadosInvalidosException {
        if (historico == null || historico.size() < MINIMO_LEITURAS) {
            int tem = historico == null ? 0 : historico.size();
            throw new DadosInvalidosException(
                    "Historico insuficiente: sao necessarias pelo menos " + MINIMO_LEITURAS
                            + " leituras para prever, mas existem " + tem + ".");
        }
        return historico.get(historico.size() - 1);
    }

    protected static double media(List<Double> valores, int inicio, int fim) {
        double soma = 0;
        for (int i = inicio; i < fim; i++) {
            soma += valores.get(i);
        }
        return soma / (fim - inicio);
    }
}
