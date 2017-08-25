package br.com.pdasolucoes.connectfood.model;

/**
 * Created by PDA on 20/02/2017.
 */
public class Usuario {

    private int id;
    private String usuario;
    private String senha;
    private String nomeUsuario;
    private int tipoInstituicao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public int getTipoInstituicao() {
        return tipoInstituicao;
    }

    public void setTipoInstituicao(int tipoInstituicao) {
        this.tipoInstituicao = tipoInstituicao;
    }
}
