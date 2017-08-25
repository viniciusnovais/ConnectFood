package br.com.pdasolucoes.connectfood.model;

/**
 * Created by PDA on 22/02/2017.
 */

public class Consulta {

    private int id;
    private Programacao idProgramacao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Programacao getIdProgramacao() {
        return idProgramacao;
    }

    public void setIdProgramacao(Programacao idProgramacao) {
        this.idProgramacao = idProgramacao;
    }
}
