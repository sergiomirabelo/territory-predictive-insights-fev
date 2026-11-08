package br.fiap.territorio.dominio;

import br.fiap.territorio.excecao.DadosInvalidosException;

import java.util.List;

/** Previsao 1: media das ultimas N leituras ("a media recente"). */
public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    public PrevisaoMediaMovel(int janela) throws DadosInvalidosException {
        super("Media Movel", janela);
    }

    @Override
    public double prever(List<Double> historico) throws DadosInvalidosException {
        super.prever(historico); // valida (lanca excecao se faltar historico)
        int n = Math.min(janela, historico.size());
        return media(historico, historico.size() - n, historico.size());
    }
}
