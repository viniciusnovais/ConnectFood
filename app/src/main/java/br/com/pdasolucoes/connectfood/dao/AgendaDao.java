package br.com.pdasolucoes.connectfood.dao;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.pdasolucoes.connectfood.model.Programacao;

/**
 * Created by PDA on 21/02/2017.
 */

public class AgendaDao {

    private DataBaseHelper helper;
    private SQLiteDatabase database;

    public AgendaDao(Context context) {
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


    public void incluir(List<Programacao> lista) {
        ContentValues values = new ContentValues();
        for (Programacao prog : lista) {
            if (existeProgramacao(prog.getId()) == false && prog.getStatus() == 0) {
                values.put("id", prog.getId());
                values.put("dataSaida", prog.getDataSaida());
                values.put("horaSaida", prog.getHoraSaida());
                values.put("dataChegada", prog.getDataChegada());
                values.put("horaChegada", prog.getHoraChegada());
                values.put("razaoSocial", prog.getRazaoSocial());
                values.put("rua", prog.getRua());
                values.put("tipo", prog.getTipo());
                values.put("status", prog.getStatus());
                values.put("idFilial", prog.getIdFilial());
                values.put("idMotorista", prog.getIdMotorista());
                values.put("email", prog.getEmail());
                values.put("sync", 0);
                values.put("telefone", prog.getTelefone());
                values.put("efetuada", 0);
                getDataBase().insert("programacao", null, values);
            }
        }
    }

    public boolean existeProgramacao(int id) {
        boolean existe = false;
        Cursor cursor = getDataBase().rawQuery("SELECT id FROM programacao where id = ?", new String[]{id + ""});
        try {

            while (cursor.moveToNext()) {
                Log.w("existe", "existe");
                existe = true;
            }
        } finally {
            cursor.close();
        }
        return existe;
    }

    public String maiorData() {
        String data = "";
        Cursor cursor = getDataBase().rawQuery("SELECT MAX(dataSaida) as maiorData FROM programacao", null);
        try {
            while (cursor.moveToNext()) {
                data = cursor.getString(cursor.getColumnIndex("maiorData"));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return data;
    }

    public long delete() {
        return getDataBase().delete("programacao", "id > 0", null);
    }

    public long alterarProg(int id) {
        ContentValues values = new ContentValues();
        values.put("status", 1);
        return getDataBase().update("programacao", values, "id=" + id, null);
    }

    public long alterarEfetuada(int id) {
        ContentValues values = new ContentValues();
        values.put("efetuada", 1);
        return getDataBase().update("programacao", values, "id=" + id, null);
    }
    public long alterarNãoEfetuada(int id) {
        ContentValues values = new ContentValues();
        values.put("efetuada", 0);
        return getDataBase().update("programacao", values, "id=" + id, null);
    }

    public long alterarDataHoraFim(int id, String dataFim, String horaFim) {
        ContentValues values = new ContentValues();
        values.put("dataChegada", dataFim);
        values.put("horaChegada", horaFim);
        return getDataBase().update("programacao", values, "id=" + id, null);
    }

    public long alterarDataHoraInicio(int id, String dataInicio, String horaInicio) {
        ContentValues values = new ContentValues();
        values.put("dataSaida", dataInicio);
        values.put("horaSaida", horaInicio);
        return getDataBase().update("programacao", values, "id=" + id, null);
    }

    public void deleteProgramacao(List<Integer> lista) {

        for (int i = 0; i < lista.size(); i++) {
            if (existeProgramacao(lista.get(i))) {
                String args = TextUtils.join(",", lista);
                getDataBase().execSQL(String.format("DELETE FROM programacao where id NOT IN (%s)", args));
            }
        }

    }

    public List<Programacao> listar(int idMotorista) {
        List<Programacao> lista = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String data = sdf.format(new Date());
        Cursor cursor = getDataBase().rawQuery("SELECT * FROM programacao WHERE status = 0 and idMotorista = ? and dataSaida = ? ORDER BY tipo, dataSaida", new String[]{idMotorista + "", data});
        try {

            while (cursor.moveToNext()) {
                Programacao p = new Programacao();
                p.setId(cursor.getInt(cursor.getColumnIndex("id")));
                p.setDataSaida(cursor.getString(cursor.getColumnIndex("dataSaida")));
                p.setHoraSaida(cursor.getString(cursor.getColumnIndex("horaSaida")));
                p.setDataChegada(cursor.getString(cursor.getColumnIndex("dataChegada")));
                p.setHoraChegada(cursor.getString(cursor.getColumnIndex("horaChegada")));
                p.setRazaoSocial(cursor.getString(cursor.getColumnIndex("razaoSocial")));
                p.setRua(cursor.getString(cursor.getColumnIndex("rua")));
                p.setIdFilial(cursor.getInt(cursor.getColumnIndex("idFilial")));
                p.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                p.setTipo(cursor.getInt(cursor.getColumnIndex("tipo")));
                p.setTelefone(cursor.getString(cursor.getColumnIndex("telefone")));

                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return lista;
    }

    public List<Programacao> listarProgDoadores(int tipo, int idMotorista) {
        List<Programacao> lista = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String data = sdf.format(new Date());
        Cursor cursor = getDataBase().rawQuery("SELECT * FROM programacao WHERE tipo = ? and status=0 and idMotorista = ? and dataSaida = ? ORDER BY dataSaida",
                new String[]{tipo + "", idMotorista + "", data});
        try {

            while (cursor.moveToNext()) {

                Programacao p = new Programacao();
                p.setId(cursor.getInt(cursor.getColumnIndex("id")));
                p.setDataSaida(cursor.getString(cursor.getColumnIndex("dataSaida")));
                p.setHoraSaida(cursor.getString(cursor.getColumnIndex("horaSaida")));
                p.setDataChegada(cursor.getString(cursor.getColumnIndex("dataChegada")));
                p.setHoraChegada(cursor.getString(cursor.getColumnIndex("horaChegada")));
                p.setRazaoSocial(cursor.getString(cursor.getColumnIndex("razaoSocial")));
                p.setRua(cursor.getString(cursor.getColumnIndex("rua")));
                p.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                p.setIdFilial(cursor.getInt(cursor.getColumnIndex("idFilial")));
                p.setTipo(cursor.getInt(cursor.getColumnIndex("tipo")));
                p.setTelefone(cursor.getString(cursor.getColumnIndex("telefone")));

                lista.add(p);
            }

        } finally {
            cursor.close();
        }
        return lista;
    }

    public List<Programacao> listarProgRecebedores(int tipo, int idMotorista) {
        List<Programacao> lista = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String data = sdf.format(new Date());
        Cursor cursor = getDataBase().rawQuery("SELECT * FROM programacao WHERE tipo = ? and status=0 and idMotorista = ? and dataSaida = ? ORDER BY efetuada DESC, dataSaida",
                new String[]{tipo + "", idMotorista + "", data});
        try {

            while (cursor.moveToNext()) {

                Programacao p = new Programacao();
                p.setId(cursor.getInt(cursor.getColumnIndex("id")));
                p.setDataSaida(cursor.getString(cursor.getColumnIndex("dataSaida")));
                p.setHoraSaida(cursor.getString(cursor.getColumnIndex("horaSaida")));
                p.setDataChegada(cursor.getString(cursor.getColumnIndex("dataChegada")));
                p.setHoraChegada(cursor.getString(cursor.getColumnIndex("horaChegada")));
                p.setRazaoSocial(cursor.getString(cursor.getColumnIndex("razaoSocial")));
                p.setRua(cursor.getString(cursor.getColumnIndex("rua")));
                p.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                p.setIdFilial(cursor.getInt(cursor.getColumnIndex("idFilial")));
                p.setTipo(cursor.getInt(cursor.getColumnIndex("tipo")));
                p.setTelefone(cursor.getString(cursor.getColumnIndex("telefone")));
                p.setEfetuada(cursor.getInt(cursor.getColumnIndex("efetuada")));

                lista.add(p);
            }

        } finally {
            cursor.close();
        }
        return lista;
    }

    public List<Programacao> listarRecebedoresNaoEfetuada(int idMotorista) {
        List<Programacao> lista = new ArrayList<>();
        Cursor cursor = getDataBase().rawQuery("SELECT id, dataSaida,horaSaida,dataChegada,horaChegada,razaoSocial," +
                "rua,idFilial,status,tipo FROM programacao p WHERE efetuada = 0 and tipo = 3 and status=0 and idMotorista = ? ", new String[]{idMotorista + ""});
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String data = sdf.format(new Date());

            while (cursor.moveToNext()) {
                Programacao p = new Programacao();

                if (cursor.getString(cursor.getColumnIndex("dataSaida")).equals(data)) {
                    p.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    p.setDataSaida(cursor.getString(cursor.getColumnIndex("dataSaida")));
                    p.setHoraSaida(cursor.getString(cursor.getColumnIndex("horaSaida")));
                    p.setDataChegada(cursor.getString(cursor.getColumnIndex("dataChegada")));
                    p.setHoraChegada(cursor.getString(cursor.getColumnIndex("horaChegada")));
                    p.setRazaoSocial(cursor.getString(cursor.getColumnIndex("razaoSocial")));
                    p.setRua(cursor.getString(cursor.getColumnIndex("rua")));
                    p.setIdFilial(cursor.getInt(cursor.getColumnIndex("idFilial")));
                    p.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                    p.setTipo(cursor.getInt(cursor.getColumnIndex("tipo")));

                    lista.add(p);
                }
            }

        } finally {
            cursor.close();
        }
        return lista;
    }

    public List<Programacao> listarRecebedores(int idMotorista) {
        List<Programacao> lista = new ArrayList<>();
        Cursor cursor = getDataBase().rawQuery("SELECT id, dataSaida,horaSaida,dataChegada,horaChegada,razaoSocial," +
                "rua,idFilial,status,tipo FROM programacao p WHERE tipo = 3 and status=0 and idMotorista = ? ", new String[]{idMotorista + ""});
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String data = sdf.format(new Date());

            while (cursor.moveToNext()) {
                Programacao p = new Programacao();

                if (cursor.getString(cursor.getColumnIndex("dataSaida")).equals(data)) {
                    p.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    p.setDataSaida(cursor.getString(cursor.getColumnIndex("dataSaida")));
                    p.setHoraSaida(cursor.getString(cursor.getColumnIndex("horaSaida")));
                    p.setDataChegada(cursor.getString(cursor.getColumnIndex("dataChegada")));
                    p.setHoraChegada(cursor.getString(cursor.getColumnIndex("horaChegada")));
                    p.setRazaoSocial(cursor.getString(cursor.getColumnIndex("razaoSocial")));
                    p.setRua(cursor.getString(cursor.getColumnIndex("rua")));
                    p.setIdFilial(cursor.getInt(cursor.getColumnIndex("idFilial")));
                    p.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                    p.setTipo(cursor.getInt(cursor.getColumnIndex("tipo")));

                    lista.add(p);
                }
            }

        } finally {
            cursor.close();
        }
        return lista;
    }

    public long alteraSync(int idProgramacao) {
        ContentValues values = new ContentValues();
        values.put("sync", 1);

        return getDataBase().update("programacao", values, "id=" + idProgramacao, null);
    }

    public List<Programacao> listarProgSync(int idMotorista) {
        List<Programacao> lista = new ArrayList<>();
        Cursor cursor = getDataBase().rawQuery("SELECT p.id, p.dataChegada,p.horaChegada,p.razaoSocial,p.rua,p.tipo,p.sync, p.idFilial, p.dataSaida,p.horaSaida" +
                " FROM programacao p, produtosDoados pd, distribuicao d, consulta c WHERE (pd.finalizado = 1 and pd.idDoador = p.idFilial and p.sync=0 and p.id=c.idProgramacao)" +
                " or (d.finalizado = 1 and d.idFilial = p.idFilial and p.sync=0 and p.id=c.idProgramacao) and idMotorista = ? and p.sync=0 " +
                " GROUP BY p.id, p.dataChegada,p.horaChegada,p.razaoSocial,p.rua,p.tipo", new String[]{idMotorista + ""});
        try {

            while (cursor.moveToNext()) {
                Programacao p = new Programacao();

                p.setId(cursor.getInt(cursor.getColumnIndex("id")));
                p.setDataSaida(cursor.getString(cursor.getColumnIndex("dataSaida")));
                p.setHoraSaida(cursor.getString(cursor.getColumnIndex("horaSaida")));
                p.setDataChegada(cursor.getString(cursor.getColumnIndex("dataChegada")));
                p.setHoraChegada(cursor.getString(cursor.getColumnIndex("horaChegada")));
                p.setRazaoSocial(cursor.getString(cursor.getColumnIndex("razaoSocial")));
                p.setRua(cursor.getString(cursor.getColumnIndex("rua")));
                p.setIdFilial(cursor.getInt(cursor.getColumnIndex("idFilial")));
                p.setSync(cursor.getInt(cursor.getColumnIndex("sync")));
                p.setTipo(cursor.getInt(cursor.getColumnIndex("tipo")));

                lista.add(p);
            }

        } finally {
            cursor.close();
        }
        return lista;
    }

    public Programacao PegaProgramacaoAtual(int idMotorista) {
        Programacao p = new Programacao();
        Cursor cursor = getDataBase().rawQuery("SELECT p.id, p.dataChegada,p.horaChegada,p.razaoSocial,p.rua,p.tipo,p.sync, p.idFilial, p.dataSaida,p.horaSaida" +
                " FROM programacao p, produtosDoados pd, consulta c WHERE p.tipo = 2 and p.id=c.idProgramacao and pd.finalizado=1 and idMotorista = ? and p.sync = 0" +
                " GROUP BY p.id, p.dataChegada,p.horaChegada,p.razaoSocial,p.rua,p.tipo", new String[]{idMotorista + ""});
        try {

            while (cursor.moveToNext()) {


                p.setId(cursor.getInt(cursor.getColumnIndex("id")));
                p.setDataSaida(cursor.getString(cursor.getColumnIndex("dataSaida")));
                p.setHoraSaida(cursor.getString(cursor.getColumnIndex("horaSaida")));
                p.setDataChegada(cursor.getString(cursor.getColumnIndex("dataChegada")));
                p.setHoraChegada(cursor.getString(cursor.getColumnIndex("horaChegada")));
                p.setRazaoSocial(cursor.getString(cursor.getColumnIndex("razaoSocial")));
                p.setRua(cursor.getString(cursor.getColumnIndex("rua")));
                p.setIdFilial(cursor.getInt(cursor.getColumnIndex("idFilial")));
                p.setSync(cursor.getInt(cursor.getColumnIndex("sync")));
                p.setTipo(cursor.getInt(cursor.getColumnIndex("tipo")));
            }

        } finally {
            cursor.close();
        }
        return p;
    }

    public Programacao PegaProgramacaoAtual2(int idMotorista) {
        Programacao p = new Programacao();
        Cursor cursor = getDataBase().rawQuery("SELECT p.id, p.dataChegada,p.horaChegada,p.razaoSocial,p.rua,p.tipo,p.sync, p.idFilial, p.dataSaida,p.horaSaida" +
                " FROM programacao p, distribuicao d, consulta c WHERE p.tipo = 3 and p.id=c.idProgramacao and d.finalizado=1 and idMotorista = ? and p.sync = 0" +
                " GROUP BY p.id, p.dataChegada,p.horaChegada,p.razaoSocial,p.rua,p.tipo", new String[]{idMotorista + ""});

        while (cursor.moveToNext()) {


            p.setId(cursor.getInt(cursor.getColumnIndex("id")));
            p.setDataSaida(cursor.getString(cursor.getColumnIndex("dataSaida")));
            p.setHoraSaida(cursor.getString(cursor.getColumnIndex("horaSaida")));
            p.setDataChegada(cursor.getString(cursor.getColumnIndex("dataChegada")));
            p.setHoraChegada(cursor.getString(cursor.getColumnIndex("horaChegada")));
            p.setRazaoSocial(cursor.getString(cursor.getColumnIndex("razaoSocial")));
            p.setRua(cursor.getString(cursor.getColumnIndex("rua")));
            p.setIdFilial(cursor.getInt(cursor.getColumnIndex("idFilial")));
            p.setSync(cursor.getInt(cursor.getColumnIndex("sync")));
            p.setTipo(cursor.getInt(cursor.getColumnIndex("tipo")));
        }
        return p;
    }


    public int ContadorColeta(int idMotorista) {
        int contador = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String data = sdf.format(new Date());
        Cursor cursor = getDataBase().rawQuery("SELECT COUNT(id) contColeta FROM programacao WHERE tipo=2 and status=0 and dataSaida = ? and idMotorista = ?", new String[]{data, idMotorista + ""});
        try {
            while (cursor.moveToNext()) {
                contador = cursor.getInt(cursor.getColumnIndex("contColeta"));

            }
        } finally {
            cursor.close();
        }
        return contador;
    }

    public int ContadorEntrega(int idMotorista) {
        int contador = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String data = sdf.format(new Date());
        Cursor cursor = getDataBase().rawQuery("SELECT COUNT(id) contEntrega FROM programacao WHERE tipo=3 and status=0 and dataSaida = ? and idMotorista = ?", new String[]{data, idMotorista + ""});
        try {
            while (cursor.moveToNext()) {
                contador = cursor.getInt(cursor.getColumnIndex("contEntrega"));

            }
        } finally {
            cursor.close();
        }
        return contador;
    }

    public int ContadorAgenda(int idMotorista) {
        int contador = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String data = sdf.format(new Date());
        Cursor cursor = getDataBase().rawQuery("SELECT COUNT(id) contEntrega FROM programacao WHERE status=0 and dataSaida = ? and idMotorista =  ?", new String[]{data, idMotorista + ""});
        try {
            while (cursor.moveToNext()) {
                contador = cursor.getInt(cursor.getColumnIndex("contEntrega"));

            }
        } finally {
            cursor.close();
        }
        return contador;
    }

}
