package br.fiap.territorio.dominio;

import br.fiap.territorio.excecao.DadosInvalidosException;

import java.util.List;

/**
 * Classe ABSTRATA de previsao. As filhas (PrevisaoMediaMovel e
 * PrevisaoComparacao) SOBRESCREVEM prever() e chamam super.prever()
 * para reaproveitar a validacao feita aqui.
 *
 * O historico sempre chega em ORDEM CRONOLOGICA (mais antigo primeiro).
 */
public abstract class EstrategiaPrevisao {

    /** Menos que isso e "chute": o sistema se cala (ver reflexao no README). */
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

    /**
     * Versao base: valida o historico e devolve a ultima leitura
     * (a previsao mais simples possivel: "vai continuar igual").
     */
    public double prever(List<Double> historico) throws DadosInvalidosException {
        if (historico == null || historico.size() < MINIMO_LEITURAS) {
            int tem = historico == null ? 0 : historico.size();
            throw new DadosInvalidosException(
                    "Historico insuficiente: sao necessarias pelo menos " + MINIMO_LEITURAS
                            + " leituras para prever, mas existem " + tem + ".");
        }
        return historico.get(historico.size() - 1);
    }

    /** Media dos elementos de inicio (inclusive) ate fim (exclusivo). */
    protected static double media(List<Double> valores, int inicio, int fim) {
        double soma = 0;
        for (int i = inicio; i < fim; i++) {
            soma += valores.get(i);
        }
        return soma / (fim - inicio);
    }
}
