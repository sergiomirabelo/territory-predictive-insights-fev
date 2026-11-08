package br.fiap.territorio.servico;

import br.fiap.territorio.dominio.EstrategiaPrevisao;
import br.fiap.territorio.dominio.PrevisaoComparacao;
import br.fiap.territorio.dominio.PrevisaoMediaMovel;
import br.fiap.territorio.excecao.DadosInvalidosException;

/**
 * Cada tipo sabe criar a SUA estrategia. Por isso a fabrica nao precisa
 * de "if (tipo == ...)": basta chamar tipo.criar(janela).
 */
public enum TipoPrevisao {

    MEDIA_MOVEL {
        @Override
        public EstrategiaPrevisao criar(int janela) throws DadosInvalidosException {
            return new PrevisaoMediaMovel(janela);
        }
    },

    COMPARACAO {
        @Override
        public EstrategiaPrevisao criar(int janela) throws DadosInvalidosException {
            return new PrevisaoComparacao(janela);
        }
    };

    public abstract EstrategiaPrevisao criar(int janela) throws DadosInvalidosException;
}
