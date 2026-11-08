package br.fiap.territorio.excecao;

/**
 * Lancada quando algum dado nao faz sentido:
 * medicao fora da faixa, populacao negativa, limiar invalido etc.
 */
public class DadosInvalidosException extends Exception {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
