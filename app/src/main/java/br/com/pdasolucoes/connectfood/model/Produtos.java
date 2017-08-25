package br.com.pdasolucoes.connectfood.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by PDA on 20/02/2017.
 */

public class Produtos implements Serializable {

    private int id;
    private String categoria;
    private String nome;
    private int cod;
    private BigDecimal qtde;
    private BigDecimal qtdeAbsoluta;
    private int idFilial;
    private int unidMedida;
    private int status;
    private int finalizado;
    private int distribuido;
    private int idProgramacao;
    private String codRomaneio;
    private BigDecimal qtdeDigitada = new BigDecimal("0");

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public int getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(int idFilial) {
        this.idFilial = idFilial;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(int finalizado) {
        this.finalizado = finalizado;
    }

    public int getDistribuido() {
        return distribuido;
    }

    public void setDistribuido(int distribuido) {
        this.distribuido = distribuido;
    }

    public int getUnidMedida() {
        return unidMedida;
    }

    public void setUnidMedida(int unidMedida) {
        this.unidMedida = unidMedida;
    }

    public int getIdProgramacao() {
        return idProgramacao;
    }

    public void setIdProgramacao(int idProgramacao) {
        this.idProgramacao = idProgramacao;
    }

    public String getCodRomaneio() {
        return codRomaneio;
    }

    public void setCodRomaneio(String codRomaneio) {
        this.codRomaneio = codRomaneio;
    }

    public BigDecimal getQtde() {
        return qtde;
    }

    public void setQtde(BigDecimal qtde) {
        this.qtde = qtde;
    }

    public BigDecimal getQtdeAbsoluta() {
        return qtdeAbsoluta;
    }

    public void setQtdeAbsoluta(BigDecimal qtdeAbsoluta) {
        this.qtdeAbsoluta = qtdeAbsoluta;
    }

    public BigDecimal getQtdeDigitada() {
        return qtdeDigitada;
    }

    public void setQtdeDigitada(BigDecimal qtdeDigitada) {
        this.qtdeDigitada = qtdeDigitada;
    }
}
