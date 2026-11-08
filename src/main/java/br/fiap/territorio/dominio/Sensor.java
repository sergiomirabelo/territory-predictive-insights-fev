package br.fiap.territorio.dominio;

import br.fiap.territorio.excecao.FonteIndisponivelException;

/**
 * INTERFACE: define o que todo sensor precisa saber fazer,
 * sem dizer COMO. Cada sensor concreto implementa do seu jeito.
 */
public interface Sensor {

    String getId();

    /** Tipo da grandeza medida, ex.: "CHUVA". */
    String getTipo();

    /** Unidade da medida, ex.: "mm/h". */
    String getUnidade();

    /** Menor valor que faz sentido para este sensor. */
    double getMinimo();

    /** Maior valor que faz sentido para este sensor. */
    double getMaximo();

    /** Le o valor atual. Lanca FonteIndisponivelException se o sensor falhou. */
    double lerValor() throws FonteIndisponivelException;

    /** Verifica funcionamento. */
    boolean estaFuncionando();

    /** Usado para simular falha (ou conserto) no sensor. */
    void definirFuncionamento(boolean funcionando);
}
