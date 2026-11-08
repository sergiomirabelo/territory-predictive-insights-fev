package br.fiap.territorio.dominio;

import br.fiap.territorio.excecao.DadosInvalidosException;

import java.util.List;

/**
 * Previsao 2: compara o periodo recente com o periodo anterior e
 * projeta a TENDENCIA. Se a media subiu de 25 para 45, preve 65.
 */
public class PrevisaoComparacao extends EstrategiaPrevisao {

    public PrevisaoComparacao(int janela) throws DadosInvalidosException {
        super("Comparacao com periodo anterior", janela);
    }

    @Override
    public double prever(List<Double> historico) throws DadosInvalidosException {
        super.prever(historico); // valida (lanca excecao se faltar historico)
        int total = historico.size();
        int k = Math.min(janela, total / 2); // tamanho de cada periodo

        double recente = media(historico, total - k, total);
        double anterior = media(historico, total - 2 * k, total - k);

        double projecao = recente + (recente - anterior);
        return Math.max(0.0, projecao); // medicao nunca e negativa
    }
}
