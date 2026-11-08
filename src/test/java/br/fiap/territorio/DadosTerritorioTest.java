package br.fiap.territorio;

import br.fiap.territorio.dominio.DadosTerritorio;
import br.fiap.territorio.excecao.DadosInvalidosException;
import br.fiap.territorio.servico.ConfiguracaoSistema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DadosTerritorioTest {

    @Test
    void deveRejeitarPopulacaoNegativa() throws DadosInvalidosException {
        DadosTerritorio dados = new DadosTerritorio("Bairro", "Zona Leste", 1000);

        assertThrows(DadosInvalidosException.class, () -> dados.setPopulacao(-1));
        assertEquals(1000, dados.getPopulacao()); // continua com o valor antigo
    }

    @Test
    void deveRejeitarLimiaresForaDeOrdem() throws DadosInvalidosException {
        DadosTerritorio dados = new DadosTerritorio("Bairro", "Zona Leste", 1000);

        assertThrows(DadosInvalidosException.class,
                () -> dados.definirLimiares("CHUVA", 50, 40, 60)); // atencao > alerta
    }

    @Test
    void deveAlterarLimiaresValidos() throws DadosInvalidosException {
        DadosTerritorio dados = new DadosTerritorio("Bairro", "Zona Leste", 1000);

        dados.definirLimiares("CHUVA", 10, 20, 30);

        assertEquals(20.0, dados.obterLimiares("CHUVA").alerta(), 0.0001);
    }

    @Test
    void configuracaoDeveSerSingleton() {
        ConfiguracaoSistema a = ConfiguracaoSistema.getInstance();
        ConfiguracaoSistema b = ConfiguracaoSistema.getInstance();

        assertSame(a, b);
    }
}
