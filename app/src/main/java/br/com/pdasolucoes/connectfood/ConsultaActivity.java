package br.com.pdasolucoes.connectfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.pdasolucoes.connectfood.adapter.ConsultaAdapter;
import br.com.pdasolucoes.connectfood.dao.ConsultaDao;
import br.com.pdasolucoes.connectfood.dao.ProdutosDao;
import br.com.pdasolucoes.connectfood.model.Consulta;
import br.com.pdasolucoes.connectfood.model.Produtos;
import br.com.pdasolucoes.connectfood.model.Programacao;

/**
 * Created by PDA on 15/02/2017.
 */

public class ConsultaActivity extends AppCompatActivity {

    private ConsultaAdapter adapter;
    private ListView listView;
    private ConsultaDao dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_consulta);
        setContentView(R.layout.activity_consulta);
        final SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.PREF, MODE_PRIVATE);

        dao = new ConsultaDao(this);
        listView = (ListView) findViewById(R.id.listView);

        adapter = new ConsultaAdapter(this, dao.listar(sharedpreferences.getInt("id", 0)));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Consulta c = (Consulta) adapter.getItem(position);

                if (c.getIdProgramacao().getTipo() == 2) {
                    Intent i = new Intent(ConsultaActivity.this, RomaneioActivity.class);
                    i.putExtra("idFilial", c.getIdProgramacao().getIdFilial());
                    i.putExtra("idProgramacao", c.getIdProgramacao().getId());
                    i.putExtra("email", c.getIdProgramacao().getEmail());
                    i.putExtra("dataInicio",c.getIdProgramacao().getDataSaida());
                    i.putExtra("horaInicio",c.getIdProgramacao().getHoraSaida());
                    i.putExtra("nomeFilial",c.getIdProgramacao().getRazaoSocial());
                    i.putExtra("idMotorista", sharedpreferences.getInt("id", 0));
                    startActivity(i);
                } else if (c.getIdProgramacao().getTipo() == 3) {
                    Intent i = new Intent(ConsultaActivity.this, EntregaActivity.class);
                    i.putExtra("idEntrega", c.getIdProgramacao().getIdFilial());
                    i.putExtra("nomeFilial",c.getIdProgramacao().getRazaoSocial());
                    i.putExtra("idProgramacao",c.getIdProgramacao().getId());
                    i.putExtra("idMotorista", sharedpreferences.getInt("id", 0));
                    startActivity(i);
                }
            }
        });

    }

}
