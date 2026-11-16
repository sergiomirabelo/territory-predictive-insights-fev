package br.fiap.territorio.dominio;

public record Limiar(double atencao, double alerta, double critico) {

    public double valorDo(NivelAlerta nivel) {
        return switch (nivel) {
            case ATENCAO -> atencao;
            case ALERTA -> alerta;
            case CRITICO -> critico;
        };
    }
}
