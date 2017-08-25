package br.com.pdasolucoes.connectfood.model;

import java.math.BigDecimal;

/**
 * Created by PDA on 07/03/2017.
 */

public class Distribuicao {

    private int id;
    private BigDecimal qtde;
    private int idFilial;
    private Produtos produtos;
    private int finalizado;
    private int assinado;
    private int codProduto;
    private String codRomaneio;
    private int idProgramacao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getQtde() {
        return qtde;
    }

    public void setQtde(BigDecimal qtde) {
        this.qtde = qtde;
    }

    public int getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(int idFilial) {
        this.idFilial = idFilial;
    }

    public Produtos getProdutos() {
        return produtos;
    }

    public void setProdutos(Produtos produtos) {
        this.produtos = produtos;
    }

    public int getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(int finalizado) {
        this.finalizado = finalizado;
    }

    public int getAssinado() {
        return assinado;
    }

    public void setAssinado(int assinado) {
        this.assinado = assinado;
    }

    public int getCodProduto() {
        return codProduto;
    }

    public void setCodProduto(int codProduto) {
        this.codProduto = codProduto;
    }

    public String getCodRomaneio() {
        return codRomaneio;
    }

    public void setCodRomaneio(String codRomaneio) {
        this.codRomaneio = codRomaneio;
    }

    public int getIdProgramacao() {
        return idProgramacao;
    }

    public void setIdProgramacao(int idProgramacao) {
        this.idProgramacao = idProgramacao;
    }
}
