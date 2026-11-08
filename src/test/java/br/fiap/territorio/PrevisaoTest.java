package br.fiap.territorio;

import br.fiap.territorio.dominio.EstrategiaPrevisao;
import br.fiap.territorio.dominio.PrevisaoComparacao;
import br.fiap.territorio.dominio.PrevisaoMediaMovel;
import br.fiap.territorio.excecao.DadosInvalidosException;
import br.fiap.territorio.servico.FabricaPrevisao;
import br.fiap.territorio.servico.TipoPrevisao;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrevisaoTest {

    @Test
    void mediaMovelDeveCalcularMediaDasUltimasLeituras() throws DadosInvalidosException {
        // Arrange
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(3);
        List<Double> historico = List.of(10.0, 20.0, 30.0, 40.0, 50.0);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert: media de 30, 40 e 50
        assertEquals(40.0, previsto, 0.0001);
    }

    @Test
    void mediaMovelDeveSeCalarComPoucoHistorico() throws DadosInvalidosException {
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(3);

        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> estrategia.prever(List.of(10.0, 20.0, 30.0)));

        assertEquals(true, erro.getMessage().contains("insuficiente"));
    }

    @Test
    void comparacaoDeveProjetarTendenciaDeAlta() throws DadosInvalidosException {
        // Arrange: periodo anterior (20,30) media 25; recente (40,50) media 45
        PrevisaoComparacao estrategia = new PrevisaoComparacao(5);
        List<Double> historico = List.of(10.0, 20.0, 30.0, 40.0, 50.0);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert: 45 + (45 - 25) = 65
        assertEquals(65.0, previsto, 0.0001);
    }

    @Test
    void comparacaoNaoDevePreverValorNegativo() throws DadosInvalidosException {
        PrevisaoComparacao estrategia = new PrevisaoComparacao(5);
        List<Double> historico = List.of(50.0, 40.0, 30.0, 5.0, 0.0);

        double previsto = estrategia.prever(historico);

        assertEquals(0.0, previsto, 0.0001);
    }

    @Test
    void fabricaDeveCriarAEstrategiaCerta() throws DadosInvalidosException {
        EstrategiaPrevisao media = FabricaPrevisao.criar(TipoPrevisao.MEDIA_MOVEL, 5);
        EstrategiaPrevisao comparacao = FabricaPrevisao.criar("comparacao", 5);

        assertInstanceOf(PrevisaoMediaMovel.class, media);
        assertInstanceOf(PrevisaoComparacao.class, comparacao);
    }

    @Test
    void fabricaDeveRejeitarTipoDesconhecido() {
        assertThrows(DadosInvalidosException.class, () -> FabricaPrevisao.criar("INEXISTENTE", 5));
    }
}
