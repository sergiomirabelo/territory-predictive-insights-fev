package br.fiap.territorio.dominio;

import br.fiap.territorio.excecao.DadosInvalidosException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Uma leitura de sensor. @Entity = esta classe vira uma TABELA no banco.
 * Cada objeto Medicao vira uma LINHA.
 */
@Entity
public class Medicao {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sensorId;
    private String tipoSensor;
    private double valor;
    private String unidade;
    private LocalDateTime dataHora;

    /** Exigido pelo JPA (nao usamos direto). */
    protected Medicao() {
    }

    public Medicao(String sensorId, String tipoSensor, double valor, String unidade)
            throws DadosInvalidosException {
        if (sensorId == null || sensorId.isBlank()) {
            throw new DadosInvalidosException("A medicao precisa de um sensor de origem.");
        }
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new DadosInvalidosException("Medicao invalida (nao e um numero): " + valor);
        }
        if (valor < 0) {
            throw new DadosInvalidosException(
                    "Medicao de " + tipoSensor + " nao pode ser negativa: " + valor);
        }
        this.sensorId = sensorId;
        this.tipoSensor = tipoSensor;
        this.valor = valor;
        this.unidade = unidade;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public double getValor() {
        return valor;
    }

    public String getUnidade() {
        return unidade;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s = %.1f %s", dataHora.format(FORMATO), sensorId, valor, unidade);
    }
}
