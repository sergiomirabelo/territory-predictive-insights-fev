package br.fiap.territorio.dominio;

import java.util.Random;

/** Sensor 1: intensidade de chuva em mm/h. */
public class SensorPluviometro extends SensorSimulado {

    public static final String TIPO = "CHUVA";

    public SensorPluviometro(String id) {
        this(id, new Random());
    }

    public SensorPluviometro(String id, Random random) {
        super(id, 0.0, 80.0, random); // simula de 0 a 80 mm/h
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    public String getUnidade() {
        return "mm/h";
    }

    @Override
    public double getMinimo() {
        return 0.0;
    }

    @Override
    public double getMaximo() {
        return 200.0;
    }
}
