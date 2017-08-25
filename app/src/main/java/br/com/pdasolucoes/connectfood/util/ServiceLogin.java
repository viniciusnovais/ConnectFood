package br.com.pdasolucoes.connectfood.util;


import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import br.com.pdasolucoes.connectfood.model.Usuario;


/**
 * Created by PDA on 06/02/2017.
 */

public class ServiceLogin {

    //logando externamente
    private static String URL="http://179.184.159.52/WSConectFood/wsusuario.asmx";
    //logando internamente
    //private static String URL="http://pdaservidor/WSConectFood/wsusuario.asmx";

    private static String SOAP_ACTION = "http://tempuri.org/PesquisaUsuario";

    private static String NAMESPACE="http://tempuri.org/";

    public static Usuario Login() {

        String url = WebService.URLLOGIN + "login";
        String resposta = WebService.makeRequest(url);
        JSONObject jsonObject= null;
        Usuario usuario = new Usuario();

        if (resposta == null) {

        } else {

            try {
                JSONArray json = new JSONArray(resposta);
                for (int i=0;i<json.length();i++){
                    jsonObject = json.getJSONObject(i);

                    usuario.setId(jsonObject.getInt("id"));
                    usuario.setUsuario(jsonObject.getString("usuario"));
                    usuario.setSenha(jsonObject.getString("senha"));
                    usuario.setNomeUsuario(jsonObject.getString("nome"));
                    usuario.setTipoInstituicao(jsonObject.getInt("tipo"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return usuario;
    }

    public static Usuario invokeLoginWS(String userName, String passWord) {
        Usuario usuario = new Usuario();

        Log.w("usuario",userName);
        Log.w("pass",passWord);
        SoapObject response=null;
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, "PesquisaUsuario");
        // Property which holds input parameters
        PropertyInfo unamePI = new PropertyInfo();
        PropertyInfo passPI = new PropertyInfo();
        // Set Username
        unamePI.setName("usuario");
        // Set Value
        unamePI.setValue(userName);
        // Set dataType
        unamePI.setType(String.class);
        // Add the property to request object
        request.addProperty(unamePI);
        //Set Password
        passPI.setName("senha");
        //Set dataType
        passPI.setValue(passWord);
        //Set dataType
        passPI.setType(String.class);
        //Add the property to request object
        request.addProperty(passPI);

        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet=true;
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            // Invoke web service
            androidHttpTransport.call(SOAP_ACTION, envelope);
            // Get the response
            response = (SoapObject) envelope.getResponse();

            SoapObject objectUsuario = (SoapObject) response.getProperty("UsuarioConectFood");

            usuario.setId(Integer.parseInt(objectUsuario.getProperty("idUsuario").toString()));
            usuario.setNomeUsuario(objectUsuario.getProperty("nome").toString());
            usuario.setTipoInstituicao(Integer.parseInt(objectUsuario.getProperty("tipo").toString()));
            usuario.setUsuario(objectUsuario.getProperty("usuario").toString());
            usuario.setSenha(objectUsuario.getProperty("senha").toString());


        } catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            //LoginActivity.errored = true;
            e.printStackTrace();
        }
        //Return booleam to calling object
        return usuario;
    }
}
