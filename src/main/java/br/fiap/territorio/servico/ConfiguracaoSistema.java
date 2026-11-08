package br.fiap.territorio.servico;

import br.fiap.territorio.dominio.DadosTerritorio;
import br.fiap.territorio.dominio.SensorNivelRio;
import br.fiap.territorio.dominio.SensorPluviometro;
import br.fiap.territorio.dominio.SensorUmidadeSolo;
import br.fiap.territorio.excecao.DadosInvalidosException;

/**
 * PADRAO SINGLETON: existe UMA unica instancia em todo o programa.
 * Guarda os dados do territorio (com os limiares) e a janela da previsao.
 * Construtor PRIVADO: ninguem faz "new ConfiguracaoSistema()".
 * Para pegar a instancia: ConfiguracaoSistema.getInstance().
 */
public final class ConfiguracaoSistema {

    private static final ConfiguracaoSistema INSTANCIA = new ConfiguracaoSistema();

    private DadosTerritorio dados;
    private int janelaPrevisao;

    private ConfiguracaoSistema() {
        restaurarPadroes();
    }

    public static ConfiguracaoSistema getInstance() {
        return INSTANCIA;
    }

    public DadosTerritorio getDados() {
        return dados;
    }

    public int getJanelaPrevisao() {
        return janelaPrevisao;
    }

    /** Volta aos valores iniciais (tambem usado nos testes). */
    public synchronized void restaurarPadroes() {
        try {
            DadosTerritorio novo = new DadosTerritorio("Territorio de Exemplo", "Zona Leste", 12000);
            novo.definirLimiares(SensorPluviometro.TIPO, 20, 40, 60);   // mm/h
            novo.definirLimiares(SensorNivelRio.TIPO, 150, 250, 350);   // cm
            novo.definirLimiares(SensorUmidadeSolo.TIPO, 60, 80, 95);   // %
            this.dados = novo;
            this.janelaPrevisao = 5;
        } catch (DadosInvalidosException e) {
            // Nao deve acontecer: os valores acima sao fixos e validos.
            throw new IllegalStateException("Configuracao padrao invalida: " + e.getMessage(), e);
        }
    }
}
