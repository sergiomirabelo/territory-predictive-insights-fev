package br.fiap.territorio.dominio;

/**
 * Os tres limites de um tipo de sensor. "record" e um jeito curto do
 * Java 17 de criar uma classe so de dados (ja vem com getters).
 */
public record Limiar(double atencao, double alerta, double critico) {

    /** Devolve o valor do limite correspondente ao nivel. */
    public double valorDo(NivelAlerta nivel) {
        return switch (nivel) {
            case ATENCAO -> atencao;
            case ALERTA -> alerta;
            case CRITICO -> critico;
        };
    }
}
