package br.fiap.territorio.servico;

import br.fiap.territorio.dominio.EstrategiaPrevisao;
import br.fiap.territorio.excecao.DadosInvalidosException;

import java.util.ArrayList;
import java.util.List;

/**
 * PADRAO FACTORY: concentra a CRIACAO das estrategias de previsao.
 * O resto do codigo pede "me de a estrategia X" e nao precisa saber
 * qual classe concreta e criada (nem repetir "new" por ai).
 */
public final class FabricaPrevisao {

    private FabricaPrevisao() {
    }

    public static EstrategiaPrevisao criar(TipoPrevisao tipo, int janela) throws DadosInvalidosException {
        return tipo.criar(janela);
    }

    /** Cria a partir de um texto (ex.: vindo de configuracao). */
    public static EstrategiaPrevisao criar(String nome, int janela) throws DadosInvalidosException {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("Informe o tipo de previsao.");
        }
        try {
            return criar(TipoPrevisao.valueOf(nome.trim().toUpperCase()), janela);
        } catch (IllegalArgumentException e) {
            throw new DadosInvalidosException("Tipo de previsao desconhecido: " + nome);
        }
    }

    /** Cria TODAS as estrategias existentes (polimorfismo: todas sao EstrategiaPrevisao). */
    public static List<EstrategiaPrevisao> todas(int janela) throws DadosInvalidosException {
        List<EstrategiaPrevisao> lista = new ArrayList<>();
        for (TipoPrevisao tipo : TipoPrevisao.values()) {
            lista.add(criar(tipo, janela));
        }
        return lista;
    }
}
