package br.com.pdasolucoes.connectfood.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewAnimationUtils;

import com.google.android.gms.ads.doubleclick.CustomRenderedAd;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.pdasolucoes.connectfood.MainActivity;
import br.com.pdasolucoes.connectfood.model.Produtos;

/**
 * Created by PDA on 21/02/2017.
 */

public class ProdutosDao {

    private DataBaseHelper helper;
    private SQLiteDatabase database;
    private Context context;

    public ProdutosDao(Context context) {
        this.context = context;
        helper = new DataBaseHelper(context);
    }

    public SQLiteDatabase getDataBase() {
        if (database == null) {
            helper = new DataBaseHelper(context);
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


    public void incluir(List<Produtos> lista) {

        ContentValues values = new ContentValues();
        for (Produtos p : lista) {
            if (existeProduto(p.getId()) == false) {
                values.put("id", p.getId());
                values.put("categoria", p.getCategoria());
                values.put("nome", p.getNome());
                values.put("cod", p.getCod());
                values.put("unidMedida", p.getUnidMedida());

                getDataBase().insert("produtos", null, values);
            }
        }

    }

    public long incluirNaLista(Produtos p) {

        ContentValues values = new ContentValues();

        values.put("categoria", p.getCategoria());
        values.put("qtde", String.valueOf(p.getQtde()));
        values.put("nome", p.getNome());
        values.put("cod", p.getCod());
        values.put("unidMedida", p.getUnidMedida());
        values.put("idDoador", p.getIdFilial());
        values.put("idProgramacao", p.getIdProgramacao());
        values.put("status", 0);
        values.put("finalizado", 0);
        values.put("distribuido", 0);
        values.put("codRomaneio", p.getCodRomaneio());
        return getDataBase().insert("produtosDoados", null, values);

    }

    public void incluirNaListaAux(List<Produtos> lista) {

        ContentValues values = new ContentValues();

        for (Produtos p : lista) {

            if (existeProdutoAux(p).size() > 0) {
                existeProdutoAux(p).get(0).getQtdeAbsoluta();
                existeProdutoAux(p).get(0).getId();

                BigDecimal somaAbsoluta = existeProdutoAux(p).get(0).getQtdeAbsoluta().add(p.getQtde());
                BigDecimal somaVariavel = existeProdutoAux(p).get(0).getQtde().add(p.getQtde());

                Produtos produtoAltera = new Produtos();
                produtoAltera.setId(existeProdutoAux(p).get(0).getId());
                produtoAltera.setQtdeAbsoluta(somaAbsoluta);
                produtoAltera.setQtde(somaVariavel);

                alterarAuxAbsoluto(produtoAltera);
                alterarAux(produtoAltera);
            } else {
                values.put("categoria", p.getCategoria());
                values.put("qtde", String.valueOf(p.getQtde()));
                values.put("qtdeAbsoluta", String.valueOf(p.getQtde()));
                values.put("nome", p.getNome());
                values.put("cod", p.getCod());
                values.put("unidMedida", p.getUnidMedida());
                values.put("idDoador", p.getIdFilial());
                values.put("idProgramacao", p.getIdProgramacao());
                values.put("status", 0);
                values.put("finalizado", 0);
                values.put("distribuido", 0);
                values.put("codRomaneio", p.getCodRomaneio());

                getDataBase().insert("produtosDoadosAux", null, values);
            }
        }

    }

    public long deleteAux() {
        return getDataBase().delete("produtosDoadosAux", "_id > 0", null);
    }

    public long delete() {
        return getDataBase().delete("produtosDoados", "_id > 0", null);
    }


    public List<Produtos> listarDoacao(int id, int idProgramacao) {
        List<Produtos> lista = new ArrayList<>();
        Cursor cursor = getDataBase().rawQuery("SELECT _id,cod, nome, SUM(qtde) as soma,categoria,status,finalizado,distribuido,unidMedida,idDoador FROM produtosDoados WHERE idDoador=? and idProgramacao=? GROUP BY cod", new String[]{id + "", idProgramacao + ""});
//        try {
        while (cursor.moveToNext()) {
            Produtos p = new Produtos();
            p.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            p.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            p.setCategoria(cursor.getString(cursor.getColumnIndex("categoria")));
            p.setCod(cursor.getInt(cursor.getColumnIndex("cod")));
            p.setQtde(new BigDecimal(cursor.getString(cursor.getColumnIndex("soma"))));
            p.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
            p.setUnidMedida(cursor.getInt(cursor.getColumnIndex("unidMedida")));
            p.setFinalizado(cursor.getInt(cursor.getColumnIndex("finalizado")));
            p.setDistribuido(cursor.getInt(cursor.getColumnIndex("distribuido")));
            p.setIdFilial(cursor.getInt(cursor.getColumnIndex("idDoador")));
            lista.add(p);
        }
//        } finally {
//            cursor.close();
//        }
        return lista;
    }

    public void deleteProdutoDoacao(int cod) {
        getDataBase().delete("produtosDoados", "cod" + "= ?", new String[]{cod + ""});
    }

    public boolean existeProduto(int id) {
        boolean existe = false;
        Cursor cursor = getDataBase().rawQuery("SELECT id FROM produtos where id = ?", new String[]{id + ""});
        try {

            while (cursor.moveToNext()) {
                existe = true;
            }
        } finally {
            cursor.close();
        }
        return existe;
    }


    public List<Produtos> existeProduto(Produtos p) {
        List<Produtos> lista = new ArrayList<>();

        Cursor cursor = getDataBase().rawQuery("SELECT _id,cod,nome,qtde,categoria,status,finalizado,distribuido,unidMedida" +
                " FROM produtosDoados WHERE idDoador = ? and idProgramacao = ? and cod= ?", new String[]{p.getIdFilial() + "", p.getIdProgramacao() + "", p.getCod() + ""});
        while (cursor.moveToNext()) {
            Produtos pr = new Produtos();
            pr.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            pr.setQtde(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtde"))));

            lista.add(pr);
        }
        return lista;
    }

    public List<Produtos> existeProdutoAux(Produtos p) {
        List<Produtos> lista = new ArrayList<>();

        Cursor cursor = getDataBase().rawQuery("SELECT _id,cod,nome,qtdeAbsoluta,qtde,categoria,status,finalizado,distribuido,unidMedida" +
                " FROM produtosDoadosAux WHERE cod= ?", new String[]{p.getCod() + ""});
        while (cursor.moveToNext()) {
            Produtos pr = new Produtos();
            pr.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            pr.setQtdeAbsoluta(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtdeAbsoluta"))));
            pr.setQtde(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtde"))));

            lista.add(pr);
        }
        return lista;
    }

    public long alterar(Produtos p) {

        ContentValues values = new ContentValues();
        values.put("qtde", String.valueOf(p.getQtde()));
        return getDataBase().update("produtosDoados", values, "_id=?", new String[]{p.getId() + ""});
    }

    public long alterarAux(Produtos p) {
        ContentValues values = new ContentValues();
        values.put("qtde", String.valueOf(p.getQtde()));
        return getDataBase().update("produtosDoadosAux", values, "_id=?", new String[]{p.getId() + ""});
    }

    public long alterarAuxAbsoluto(Produtos p) {
        ContentValues values = new ContentValues();
        values.put("qtdeAbsoluta", String.valueOf(p.getQtdeAbsoluta()));
        return getDataBase().update("produtosDoadosAux", values, "_id=?", new String[]{p.getId() + ""});
    }


    public long alterarDistribuicao(Produtos p) {

        ContentValues values = new ContentValues();
        values.put("distribuido", 1);
        return getDataBase().update("produtosDoados", values, "_id=?", new String[]{p.getId() + ""});
    }

    public long inserirCodRomaneio(Produtos p) {
        ContentValues values = new ContentValues();
        values.put("codRomaneio", p.getCodRomaneio());
        return getDataBase().update("produtosDoados", values, "_id=?", new String[]{p.getId() + ""});

    }

    public long alterarStatus(Produtos p) {

        ContentValues values = new ContentValues();
        values.put("status", 1);
        return getDataBase().update("produtosDoados", values, "_id=?", new String[]{p.getId() + ""});
    }

    public long alterarFinalizacao(Produtos p) {

        ContentValues values = new ContentValues();
        values.put("finalizado", 1);
        return getDataBase().update("produtosDoados", values, "_id=?", new String[]{p.getId() + ""});
    }

    public long alterarFinalizacaoAux(Produtos p) {

        ContentValues values = new ContentValues();
        values.put("finalizado", 1);
        return getDataBase().update("produtosDoadosAux", values, "_id=?", new String[]{p.getId() + ""});
    }


    public void deleteProduto(List<Integer> lista) {

        String args = TextUtils.join(",", lista);
        getDataBase().execSQL(String.format("DELETE FROM produtos where id NOT IN (%s)", args));
    }

    public List<Produtos> listar() {
        List<Produtos> lista = new ArrayList<>();
        Cursor cursor = getDataBase().rawQuery("SELECT id,nome,categoria,cod,unidMedida FROM produtos ", null);
        try {

            while (cursor.moveToNext()) {
                Produtos p = new Produtos();
                p.setId(cursor.getInt(cursor.getColumnIndex("id")));
                p.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                p.setCategoria(cursor.getString(cursor.getColumnIndex("categoria")));
                p.setCod(cursor.getInt(cursor.getColumnIndex("cod")));
                p.setUnidMedida(cursor.getInt(cursor.getColumnIndex("unidMedida")));
                //p.setIdFilial(cursor.getInt(cursor.getColumnIndex("idDoador")));

                lista.add(p);
            }
        } finally {
            cursor.close();
        }
        return lista;
    }

    public List<Produtos> listarTodosFilial(int idProgramacao) {

        List<Produtos> lista = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String data = sdf.format(new Date());
        Cursor cursor = getDataBase().rawQuery("SELECT idDoador,cod,_id,nome,qtde,categoria, unidMedida, pd.status,p.id, finalizado, distribuido FROM produtosDoados pd, programacao p " +
                "WHERE pd.finalizado=1 and p.id=pd.idProgramacao and p.dataSaida = ? and p.id = ?", new String[]{data, idProgramacao + ""});

        try {

            while (cursor.moveToNext()) {
                Produtos p = new Produtos();
                p.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                p.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                p.setCod(cursor.getInt(cursor.getColumnIndex("cod")));
                p.setQtde(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtde"))));
                p.setCategoria(cursor.getString(cursor.getColumnIndex("categoria")));
                p.setUnidMedida(cursor.getInt(cursor.getColumnIndex("unidMedida")));
                p.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                p.setFinalizado(cursor.getInt(cursor.getColumnIndex("finalizado")));
                p.setDistribuido(cursor.getInt(cursor.getColumnIndex("distribuido")));
                p.setIdFilial(cursor.getInt(cursor.getColumnIndex("idDoador")));
                p.setIdProgramacao(cursor.getInt(cursor.getColumnIndex("id")));
                lista.add(p);
            }
        } finally {
            cursor.close();
        }
        return lista;
    }

    public List<Produtos> listarTodosAuxProduto(int idMotorista) {

        List<Produtos> lista = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String data = sdf.format(new Date());
        Cursor cursor = getDataBase().rawQuery("SELECT cod,_id, nome,qtde,qtdeAbsoluta,categoria,distribuido,unidMedida FROM produtosDoadosAux, programacao p" +
                " WHERE p.dataSaida = ? and p.id = idProgramacao and p.idMotorista = ? and qtdeAbsoluta > 0 GROUP BY cod", new String[]{data + "", idMotorista + ""});

        try {

            while (cursor.moveToNext()) {
                Produtos p = new Produtos();
                p.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                p.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                p.setCategoria(cursor.getString(cursor.getColumnIndex("categoria")));
                p.setCod(cursor.getInt(cursor.getColumnIndex("cod")));
                p.setQtde(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtde"))));
                p.setQtdeAbsoluta(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtdeAbsoluta"))));
                p.setUnidMedida(cursor.getInt(cursor.getColumnIndex("unidMedida")));
                p.setDistribuido(cursor.getInt(cursor.getColumnIndex("distribuido")));
                lista.add(p);
            }
        } finally {
            cursor.close();
        }
        return lista;
    }

    public List<Produtos> listarTodosAuxFilial() {

        List<Produtos> lista = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String data = sdf.format(new Date());
        Cursor cursor = getDataBase().rawQuery("SELECT cod,_id, nome,qtde,qtdeAbsoluta,categoria,distribuido,unidMedida FROM produtosDoadosAux, programacao p WHERE p.dataSaida = ? and qtde > 0 GROUP BY cod", new String[]{data + ""});

        try {

            while (cursor.moveToNext()) {
                Produtos p = new Produtos();
                p.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                p.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                p.setCategoria(cursor.getString(cursor.getColumnIndex("categoria")));
                p.setCod(cursor.getInt(cursor.getColumnIndex("cod")));
                p.setQtde(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtde"))));
                p.setQtdeAbsoluta(new BigDecimal(cursor.getString(cursor.getColumnIndex("qtdeAbsoluta"))));
                p.setUnidMedida(cursor.getInt(cursor.getColumnIndex("unidMedida")));
                p.setDistribuido(cursor.getInt(cursor.getColumnIndex("distribuido")));
                lista.add(p);
            }
        } finally {
            cursor.close();
        }
        return lista;
    }

    public int ContadorProdutosDistribuir(int idMotorista) {
        int total = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String data = sdf.format(new Date());
        Cursor cursor = getDataBase().rawQuery("SELECT COUNT(cod) as conta FROM (SELECT cod FROM produtosDoadosAux pr, programacao p" +
                " WHERE dataSaida = ? and p.id = pr.idProgramacao and p.idMotorista  = ? and qtdeAbsoluta > 0 GROUP BY cod) x", new String[]{data + "", idMotorista + ""});

        try {

            while (cursor.moveToNext()) {
                total = cursor.getInt(cursor.getColumnIndex("conta"));
            }
        } finally {
            cursor.close();
        }

        return total;
    }


    public List<Produtos> buscarProdutoPorCategoria(String categoria) {
        List<Produtos> lista = new ArrayList<>();
        Cursor cursor = getDataBase().rawQuery("SELECT nome from produtos WHERE categoria LIKE ? ", new String[]{categoria});
        try {

            while (cursor.moveToNext()) {
                Produtos p = new Produtos();
                p.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                lista.add(p);

            }
        } finally {
            cursor.close();
        }
        return lista;
    }


    public int buscarUnidadePorCod(int cod) {
        int unidade = 0;
        Cursor cursor = getDataBase().rawQuery("SELECT unidMedida from produtos WHERE cod = ?",
                new String[]{cod + ""});
        try {
            while (cursor.moveToNext()) {
                unidade = cursor.getInt(cursor.getColumnIndex("unidMedida"));

            }
        } finally {
            cursor.close();
        }
        return unidade;
    }


    public List<Produtos> buscarProdutoPorCod2(int cod) {
        List<Produtos> lista = new ArrayList<>();
        Cursor cursor = getDataBase().rawQuery("SELECT categoria,nome,unidMedida from produtos WHERE cod = ?",
                new String[]{cod + ""});
        try {
            while (cursor.moveToNext()) {
                Produtos p = new Produtos();
                p.setCategoria(cursor.getString(cursor.getColumnIndex("categoria")));
                p.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                p.setUnidMedida(cursor.getInt(cursor.getColumnIndex("unidMedida")));
                lista.add(p);

            }
        } finally {
            cursor.close();
        }
        return lista;
    }

    public int buscarCodPorProduto(String produto) {
        int cod = 0;
        Cursor cursor = getDataBase().rawQuery("SELECT cod from produtos WHERE nome LIKE ? ", new String[]{produto});
        try {
            while (cursor.moveToNext()) {
                cod = cursor.getInt(cursor.getColumnIndex("cod"));


            }
        } finally {
            cursor.close();
        }
        return cod;
    }

    public float totalProdutosDoados(int idFilial, int idProgramacao) {
        float qtde = 0;
        Cursor cursor = getDataBase().rawQuery("SELECT SUM(soma) as total FROM (SELECT SUM(qtde) as soma FROM produtosDoados" +
                " WHERE idDoador=? and idProgramacao=? GROUP BY cod) X", new String[]{idFilial + "", idProgramacao + ""});

        try {
            while (cursor.moveToNext()) {
                qtde = cursor.getFloat(cursor.getColumnIndex("total"));
            }
        } finally {
            cursor.close();
        }

        return qtde;
    }

    public String buscarCodRomaneio(int idFilial) {
        String cod = "";
        Cursor cursor = getDataBase().rawQuery("SELECT codRomaneio FROM produtosDoados where idDoador = ? GROUP BY idDoador", new String[]{idFilial + ""});

        while (cursor.moveToNext()) {
            cod = cursor.getString(cursor.getColumnIndex("codRomaneio"));
        }

        return cod;
    }

}
