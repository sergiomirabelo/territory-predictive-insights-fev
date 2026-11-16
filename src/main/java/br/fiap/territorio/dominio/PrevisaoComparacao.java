package br.fiap.territorio.dominio;

import br.fiap.territorio.excecao.DadosInvalidosException;

import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao {

    public PrevisaoComparacao(int janela) throws DadosInvalidosException {
        super("Comparacao com periodo anterior", janela);
    }

    @Override
    public double prever(List<Double> historico) throws DadosInvalidosException {
        super.prever(historico); 
        int total = historico.size();
        int k = Math.min(janela, total / 2); 

        double recente = media(historico, total - k, total);
        double anterior = media(historico, total - 2 * k, total - k);

        double projecao = recente + (recente - anterior);
        return Math.max(0.0, projecao); 
    }
}
