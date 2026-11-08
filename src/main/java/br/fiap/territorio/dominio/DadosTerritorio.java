package br.fiap.territorio.dominio;

import br.fiap.territorio.excecao.DadosInvalidosException;

import java.util.HashMap;
import java.util.Map;

/**
 * Dados do territorio (descrito por regiao, NUNCA por pessoas).
 * Atributos PRIVADOS: so mudam pelos metodos abaixo, que VALIDAM.
 * Assim ninguem coloca populacao negativa ou limiares sem sentido.
 */
public class DadosTerritorio {

    private String nome;
    private String regiao;
    private long populacao;
    private final Map<String, Limiar> limiares = new HashMap<>();

    public DadosTerritorio(String nome, String regiao, long populacao) throws DadosInvalidosException {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome do territorio nao pode ser vazio.");
        }
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("A regiao nao pode ser vazia.");
        }
        this.nome = nome;
        this.regiao = regiao;
        setPopulacao(populacao);
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public long getPopulacao() {
        return populacao;
    }

    public void setPopulacao(long populacao) throws DadosInvalidosException {
        if (populacao < 0) {
            throw new DadosInvalidosException("Populacao nao pode ser negativa: " + populacao);
        }
        this.populacao = populacao;
    }

    /** Codigo de acesso para ALTERAR limiares (com validacao). */
    public void definirLimiares(String tipoSensor, double atencao, double alerta, double critico)
            throws DadosInvalidosException {
        if (tipoSensor == null || tipoSensor.isBlank()) {
            throw new DadosInvalidosException("Informe o tipo de sensor do limiar.");
        }
        if (atencao <= 0) {
            throw new DadosInvalidosException("Limiar de ATENCAO deve ser maior que zero: " + atencao);
        }
        if (!(atencao < alerta && alerta < critico)) {
            throw new DadosInvalidosException(
                    "Limiares invalidos: precisa ser ATENCAO < ALERTA < CRITICO, mas recebi "
                            + atencao + " / " + alerta + " / " + critico);
        }
        limiares.put(tipoSensor, new Limiar(atencao, alerta, critico));
    }

    public Limiar obterLimiares(String tipoSensor) throws DadosInvalidosException {
        Limiar limiar = limiares.get(tipoSensor);
        if (limiar == null) {
            throw new DadosInvalidosException("Nao ha limiares cadastrados para o tipo: " + tipoSensor);
        }
        return limiar;
    }

    /** Copia somente leitura, para exibir no menu. */
    public Map<String, Limiar> getLimiares() {
        return Map.copyOf(limiares);
    }
}
