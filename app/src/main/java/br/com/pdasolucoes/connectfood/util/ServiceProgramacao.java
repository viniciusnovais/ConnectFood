package br.com.pdasolucoes.connectfood.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.pdasolucoes.connectfood.model.Programacao;
import br.com.pdasolucoes.connectfood.model.Usuario;

/**
 * Created by PDA on 20/02/2017.
 */

public class ServiceProgramacao {

    public static List<Programacao> Programacao() {

        String url = WebService.URL + "2aecab47a884";
        String resposta = WebService.makeRequest(url);
        JSONObject jsonObject;
        List<Programacao> lista = new ArrayList<>();
        String dataS = null, dataC = null;
    
        if (resposta == null) {

        } else {

            try {
                JSONArray json = new JSONArray(resposta);
                for (int i = 0; i < json.length(); i++) {
                    jsonObject = json.getJSONObject(i);

                    Programacao p = new Programacao();

                    p.setId(jsonObject.getInt("id"));
                    p.setDataSaida(jsonObject.getString("dataSaida"));
                    p.setHoraSaida(jsonObject.getString("horaSaida"));
                    p.setDataChegada(jsonObject.getString("dataChegada"));
                    p.setHoraChegada(jsonObject.getString("horaChegada"));
                    p.setRazaoSocial(jsonObject.getString("nomeFantasia"));
                    p.setRua(jsonObject.getString("rua"));
                    p.setTipo(jsonObject.getInt("tipo"));
                    p.setIdFilial(jsonObject.getInt("idFilial"));
                    p.setStatus(jsonObject.getInt("status"));
                    p.setIdMotorista(jsonObject.getInt("idMotorista"));
                    p.setEmail(jsonObject.getString("email"));
                    p.setTelefone(jsonObject.getString("telefone"));

                    if (p.getTipo() == 2 || p.getTipo() == 3) {
                        lista.add(p);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return lista;
    }
}
