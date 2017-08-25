package br.com.pdasolucoes.connectfood.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.pdasolucoes.connectfood.model.Distribuicao;
import br.com.pdasolucoes.connectfood.model.Produtos;
import br.com.pdasolucoes.connectfood.model.Programacao;

/**
 * Created by PDA on 07/03/2017.
 */

public class DistribuicaoDao {

    private DataBaseHelper helper;
    private SQLiteDatabase database;

    public DistribuicaoDao(Context context) {
        helper = new DataBaseHelper(context);
    }

    public SQLiteDatabase getDataBase() {
        if (database == null) {
            database = helper.getWritableDatabase();
        }

        return database;
    }

    public void close() {
        helper.close();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    public long incluir(Distribuicao d) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        ContentValues values = new ContentValues();


        values.put("qtde", String.valueOf(d.getQtde()));
        values.put("idFilial", d.getIdFilial());
        values.put("idProgramacao", d.getIdProgramacao());
        values.put("codProduto", d.getProdutos().getCod());
        values.put("data", sdf.format(new Date()));
        values.put("assinado", 0);
        values.put("finalizado", 0);
        values.put("codRomaneio", d.getCodRomaneio());

        return getDataBase().insert("distribuicao", null, values);
    }

    public long alterar(Distribuicao d) {

        ContentValues values = new ContentValues();
        values.put("qtde", String.valueOf(d.getQtde()));
        return getDataBase().update("distribuicao", values, "_id=?", new String[]{d.getId() + ""});
    }

    public long alterarStatusFinalizado(Distribuicao d) {

        ContentValues values = new ContentValues();
        values.put("finalizado", 1);
        return getDataBase().update("distribuicao", values, "_id=?", new String[]{d.getId() + ""});
    }

    public long alterarStatusAssinado(Distribuicao d) {

        ContentValues values = new ContentValues();
        values.put("assinado", 1);
        return getDataBase().update("distribuicao", values, "_id=?", new String[]{d.getId() + ""});
    }

    public long inserirCodRomaneio(Distribuicao d) {
        ContentValues values = new ContentValues();
        values.put("codRomaneio", d.getCodRomaneio());
        return getDataBase().update("distribuicao", values, "_id=?", new String[]{d.getId() + ""});

    }

    public long delete() {
        return getDataBase().delete("distribuicao", "_id > 0", null);
    }


    public List<Distribuicao> listarIdParaAlterar(int codProduto) {
        List<Distribuicao> lista = new ArrayList<>();
        Cursor cursor = getDataBase().rawQuery("SELECT _id FROM distribuicao" +
                " WHERE finalizado = 0 and codProduto = ? GROUP BY _id", new String[]{codProduto + ""});
        try {
            while (cursor.moveToNext()) {
                Distribuicao d = new Distribuicao();

                d.setId(cursor.getInt(cursor.getColumnIndex("_id")));

                lista.add(d);
            }
        } finally {
            cursor.close();
        }
        return lista;
    }

    public List<Distribuicao> existeProdutoDistribuido(int idFilial, int codProduto) {
        List<Distribuicao> lista = new ArrayList<>();

        Cursor cursor = getDataBase().rawQuery("SELECT _id,qtde,codProduto FROM distribuicao WHERE idFilial = ? and codProduto = ?", new String[]{idFilial + "", codProduto + ""});
        while (cursor.moveToNext()) {
            Distribuicao d = new Distribuicao();
            d.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            d.setQtde(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtde"))));
            d.setCodProduto(cursor.getInt(cursor.getColumnIndex("codProduto")));

            lista.add(d);
        }
        return lista;
    }

    public boolean existeAlgumProdutoDistribuido(int idFilial, int idProgramacao) {
        List<Distribuicao> lista = new ArrayList<>();

        Cursor cursor = getDataBase().rawQuery("SELECT * FROM distribuicao WHERE idFilial = ? and idProgramacao = ? and qtde > 0", new String[]{idFilial + "", idProgramacao + ""});
        while (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    public long deletarDistribuicao(int idDistribuicao) {
        return getDataBase().delete("distribuicao", "_id=?", new String[]{idDistribuicao + ""});
    }


    public List<Distribuicao> listarDistribuicaoFilial(int idFilial, int idProgramacao) {
        List<Distribuicao> lista = new ArrayList<>();
        Cursor cursor = getDataBase().rawQuery("SELECT p.unidMedida,d.idFilial,d._id, d.qtde,d.codProduto,p._id as _idProduto, p.nome,p.cod,p.categoria,d.assinado,d.finalizado,p.qtde as qtdeProduto,p.qtdeAbsoluta" +
                " FROM distribuicao d, produtosDoadosAux p WHERE d.idFilial= ? and d.codProduto = p.cod and d.qtde > 0 and d.idProgramacao = ?", new String[]{idFilial + "", idProgramacao + ""});
        try {
            while (cursor.moveToNext()) {
                Distribuicao d = new Distribuicao();

                d.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                d.setQtde(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtde"))));
                d.setAssinado(cursor.getInt(cursor.getColumnIndex("assinado")));
                d.setFinalizado(cursor.getInt(cursor.getColumnIndex("finalizado")));
                d.setCodProduto(cursor.getInt(cursor.getColumnIndex("codProduto")));
                d.setIdFilial(cursor.getInt(cursor.getColumnIndex("idFilial")));
                Produtos p = new Produtos();
                p.setCod(cursor.getInt(cursor.getColumnIndex("cod")));
                p.setId(cursor.getInt(cursor.getColumnIndex("_idProduto")));
                p.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                p.setCategoria(cursor.getString(cursor.getColumnIndex("categoria")));
                p.setUnidMedida(cursor.getInt(cursor.getColumnIndex("unidMedida")));
                p.setQtde(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtdeProduto"))));
                p.setQtdeAbsoluta(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtdeAbsoluta"))));
                d.setProdutos(p);

                lista.add(d);
            }
        } finally {
            cursor.close();
        }
        return lista;
    }

    public List<Distribuicao> listarDistribuicaoFilial(int idFilial) {
        List<Distribuicao> lista = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Cursor cursor = getDataBase().rawQuery("SELECT d.idFilial,d._id, d.qtde,d.codProduto,p._id as _idProduto, p.nome,p.cod,p.categoria,d.assinado,d.finalizado,p.qtde as qtdeProduto,p.qtdeAbsoluta" +
                " FROM distribuicao d, produtosDoadosAux p WHERE d.idFilial= ? and d.codProduto = p.cod and d.qtde > 0", new String[]{idFilial + ""});
        try {
            while (cursor.moveToNext()) {
                Distribuicao d = new Distribuicao();

                d.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                d.setQtde(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtde"))));
                d.setAssinado(cursor.getInt(cursor.getColumnIndex("assinado")));
                d.setFinalizado(cursor.getInt(cursor.getColumnIndex("finalizado")));
                d.setCodProduto(cursor.getInt(cursor.getColumnIndex("codProduto")));
                d.setIdFilial(cursor.getInt(cursor.getColumnIndex("idFilial")));
                Produtos p = new Produtos();
                p.setCod(cursor.getInt(cursor.getColumnIndex("cod")));
                p.setId(cursor.getInt(cursor.getColumnIndex("_idProduto")));
                p.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                p.setCategoria(cursor.getString(cursor.getColumnIndex("categoria")));
                p.setQtde(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtdeProduto"))));
                p.setQtdeAbsoluta(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtdeAbsoluta"))));
                d.setProdutos(p);

                lista.add(d);
            }
        } finally {
            cursor.close();
        }
        return lista;
    }

    public int buscarQtdeFinalizado(int codProduto) {
        int qtde = 0;
        Cursor cursor = getDataBase().rawQuery("SELECT SUM(qtde) as soma FROM distribuicao d  " +
                "WHERE finalizado = 1 and d.codProduto = ? ", new String[]{codProduto + ""});
        try {
            while (cursor.moveToNext()) {
                Distribuicao d = new Distribuicao();

                qtde = cursor.getInt(cursor.getColumnIndex("soma"));

            }
        } finally {
            cursor.close();
        }
        return qtde;
    }

    public float totalDistribuidoInstituicao(int idFilial, int idProgramacao) {
        float qtde = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String data = sdf.format(new Date());
        Cursor cursor = getDataBase().rawQuery("SELECT SUM(qtdeProduto) as soma FROM (SELECT d.qtde as qtdeProduto FROM distribuicao d, produtosDoadosAux p, programacao pr WHERE d.idFilial= ?" +
                " and d.idProgramacao = ? and d.codProduto = p.cod and d.qtde > 0 and pr.dataSaida = ? GROUP BY p.cod) X", new String[]{idFilial + "", idProgramacao + "", data});

        try {
            while (cursor.moveToNext()) {
                qtde = cursor.getFloat(cursor.getColumnIndex("soma"));
            }
        } finally {
            cursor.close();
        }

        return qtde;
    }

    public String buscarCodRomaneio(int idFilial, int idProgramacao) {
        String codRomaneio = "";
        Cursor cursor = getDataBase().rawQuery("SELECT codRomaneio FROM distribuicao where idFilial = ? and idProgramacao = ? GROUP BY idFilial", new String[]{idFilial + "", idProgramacao + ""});

        while (cursor.moveToNext()) {
            codRomaneio = cursor.getString(cursor.getColumnIndex("codRomaneio"));
        }
        return codRomaneio;
    }
}
