package br.fiap.territorio.dominio;

import java.util.Random;

/** Sensor 2: nivel do corrego/rio em centimetros. */
public class SensorNivelRio extends SensorSimulado {

    public static final String TIPO = "NIVEL_RIO";

    public SensorNivelRio(String id) {
        this(id, new Random());
    }

    public SensorNivelRio(String id, Random random) {
        super(id, 50.0, 400.0, random); // simula de 50 a 400 cm
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    public String getUnidade() {
        return "cm";
    }

    @Override
    public double getMinimo() {
        return 0.0;
    }

    @Override
    public double getMaximo() {
        return 800.0;
    }
}
