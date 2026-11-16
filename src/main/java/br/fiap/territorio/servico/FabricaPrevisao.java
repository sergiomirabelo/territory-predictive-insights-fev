package br.fiap.territorio.servico;

import br.fiap.territorio.dominio.EstrategiaPrevisao;
import br.fiap.territorio.excecao.DadosInvalidosException;

import java.util.ArrayList;
import java.util.List;

public final class FabricaPrevisao {

    private FabricaPrevisao() {
    }

    public static EstrategiaPrevisao criar(TipoPrevisao tipo, int janela) throws DadosInvalidosException {
        return tipo.criar(janela);
    }

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

    public static List<EstrategiaPrevisao> todas(int janela) throws DadosInvalidosException {
        List<EstrategiaPrevisao> lista = new ArrayList<>();
        for (TipoPrevisao tipo : TipoPrevisao.values()) {
            lista.add(criar(tipo, janela));
        }
        return lista;
    }
}
