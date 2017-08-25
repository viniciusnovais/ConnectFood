package br.com.pdasolucoes.connectfood;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.pdasolucoes.connectfood.adapter.SyncAdapter;
import br.com.pdasolucoes.connectfood.dao.AgendaDao;
import br.com.pdasolucoes.connectfood.dao.DistribuicaoDao;
import br.com.pdasolucoes.connectfood.dao.ProdutosDao;
import br.com.pdasolucoes.connectfood.model.Distribuicao;
import br.com.pdasolucoes.connectfood.model.Produtos;
import br.com.pdasolucoes.connectfood.model.Programacao;
import br.com.pdasolucoes.connectfood.util.ServicePostRomaneio;
import br.com.pdasolucoes.connectfood.util.VerificaConexao;

/**
 * Created by PDA on 16/02/2017.
 */

public class SyncActivity extends AppCompatActivity {

    private List<Programacao> lista = new ArrayList<>();
    private ListView listView;
    private SyncAdapter adapter;
    private AgendaDao dao;
    private Spinner spinnerFiltro;
    private String[] itemFiltroSelecao = new String[]{"Selecione uma Opção", "Selecionar", "Tudo"};
    public static int POSITION;
    private Button buttonSync;
    private AlertDialog dialogProgress;
    private Handler handler = new Handler();
    private int progressStatus = 0;
    private static int progress;
    private ProgressBar progressBar;
    private int[] vetorCheckSelecionado;
    private DistribuicaoDao distribuicaoDao;
    private ProdutosDao produtosDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_sync);
        setContentView(R.layout.activity_sync);

        dao = new AgendaDao(this);
        distribuicaoDao = new DistribuicaoDao(this);
        produtosDao = new ProdutosDao(this);
        SharedPreferences preferences = getSharedPreferences(MainActivity.PREF, MODE_PRIVATE);
        lista = dao.listarProgSync(preferences.getInt("id", 0));

        listView = (ListView) findViewById(R.id.listView);
        spinnerFiltro = (Spinner) findViewById(R.id.spinnerFiltro);
        buttonSync = (Button) findViewById(R.id.buttonSync);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.progress_bar, null);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        builder.setView(v);
        dialogProgress = builder.create();

        ArrayAdapter<String> adapterSpinnerFiltro = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, itemFiltroSelecao);

        spinnerFiltro.setAdapter(adapterSpinnerFiltro);

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                POSITION = position;

                adapter = new SyncAdapter(SyncActivity.this, lista);
                listView.setAdapter(adapter);

                adapter.setOnItemClickListener(new SyncAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(int[] vetorInt) {
                        vetorCheckSelecionado = vetorInt;
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VerificaConexao.isNetworkConnected(SyncActivity.this)) {
                    try {
                        if (vetorCheckSelecionado.length != 0) {
                            AsyncPostRomaneio task = new AsyncPostRomaneio();
                            task.execute();
                        }
                    } catch (NullPointerException e) {
                        Toast.makeText(getApplicationContext(), "Selecione um Item", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Conecte-se a uma rede de internet", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public class AsyncPostRomaneio extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            new Thread(new Runnable() {
                public void run() {
                    progressStatus = doSomeWork();
                    handler.post(new Runnable() {
                        public void run() {
                            dialogProgress.show();
                            progressBar.setProgress(progressStatus);
                        }
                    });
                }

                private int doSomeWork() {
                    try {
                        // ---simulate doing some work---
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return ++progress;
                }
            }).start();


        }

        @Override
        protected String doInBackground(String... params) {
            SharedPreferences preferences = getSharedPreferences(MainActivity.PREF, MODE_PRIVATE);
            for (int i = 0; i < vetorCheckSelecionado.length; i++) {
                if (vetorCheckSelecionado[i] == 1) {
                    Programacao p;
                    p = (Programacao) adapter.getItem(i);
                    if (p.getTipo() == 2) {
                        for (Produtos produto : produtosDao.listarDoacao(p.getIdFilial(), p.getId())) {
                            try {
                                if (p.getSync() == 0) {
                                    dao.alteraSync(p.getId());
                                    String aux = String.format("%.3f", produto.getQtde());
                                    ServicePostRomaneio.post(p.getRazaoSocial(), produto.getCod(), produto.getCategoria(), produto.getNome(), aux,
                                            p.getTipo(), p.getDataSaida(), p.getHoraSaida(), p.getDataChegada(), p.getHoraChegada(), preferences.getString("nome", ""));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (p.getTipo() == 3) {
                        for (Distribuicao d : distribuicaoDao.listarDistribuicaoFilial(p.getIdFilial())) {
                            try {
                                if (p.getSync() == 0) {
                                    dao.alteraSync(p.getId());
                                    String aux = String.format("%.3f", d.getQtde());
                                    ServicePostRomaneio.post(p.getRazaoSocial(), d.getProdutos().getCod(), d.getProdutos().getCategoria(),
                                            d.getProdutos().getNome(), aux, p.getTipo(), p.getDataSaida(), p.getHoraSaida(), p.getDataChegada(), p.getHoraChegada(), preferences.getString("nome", ""));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialogProgress.isShowing()) {
                finish();
                startActivity(getIntent());
                dialogProgress.dismiss();
            }
        }
    }
}
