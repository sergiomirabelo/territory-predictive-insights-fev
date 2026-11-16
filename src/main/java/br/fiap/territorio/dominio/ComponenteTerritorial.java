package br.fiap.territorio.dominio;

public abstract class ComponenteTerritorial {

    private final long id;
    private final String nome;
    private final String regiao;
    private StatusComponente status = StatusComponente.OPERANTE;

    protected ComponenteTerritorial(long id, String nome, String regiao) {
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
    }

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
