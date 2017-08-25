package br.com.pdasolucoes.connectfood.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.pdasolucoes.connectfood.model.Produtos;

/**
 * Created by PDA on 20/02/2017.
 */

public class ServiceProdutos {

    public static List<Produtos> Produtos() {

        String url = WebService.URL + "9ef4dd5d62de";
        String resposta = WebService.makeRequest(url);
        JSONObject jsonObject;
        List<Produtos> lista = new ArrayList<>();


        if (resposta == null) {

        } else {

            try {
                JSONArray json = new JSONArray(resposta);
                for (int i = 0; i < json.length(); i++) {
                    jsonObject = json.getJSONObject(i);

                    Produtos p = new Produtos();

                    p.setId(jsonObject.getInt("id"));
                    p.setCategoria(jsonObject.getString("categoria"));
                    p.setNome(jsonObject.getString("nome"));
                    p.setCod(jsonObject.getInt("cod"));
                    p.setUnidMedida(jsonObject.getInt("unidMedida"));
                    //p.setIdFilial(jsonObject.getInt("idFilial"));

                    lista.add(p);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return lista;
    }
}
