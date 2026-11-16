package br.fiap.territorio.servico;

import br.fiap.territorio.dominio.Alerta;
import br.fiap.territorio.dominio.EstrategiaPrevisao;
import br.fiap.territorio.dominio.Limiar;
import br.fiap.territorio.dominio.Medicao;
import br.fiap.territorio.dominio.NivelAlerta;
import br.fiap.territorio.dominio.PontoMonitorado;
import br.fiap.territorio.dominio.Sensor;
import br.fiap.territorio.dominio.SensorNivelRio;
import br.fiap.territorio.dominio.SensorPluviometro;
import br.fiap.territorio.dominio.SensorUmidadeSolo;
import br.fiap.territorio.excecao.DadosInvalidosException;
import br.fiap.territorio.excecao.FonteIndisponivelException;
import br.fiap.territorio.persistencia.AlertaRepository;
import br.fiap.territorio.persistencia.MedicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ServicoMonitoramento {

    private static final Locale BR = Locale.forLanguageTag("pt-BR");

    private final MedicaoRepository medicaoRepository;
    private final AlertaRepository alertaRepository;
    private final ConfiguracaoSistema configuracao;
    private final PontoMonitorado ponto;

    @Autowired
    public ServicoMonitoramento(MedicaoRepository medicaoRepository, AlertaRepository alertaRepository) {
        this(medicaoRepository, alertaRepository, ConfiguracaoSistema.getInstance());
    }

    public ServicoMonitoramento(MedicaoRepository medicaoRepository, AlertaRepository alertaRepository,
                                ConfiguracaoSistema configuracao) {
        this.medicaoRepository = medicaoRepository;
        this.alertaRepository = alertaRepository;
        this.configuracao = configuracao;

        List<Sensor> sensores = List.of(
                new SensorPluviometro("PLUV-01"),
                new SensorNivelRio("RIO-01"),
                new SensorUmidadeSolo("SOLO-01"));
        this.ponto = new PontoMonitorado(1L, "Ponto do Corrego (trecho que alaga)",
                configuracao.getDados().getRegiao(), sensores);
    }


    public List<Sensor> getSensores() {
        return ponto.getSensores();
    }

    public PontoMonitorado getPonto() {
        return ponto;
    }

    public ConfiguracaoSistema getConfiguracao() {
        return configuracao;
    }

    public Medicao lerERegistrar(Sensor sensor) throws DadosInvalidosException, FonteIndisponivelException {
        double valor = sensor.lerValor(); 
        return registrar(sensor, valor);
    }

    public Medicao registrar(Sensor sensor, double valor) throws DadosInvalidosException {
        if (Double.isNaN(valor) || valor < sensor.getMinimo() || valor > sensor.getMaximo()) {
            throw new DadosInvalidosException(String.format(BR,
                    "Medicao de %s fora da faixa valida (%.1f a %.1f %s): %.1f",
                    sensor.getTipo(), sensor.getMinimo(), sensor.getMaximo(), sensor.getUnidade(), valor));
        }
        Medicao medicao = new Medicao(sensor.getId(), sensor.getTipo(), valor, sensor.getUnidade());
        return medicaoRepository.save(medicao);
    }

    public List<Medicao> ultimasMedicoes(String sensorId, int quantidade) {
        List<Medicao> recentes = new ArrayList<>(
                medicaoRepository.findBySensorIdOrderByIdDesc(sensorId, PageRequest.of(0, quantidade)));
        Collections.reverse(recentes);
        return recentes;
    }

    public List<Alerta> alertasEmitidos() {
        return alertaRepository.findAllByOrderByIdDesc();
    }

    public List<ResultadoPrevisao> preverSensor(Sensor sensor) throws DadosInvalidosException {
        int janela = configuracao.getJanelaPrevisao();
        List<Double> historico = new ArrayList<>();
        for (Medicao m : ultimasMedicoes(sensor.getId(), janela * 2)) {
            historico.add(m.getValor());
        }

        List<ResultadoPrevisao> resultados = new ArrayList<>();
        for (EstrategiaPrevisao estrategia : FabricaPrevisao.todas(janela)) {
            double previsto = estrategia.prever(historico); 
            resultados.add(avaliar(sensor, estrategia.getNome(), previsto));
        }
        return resultados;
    }

    private ResultadoPrevisao avaliar(Sensor sensor, String estrategia, double previsto)
            throws DadosInvalidosException {
        NivelAlerta nivel = classificar(sensor.getTipo(), previsto);
        Limiar limiar = configuracao.getDados().obterLimiares(sensor.getTipo());

        if (nivel == null) {
            String texto = String.format(BR,
                    "Valor previsto %.1f %s esta abaixo do limite de ATENCAO (%.1f %s). Sem alerta.",
                    previsto, sensor.getUnidade(), limiar.atencao(), sensor.getUnidade());
            return new ResultadoPrevisao(sensor.getId(), estrategia, previsto, sensor.getUnidade(), null, texto);
        }

        double limite = limiar.valorDo(nivel);
        String mensagem = String.format(BR,
                "Sensor %s (%s): valor previsto %.1f %s pela estrategia '%s' ultrapassou o limite de %s (%.1f %s).",
                sensor.getId(), sensor.getTipo(), previsto, sensor.getUnidade(),
                estrategia, nivel, limite, sensor.getUnidade());
        alertaRepository.save(new Alerta(sensor.getId(), nivel, previsto, limite, estrategia, mensagem));
        return new ResultadoPrevisao(sensor.getId(), estrategia, previsto, sensor.getUnidade(), nivel, mensagem);
    }

    public NivelAlerta classificar(String tipoSensor, double valor) throws DadosInvalidosException {
        Limiar limiar = configuracao.getDados().obterLimiares(tipoSensor);
        if (valor >= limiar.critico()) {
            return NivelAlerta.CRITICO;
        }
        if (valor >= limiar.alerta()) {
            return NivelAlerta.ALERTA;
        }
        if (valor >= limiar.atencao()) {
            return NivelAlerta.ATENCAO;
        }
        return null;
    }


    public String statusCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("Territorio: ").append(configuracao.getDados().getNome())
                .append(" | Regiao: ").append(configuracao.getDados().getRegiao())
                .append(" | Populacao: ").append(configuracao.getDados().getPopulacao()).append("\n");
        sb.append("Ponto monitorado: ").append(ponto.getNome())
                .append(" -> ").append(ponto.verificarStatus()).append("\n");
        for (Sensor s : getSensores()) {
            sb.append("  - ").append(s.getId()).append(" (").append(s.getTipo()).append("): ")
                    .append(s.estaFuncionando() ? "funcionando" : "FORA DE OPERACAO").append("\n");
        }
        sb.append("Limiares (atencao / alerta / critico):\n");
        for (Map.Entry<String, Limiar> e : configuracao.getDados().getLimiares().entrySet()) {
            Limiar l = e.getValue();
            sb.append(String.format(BR, "  - %s: %.1f / %.1f / %.1f%n", e.getKey(), l.atencao(), l.alerta(), l.critico()));
        }
        sb.append("Janela da previsao: ").append(configuracao.getJanelaPrevisao()).append(" leituras\n");
        sb.append("Medicoes gravadas: ").append(medicaoRepository.count()).append("\n");
        sb.append("Alertas emitidos: ").append(alertaRepository.count());
        return sb.toString();
    }
}
