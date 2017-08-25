package br.com.pdasolucoes.connectfood.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.pdasolucoes.connectfood.model.Consulta;
import br.com.pdasolucoes.connectfood.model.Programacao;

/**
 * Created by PDA on 22/02/2017.
 */

public class ConsultaDao {

    private DataBaseHelper helper;
    private SQLiteDatabase database;

    public ConsultaDao(Context context) {
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

    public long incluir(Programacao p) {
        ContentValues values = new ContentValues();
        values.put("idProgramacao", p.getId());
        Log.w("incluiu", "consulta");
        return getDataBase().insert("consulta", null, values);
    }

    public List<Consulta> listar(int idMotorista) {
        List<Consulta> lista = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String data = sdf.format(new Date());
        Cursor cursor = getDataBase().rawQuery("SELECT c._id, c.idProgramacao,p.idMotorista,p.email, p.razaoSocial, p.id, p.tipo,p.dataSaida,p.horaSaida,p.idFilial " +
                "FROM consulta c, programacao p WHERE c.idProgramacao = p.id and p.idMotorista= ? and dataSaida = ? ORDER BY p.tipo", new String[]{idMotorista + "", data});
        try {
            while (cursor.moveToNext()) {


                Consulta c = new Consulta();
                c.setId(cursor.getInt(cursor.getColumnIndex("_id")));

                Programacao p = new Programacao();

                p.setId(cursor.getInt(cursor.getColumnIndex("id")));
                p.setDataSaida(cursor.getString(cursor.getColumnIndex("dataSaida")));
                p.setHoraSaida(cursor.getString(cursor.getColumnIndex("horaSaida")));
                p.setRazaoSocial(cursor.getString(cursor.getColumnIndex("razaoSocial")));
                p.setTipo(cursor.getInt(cursor.getColumnIndex("tipo")));
                p.setIdFilial(cursor.getInt(cursor.getColumnIndex("idFilial")));
                p.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                c.setIdProgramacao(p);

                lista.add(c);
            }
        } finally {
            cursor.close();
        }
        return lista;
    }

    public long delete() {
        return getDataBase().delete("consulta", "_id>0", null);
    }

    public int ContarConsulta(int idMotorista) {
        int contador = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String data = sdf.format(new Date());
        Cursor cursor = getDataBase().rawQuery("SELECT COUNT(c._id) as contador,p.idMotorista FROM consulta c, programacao p WHERE c.idProgramacao=p.id and idMotorista = ? and dataSaida = ?", new String[]{idMotorista + "", data});
        try {
            while (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex("idMotorista")) == idMotorista) {
                    contador = cursor.getInt(cursor.getColumnIndex("contador"));
                }
            }
        } finally

        {
            cursor.close();
        }

        return contador;
    }

}
