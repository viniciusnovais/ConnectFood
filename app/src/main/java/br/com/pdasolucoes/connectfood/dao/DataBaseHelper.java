package br.com.pdasolucoes.connectfood.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PDA on 21/02/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String BANCO_DADOS = "connectfood";
    private static final int VERSAO = 4;


    public DataBaseHelper(Context context) {
        super(context, BANCO_DADOS, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE if not exists produtos(id INTEGER, categoria TEXT, nome TEXT,unidMedida INTEGER,cod INTEGER, idDoador INTEGER)");

        db.execSQL("CREATE TABLE if not exists produtosDoados(_id INTEGER PRIMARY KEY, categoria TEXT,unidMedida INTEGER, nome TEXT, qtde REAL," +
                " cod INTEGER, idDoador INTEGER, status INTEGER, finalizado INTEGER, distribuido INTEGER, idProgramacao INTEGER, codRomaneio TEXT)");

        db.execSQL("CREATE TABLE if not exists produtosDoadosAux(_id INTEGER PRIMARY KEY, categoria TEXT,unidMedida INTEGER, nome TEXT, qtde REAL, qtdeAbsoluta REAL," +
                " cod INTEGER, idDoador INTEGER, status INTEGER, finalizado INTEGER, distribuido INTEGER, idProgramacao INTEGER, codRomaneio TEXT)");

        db.execSQL("CREATE TABLE if not exists programacao(id INTEGER, dataSaida TEXT, horaSaida TEXT, dataChegada TEXT, horaChegada TEXT," +
                "razaoSocial TEXT, rua TEXT, tipo INTEGER, idFilial INTEGER, status INTEGER,idMotorista INTEGER, sync INTEGER, email TEXT, telefone TEXT, efetuada INTEGER)");

        db.execSQL("CREATE TABLE if not exists consulta(_id INTEGER PRIMARY KEY, idProgramacao INTEGER, FOREIGN KEY(idProgramacao) REFERENCES programacao(id))");

        db.execSQL("CREATE TABLE if not exists distribuicao(_id INTEGER PRIMARY KEY, qtde INTEGER, data TEXT, " +
                "codProduto INTEGER,assinado INTEGER,finalizado INTEGER,codRomaneio TEXT,idProgramacao INTEGER, idFilial INTEGER, FOREIGN KEY(codProduto) REFERENCES produtosDoadosAux(cod))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion != newVersion) {
            deleteDataBases(db);
            onCreate(db);
        }

    }

    private void deleteDataBases(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();

        while (cursor.moveToNext()) {
            if (!cursor.getString(0).equals("sqlite_sequence")) {
                tables.add(cursor.getString(0));
            }
        }

        for (String table : tables) {
            String dropQuery = "DROP TABLE IF EXISTS " + table;
            db.execSQL(dropQuery);
        }
    }
}

