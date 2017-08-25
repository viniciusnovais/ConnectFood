package br.com.pdasolucoes.connectfood.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class WebService {
    public static final String URL = "https://sheetsu.com/apis/v1.0/";
    public static final String URLLOGIN = "https://private-89c42-connectfood.apiary-mock.com/";

    /**
     * M�todo para realizar a leitura de um InputStream e convert�-lo em String
     *
     * @return InputStream com os dados para serem lidos
     * <p>
     * Conte�do convertido em String
     */

    public static String readStream(InputStream in) {
        // cria uma refer�ncia para BufferedReader
        BufferedReader reader = null;
        // cria um StringBuilder
        StringBuilder builder = new StringBuilder();
        try {
            // cria o BufferReader passando o InputStream recebido como
            // par�metro
            reader = new BufferedReader(new InputStreamReader(in));
            // cria uma linha nula
            String line = "null";
            // l� as linhas e acrescentar ao StringBuilder
            // at� que n�o haja mais conte�do nas linhas
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
        } catch (Exception e) {
            Log.w("Erro", e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    // tenta fechar o reader
                    reader.close();
                } catch (Exception e2) {
                    Log.w("Erro", e2.getMessage());
                }
            }
        }
        return builder.toString();
    }

    /**
     * M�todo para ler os dados de um endere�o HTTP
     *
     * @param urlAddres no formato http://
     * @return
     */

    public static String makeRequest(String urlAddres) {
        // cria uma refer�ncia para se conectar com uma URL Http
        HttpURLConnection con = null;

        // cria uma refer�ncia para uma URL
        URL url = null;
        //cria uma String de retono
        String retorno = null;
        try {
            //inst�ncia a url atrav�s do endere�o recebido no m�todo
            url = new URL(urlAddres);
            //abre a conex�o e guarda na vari�vel con
            con = (HttpURLConnection) url.openConnection();
            //code resposta
            con.getResponseCode();
            //invocar o m�tpodo readStream para ler o conte�do da p�gina
            retorno = readStream(con.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //fecha conex�o
            con.disconnect();
        }
        //retorna texto lido em forma de String (Pegar isso e traduzir para GSON)
        return retorno;
    }
}
