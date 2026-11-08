package br.fiap.territorio.excecao;

/**
 * Lancada quando a fonte de dados falha: sensor fora de operacao,
 * gerador de dados indisponivel etc.
 */
public class FonteIndisponivelException extends Exception {

    public FonteIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
