package br.fiap.territorio.dominio;

/**
 * Classe ABSTRATA: representa qualquer "pedaco" do territorio que
 * monitoramos (um ponto de alagamento, uma UBS, uma estacao etc.).
 * Nao da para criar um ComponenteTerritorial direto: so as filhas.
 */
public abstract class ComponenteTerritorial {

    // ENCAPSULAMENTO: atributos privados, acesso so por getters
    private final long id;
    private final String nome;
    private final String regiao;
    private StatusComponente status = StatusComponente.OPERANTE;

    protected ComponenteTerritorial(long id, String nome, String regiao) {
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
    }

    /** Metodo ABSTRATO: cada filha decide como verifica o proprio status. */
    public abstract StatusComponente verificarStatus();

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public StatusComponente getStatus() {
        return status;
    }

    /** protected: so as filhas conseguem mudar o status. */
    protected void setStatus(StatusComponente status) {
        this.status = status;
    }
}
