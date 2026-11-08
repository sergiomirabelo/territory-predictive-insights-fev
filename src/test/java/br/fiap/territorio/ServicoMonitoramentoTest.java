package br.fiap.territorio;

import br.fiap.territorio.dominio.Alerta;
import br.fiap.territorio.dominio.Medicao;
import br.fiap.territorio.dominio.NivelAlerta;
import br.fiap.territorio.dominio.Sensor;
import br.fiap.territorio.dominio.SensorPluviometro;
import br.fiap.territorio.excecao.DadosInvalidosException;
import br.fiap.territorio.excecao.FonteIndisponivelException;
import br.fiap.territorio.persistencia.AlertaRepository;
import br.fiap.territorio.persistencia.MedicaoRepository;
import br.fiap.territorio.servico.ConfiguracaoSistema;
import br.fiap.territorio.servico.ResultadoPrevisao;
import br.fiap.territorio.servico.ServicoMonitoramento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Testa a logica de alerta SEM banco: os repositories sao falsos (Mockito).
 */
@ExtendWith(MockitoExtension.class)
class ServicoMonitoramentoTest {

    @Mock
    private MedicaoRepository medicaoRepository;

    @Mock
    private AlertaRepository alertaRepository;

    private ServicoMonitoramento servico;
    private Sensor pluviometro;

    @BeforeEach
    void preparar() {
        ConfiguracaoSistema.getInstance().restaurarPadroes(); // limiares: 20 / 40 / 60
        servico = new ServicoMonitoramento(medicaoRepository, alertaRepository, ConfiguracaoSistema.getInstance());
        pluviometro = new SensorPluviometro("PLUV-01");
    }

    private List<Medicao> historicoComValor(double valor, int quantidade) throws DadosInvalidosException {
        List<Medicao> lista = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            lista.add(new Medicao("PLUV-01", "CHUVA", valor, "mm/h"));
        }
        return lista;
    }

    @Test
    void deveEmitirAlertaCriticoQuandoPrevisaoPassaDoLimite() throws DadosInvalidosException {
        // Arrange: historico todo em 90 mm/h (limite critico = 60)
        when(medicaoRepository.findBySensorIdOrderByIdDesc(eq("PLUV-01"), any(Pageable.class)))
                .thenReturn(historicoComValor(90.0, 6));

        // Act
        List<ResultadoPrevisao> resultados = servico.preverSensor(pluviometro);

        // Assert: 2 estrategias -> 2 alertas gravados
        assertEquals(2, resultados.size());
        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertaRepository, times(2)).save(captor.capture());
        Alerta alerta = captor.getAllValues().get(0);
        assertEquals(NivelAlerta.CRITICO, alerta.getNivel());
        assertTrue(alerta.getMensagem().contains("PLUV-01"));   // explica o sensor
        assertTrue(alerta.getMensagem().contains("90,0"));      // explica o valor previsto
        assertTrue(alerta.getMensagem().contains("60,0"));      // explica o limite estourado
    }

    @Test
    void naoDeveEmitirAlertaQuandoPrevisaoEstaBaixa() throws DadosInvalidosException {
        when(medicaoRepository.findBySensorIdOrderByIdDesc(eq("PLUV-01"), any(Pageable.class)))
                .thenReturn(historicoComValor(5.0, 6));

        List<ResultadoPrevisao> resultados = servico.preverSensor(pluviometro);

        assertNull(resultados.get(0).nivel());
        verify(alertaRepository, never()).save(any(Alerta.class));
    }

    @Test
    void deveSeCalarQuandoHistoricoEPequeno() throws DadosInvalidosException {
        when(medicaoRepository.findBySensorIdOrderByIdDesc(eq("PLUV-01"), any(Pageable.class)))
                .thenReturn(historicoComValor(90.0, 3));

        assertThrows(DadosInvalidosException.class, () -> servico.preverSensor(pluviometro));
        verify(alertaRepository, never()).save(any(Alerta.class));
    }

    @Test
    void naoDeveGravarMedicaoForaDaFaixa() {
        // Pluviometro aceita de 0 a 200 mm/h
        assertThrows(DadosInvalidosException.class, () -> servico.registrar(pluviometro, 500.0));
        assertThrows(DadosInvalidosException.class, () -> servico.registrar(pluviometro, -1.0));

        verifyNoInteractions(medicaoRepository);
    }

    @Test
    void deveGravarMedicaoValida() throws DadosInvalidosException {
        servico.registrar(pluviometro, 33.3);

        verify(medicaoRepository, times(1)).save(any(Medicao.class));
    }

    @Test
    void naoDeveLerSensorComFalha() {
        pluviometro.definirFuncionamento(false);

        assertThrows(FonteIndisponivelException.class, () -> servico.lerERegistrar(pluviometro));
        verifyNoInteractions(medicaoRepository);
    }

    @Test
    void deveClassificarNiveisPeloValor() throws DadosInvalidosException {
        assertNull(servico.classificar("CHUVA", 10));
        assertEquals(NivelAlerta.ATENCAO, servico.classificar("CHUVA", 25));
        assertEquals(NivelAlerta.ALERTA, servico.classificar("CHUVA", 45));
        assertEquals(NivelAlerta.CRITICO, servico.classificar("CHUVA", 60));
    }
}
