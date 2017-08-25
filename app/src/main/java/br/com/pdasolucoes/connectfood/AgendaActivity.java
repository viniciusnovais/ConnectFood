package br.com.pdasolucoes.connectfood;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import br.com.pdasolucoes.connectfood.adapter.AgendaAdapter;
import br.com.pdasolucoes.connectfood.dao.AgendaDao;
import br.com.pdasolucoes.connectfood.model.Programacao;
import br.com.pdasolucoes.connectfood.util.ServiceProgramacao;
import br.com.pdasolucoes.connectfood.util.VerificaConexao;

/**
 * Created by PDA on 15/02/2017.
 */

public class AgendaActivity extends AppCompatActivity {

    private ListView listView;
    private AgendaAdapter adapter;
    private List<Programacao> lista = new ArrayList<>();
    private ImageButton btHome;
    private AgendaDao dao;
    private List<Integer> listaInt;
    private Spinner spinnerAtividade, spinnerPeríodo;
    private String[] listaPeriodo = new String[]{"Semanal", "Quinzenal", "Mensal"};
    private String[] listaAtividade = new String[]{"Todos", "Coleta", "Entrega"};
    public final static String PREF = "LoginPreference";
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_agenda);
        setContentView(R.layout.activity_agenda);

        dao = new AgendaDao(this);

        final SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.PREF, MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.progress_bar, null);
        builder.setView(v);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        AsyncAgenda task = new AsyncAgenda();
        task.execute();

        btHome = (ImageButton) findViewById(R.id.btHome);
        btHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AgendaActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });


        spinnerAtividade = (Spinner) findViewById(R.id.spinnerAtividade);

        ArrayAdapter<String> adapterAtividade = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, listaAtividade);
        spinnerAtividade.setAdapter(adapterAtividade);

        spinnerAtividade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    listView = (ListView) findViewById(R.id.listView);
                    if (position == 0) {
                        adapter = new AgendaAdapter(AgendaActivity.this, dao.listar(sharedpreferences.getInt("id", 0)));
                        listView.setAdapter(adapter);

                    } else if (position == 1) {
                        spinnerAtividade.setSelection(1);
                        adapter = new AgendaAdapter(AgendaActivity.this, dao.listarProgDoadores(2, sharedpreferences.getInt("id", 0)));
                        listView.setAdapter(adapter);

                    } else if (position == 2) {
                        spinnerAtividade.setSelection(2);
                        adapter = new AgendaAdapter(AgendaActivity.this, dao.listarProgRecebedores(3, sharedpreferences.getInt("id", 0)));
                        listView.setAdapter(adapter);
                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerPeríodo = (Spinner)

                findViewById(R.id.spinnerPeriodo);

        ArrayAdapter<String> adapterPeriodo = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, listaPeriodo);
        spinnerPeríodo.setAdapter(adapterPeriodo);

        spinnerPeríodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {
                if (position == 0) {
                    //semanal

                } else if (position == 1) {
                    //quinzenal

                } else {
                    //mensal

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public class AsyncAgenda extends AsyncTask {

        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.PREF, MODE_PRIVATE);

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                listaInt = new ArrayList<>();
                if (VerificaConexao.isNetworkConnected(AgendaActivity.this)) {
                    lista = ServiceProgramacao.Programacao();
                    for (Programacao p : lista) {
                        listaInt.add(p.getId());
                    }
                    dao.deleteProgramacao(listaInt);
                } else {
                    lista = dao.listar(sharedpreferences.getInt("id", 0));
                    for (Programacao p : lista) {
                        listaInt.add(p.getId());
                    }
                }
                dao.incluir(lista);

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            listView = (ListView) findViewById(R.id.listView);
            try {
                if (getIntent().getExtras().getInt("tipo") == 2) {
                    adapter = new AgendaAdapter(AgendaActivity.this, dao.listarProgDoadores(2, sharedpreferences.getInt("id", 0)));
                    listView.setAdapter(adapter);

                } else if (getIntent().getExtras().getInt("tipo") == 3) {
                    adapter = new AgendaAdapter(AgendaActivity.this, dao.listarProgRecebedores(3, sharedpreferences.getInt("id", 0)));
                    listView.setAdapter(adapter);

                } else if (getIntent().getExtras().getInt("tipo") == -1) {
                    adapter = new AgendaAdapter(AgendaActivity.this, dao.listar(sharedpreferences.getInt("id", 0)));
                    listView.setAdapter(adapter);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //pegando a data de hoje
                    Date date = new Date();
                    long l = date.getTime();
                    //setando a data de hoje nessa variavel
                    Date hoje = new Date(l);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date dataRealizarTarefa = null;
                    try {

                        dataRealizarTarefa = sdf.parse(adapter.getItem(position).getDataChegada().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    if (hoje.before(dataRealizarTarefa)) {
                        //Toast.makeText(getApplicationContext(),sdf.format(hoje)+" < "+sdf.format(dataRealizarTarefa),Toast.LENGTH_LONG).show();
                    } else if (hoje.equals(dataRealizarTarefa)) {
                        //uToast.makeText(getApplicationContext(),hoje+" = "+dataRealizarTarefa,Toast.LENGTH_LONG).show();
                    } else {
                        //Toast.makeText(getApplicationContext(),hoje+" > "+dataRealizarTarefa,Toast.LENGTH_LONG).show();
                    }

                    int getid = adapter.getItem(position).getId();
                    String getLocal = adapter.getItem(position).getRua();
                    int tipo = adapter.getItem(position).getTipo();
                    int idDoador = adapter.getItem(position).getIdFilial();

                    Intent i = new Intent(AgendaActivity.this, CheckinActivity.class);
                    i.putExtra("idProgramacao", getid);
                    i.putExtra("email", adapter.getItem(position).getEmail());
                    i.putExtra("idDoador", idDoador);
                    i.putExtra("local", getLocal);
                    i.putExtra("dataSaida", adapter.getItem(position).getDataSaida());
                    i.putExtra("tipo", tipo);
                    i.putExtra("razaoSocial", adapter.getItem(position).getRazaoSocial());
                    i.putExtra("telefone", adapter.getItem(position).getTelefone());
                    startActivity(i);
                    finish();

                }
            });

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

}
