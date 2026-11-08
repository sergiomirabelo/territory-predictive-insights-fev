package br.fiap.territorio.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Um alerta emitido. Tambem vira tabela no banco. */
@Entity
public class Alerta {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sensorId;

    @Enumerated(EnumType.STRING)
    private NivelAlerta nivel;

    private double valorPrevisto;
    private double limite;
    private String estrategia;

    @Column(length = 500)
    private String mensagem;

    private LocalDateTime dataHora;

    protected Alerta() {
    }

    public Alerta(String sensorId, NivelAlerta nivel, double valorPrevisto, double limite,
                  String estrategia, String mensagem) {
        this.sensorId = sensorId;
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limite = limite;
        this.estrategia = estrategia;
        this.mensagem = mensagem;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimite() {
        return limite;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return "[" + dataHora.format(FORMATO) + "] " + nivel + " - " + mensagem;
    }
}
