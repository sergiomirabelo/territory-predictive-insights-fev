package br.fiap.territorio.dominio;

import java.util.Random;

/** Sensor 3: umidade do solo em porcentagem (encosta saturada = risco). */
public class SensorUmidadeSolo extends SensorSimulado {

    public static final String TIPO = "UMIDADE_SOLO";

    public SensorUmidadeSolo(String id) {
        this(id, new Random());
    }

    public SensorUmidadeSolo(String id, Random random) {
        super(id, 30.0, 100.0, random); // simula de 30 a 100 %
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    public String getUnidade() {
        return "%";
    }

    @Override
    public double getMinimo() {
        return 0.0;
    }

    @Override
    public double getMaximo() {
        return 100.0;
    }
}
