package br.fiap.territorio.dominio;

import java.util.List;

public class PontoMonitorado extends ComponenteTerritorial {

    private final List<Sensor> sensores;

    public PontoMonitorado(long id, String nome, String regiao, List<Sensor> sensores) {
        super(id, nome, regiao);
        this.sensores = sensores;
    }

    public List<Sensor> getSensores() {
        return sensores;
    }

    @Override
    public StatusComponente verificarStatus() {
        long funcionando = sensores.stream().filter(Sensor::estaFuncionando).count();
        StatusComponente novo;
        if (funcionando == sensores.size()) {
            novo = StatusComponente.OPERANTE;
        } else if (funcionando == 0) {
            novo = StatusComponente.FORA_DE_OPERACAO;
        } else {
            novo = StatusComponente.DEGRADADO;
        }
        setStatus(novo);
        return novo;
    }
}
