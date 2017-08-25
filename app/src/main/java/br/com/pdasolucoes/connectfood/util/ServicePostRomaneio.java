package br.com.pdasolucoes.connectfood.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by PDA on 28/03/2017.
 */

public class ServicePostRomaneio {

    public static void post(String razaoSocial, int cod, String categoria, String nome, String qtde, int tipo, String dataInicio, String horaInicio, String dataTermino, String horaTermino, String motorista) throws JSONException {
        String resposta = "";


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("razaoSocial", razaoSocial);
        jsonObject.put("cod", cod);
        jsonObject.put("categoria", categoria);
        jsonObject.put("nome", nome);
        jsonObject.put("qtde", qtde);
        jsonObject.put("tipo", tipo);
        jsonObject.put("dataInicio", dataInicio);
        jsonObject.put("horaInicio", horaInicio);
        jsonObject.put("dataTermino", dataTermino);
        jsonObject.put("horaTermino", horaTermino);
        jsonObject.put("motorista", motorista);

        try {
            URL url = new URL(WebService.URL + "aaf614efc50e/");
            HttpURLConnection conexao = null;
            conexao = (HttpURLConnection) url.openConnection();
            conexao.setDoInput(true);
            conexao.setDoOutput(true);
            conexao.setRequestProperty("Content-Type", "application/json");
            conexao.setRequestProperty("Accept", "application/json");
            conexao.setRequestMethod("POST");


            conexao.connect();
            OutputStreamWriter wr = new OutputStreamWriter(conexao.getOutputStream());
            wr.write(jsonObject.toString());

            OutputStream os = conexao.getOutputStream();
            os.write(jsonObject.toString().getBytes("UTF-8"));
            //os.close();

            InputStream in;
            int status = conexao.getResponseCode();
            if (status >= HttpURLConnection.HTTP_BAD_REQUEST) {
                Log.d("ERRO", "Error code: " + status);
                in = conexao.getErrorStream();
            } else {
                in = conexao.getInputStream();
            }

            resposta = WebService.readStream(in);
            Log.w("Resposta", resposta);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

