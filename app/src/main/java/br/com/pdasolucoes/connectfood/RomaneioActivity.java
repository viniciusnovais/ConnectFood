package br.com.pdasolucoes.connectfood;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import br.com.pdasolucoes.connectfood.adapter.RomaneioAdapter;
import br.com.pdasolucoes.connectfood.dao.AgendaDao;
import br.com.pdasolucoes.connectfood.dao.ProdutosDao;
import br.com.pdasolucoes.connectfood.model.Produtos;
import br.com.pdasolucoes.connectfood.model.Programacao;
import br.com.pdasolucoes.connectfood.util.SendMailRomaneio;
import br.com.pdasolucoes.connectfood.util.ServicePostRomaneio;
import br.com.pdasolucoes.connectfood.util.VerificaConexao;

/**
 * Created by PDA on 20/02/2017.
 */

public class RomaneioActivity extends AppCompatActivity {

    private ListView listView;
    private RomaneioAdapter adapter;
    private ProdutosDao dao;
    private AgendaDao agendaDao;
    private ImageButton btAlterar, btAssinar;
    public String message = "";
    private List<Produtos> lista;
    private AlertDialog dialogProgress;
    private Handler handler = new Handler();
    private int progressStatus = 0;
    private static int progress;
    private ProgressBar progressBar;
    private String dataFim, horaFim;
    private TextView tvInstituicao, tvTotal, tvCodRomaneio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_romaneio);
        setContentView(R.layout.activity_romaneio);
        dao = new ProdutosDao(this);
        agendaDao = new AgendaDao(this);
        lista = dao.listarDoacao(getIntent().getExtras().getInt("idFilial"), getIntent().getExtras().getInt("idProgramacao"));

        tvInstituicao = (TextView) findViewById(R.id.tvInstituicao);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        tvCodRomaneio = (TextView) findViewById(R.id.tvCodRomaneio);

        String aux = String.format("%.3f", dao.totalProdutosDoados(getIntent().getExtras().getInt("idFilial"), getIntent().getExtras().getInt("idProgramacao"))).replaceAll("[.]", ",");
        tvTotal.setText(aux);
        tvCodRomaneio.setText(dao.buscarCodRomaneio(getIntent().getExtras().getInt("idFilial")));
        tvInstituicao.setText(getIntent().getExtras().getString("nomeFilial"));

        listView = (ListView) findViewById(R.id.listView);

        btAlterar = (ImageButton) findViewById(R.id.btAlterar);
        btAssinar = (ImageButton) findViewById(R.id.btAssinar);

        //alert progressBar
        AlertDialog.Builder builderProgress = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.progress_bar, null);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        builderProgress.setView(view);
        dialogProgress = builderProgress.create();

        //alert confirma
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View v = View.inflate(this, R.layout.confirma_senha, null);
//        btConfirma = (Button) v.findViewById(R.id.btConfirma);
//        editCodigo = (EditText) v.findViewById(R.id.editCodigo);
//        builder.setView(v);
//        dialogConfirma = builder.create();
//        dialogConfirma.setCanceledOnTouchOutside(false);

        for (Produtos p : lista) {
            if (p.getStatus() == 1) {
                btAssinar.setImageResource(R.drawable.ic_assinar_gray);
                btAssinar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(RomaneioActivity.this, "Romaneio já foi assinado!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                btAssinar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //gerando código romaneio coleta
                        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
                        Date hoje = new Date();
                        String codRomaneio = formato.format(hoje).replaceAll("[/]", "");
                        codRomaneio = codRomaneio + getIntent().getExtras().getInt("idProgramacao");
                        for (Produtos p : lista) {
                            p.setCodRomaneio(codRomaneio);
                            dao.inserirCodRomaneio(p);
                        }

                        if (VerificaConexao.isNetworkConnected(RomaneioActivity.this)) {
                            sendEmail();
                            Toast.makeText(getApplicationContext(), "Enviando Email", Toast.LENGTH_SHORT).show();
                            AsyncEnviarRomaneio task = new AsyncEnviarRomaneio();
                            task.execute();
                        } else {
                            Toast.makeText(getApplicationContext(), "Não esqueça de enviar as informações no Sync", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(getIntent());
                            Intent i = new Intent(RomaneioActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();
                        }

                        //mudar cor do assinar e desabilitado
                        btAssinar.setImageResource(R.drawable.ic_assinar_gray);
                        btAssinar.setClickable(false);
                        int statusFinalizado = 0;
                        for (Produtos produtos : dao.listarDoacao(getIntent().getExtras().getInt("idFilial"), getIntent().getExtras().getInt("idProgramacao"))) {
                            dao.alterarStatus(produtos);

                            if (produtos.getFinalizado() == 0) {
                                dao.alterarFinalizacao(produtos);
                                dao.alterarFinalizacaoAux(produtos);
                                statusFinalizado = 1;
                            }

                        }

                        if (statusFinalizado == 1) {
                            Toast.makeText(getApplicationContext(), "Romaneio Finalizado", Toast.LENGTH_SHORT).show();

                            //Pegando hora fim do romaneio coleta (assinatura)
                            SimpleDateFormat sdf = new SimpleDateFormat("HH : mm");
                            SimpleDateFormat sdfData = new SimpleDateFormat("yyyy/MM/dd");
                            horaFim = sdf.format(new Date());
                            dataFim = sdfData.format(new Date());

                            String dataInicio = getIntent().getExtras().getString("dataInicio");
                            String horaInicio = getIntent().getExtras().getString("horaInicio");
                            agendaDao.alterarDataHoraInicio(getIntent().getExtras().getInt("idProgramacao"), dataInicio, horaInicio);
                            agendaDao.alterarDataHoraFim(getIntent().getExtras().getInt("idProgramacao"), dataFim, horaFim);

                        }

                        dao.incluirNaListaAux(dao.listarTodosFilial(getIntent().getExtras().getInt("idProgramacao")));

                        //dialogConfirma.show();

                    }
                });


            }
        }

        if (lista.size() < 1) {
            btAssinar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RomaneioActivity.this, "Não existem produtos", Toast.LENGTH_SHORT).show();
                }
            });
        }

        adapter = new RomaneioAdapter(this, dao.listarDoacao(getIntent().getExtras().getInt("idFilial"), getIntent().getExtras().getInt("idProgramacao")));
        listView.setAdapter(adapter);

        btAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int alteraStatus = 0;
                for (Produtos p : lista) {
                    if (p.getStatus() == 1) {
                        btAlterar.setClickable(false);
                        alteraStatus = 2;
                    }
                }
                if (alteraStatus == 2) {
                    Toast.makeText(getApplicationContext(), "Romaneio já foi confirmado", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(RomaneioActivity.this, ListaProdutosActivity.class);
                    i.putExtra("idProgramacao", getIntent().getExtras().getInt("idProgramacao"));
                    i.putExtra("idDoa", getIntent().getExtras().getInt("idFilial"));
                    i.putExtra("email", getIntent().getExtras().getString("email"));
                    i.putExtra("dataInicio", getIntent().getExtras().getString("dataInicio"));
                    i.putExtra("horaInicio", getIntent().getExtras().getString("horaInicio"));
                    i.putExtra("nomeFilial", getIntent().getExtras().getString("nomeFilial"));
                    i.putExtra("romaneioAltera", "RomaneioActivity");
                    startActivity(i);
                    finish();

                }
            }
        });
//USAREI DEPOIS, QUANDO PRECISAR MANDAR O CODIGO PARA A FILIAL
//        btConfirma.setOnClickListener(new View.OnClickListener() {
//                                          @Override
//                                          public void onClick(View v) {
//                                              if (editCodigo.getText().toString().equals(message)) {
        //Toast.makeText(getApplicationContext(), "Código confirmado", Toast.LENGTH_SHORT).show();
//                                              dialogConfirma.dismiss();
//                                              btAssinar.setImageResource(R.drawable.ic_assinar_gray);
//                                              btAssinar.setClickable(false);
//                                              finish();
//                                              startActivity(getIntent());
//                                              int statusFinalizado = 0;
//                                              for (Produtos p : dao.listarDoacao(getIntent().getExtras().getInt("idFilial"), getIntent().getExtras().getInt("idProgramacao"))) {
//                                                  dao.alterarStatus(p);
//
//                                                  if (p.getFinalizado() == 0) {
//                                                      dao.alterarFinalizacao(p);
//                                                      statusFinalizado = 1;
//                                                  }
//
//                                              }
//
//                                              if (statusFinalizado == 1) {
//                                                  Toast.makeText(getApplicationContext(), "Romaneio Finalizado", Toast.LENGTH_SHORT).show();
//                                                  Intent i = new Intent(RomaneioActivity.this, HomeActivity.class);
//                                                  startActivity(i);
//                                                  finish();
//                                              }

//                                              } else {
//                                                  Toast.makeText(getApplicationContext(), "Código errado", Toast.LENGTH_SHORT).show();
//                                                  dialogConfirma.dismiss();
//                                              }
//                                          }
//                                      }

//        );
    }


    public class AsyncEnviarRomaneio extends AsyncTask<String, Void, String> {

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
                        Thread.sleep(100);
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
            Programacao p = agendaDao.PegaProgramacaoAtual(preferences.getInt("id", 0));

            for (Produtos produto : dao.listarDoacao(p.getIdFilial(), p.getId())) {
                try {

                    if (p.getSync() == 0) {
                        agendaDao.alteraSync(p.getId());
                        String aux = String.format("%.3f", produto.getQtde());
                        ServicePostRomaneio.post(p.getRazaoSocial(), produto.getCod(), produto.getCategoria(), produto.getNome(), aux,
                                p.getTipo(), p.getDataSaida(), p.getHoraSaida(), p.getDataChegada(), p.getHoraSaida(), preferences.getString("nome", ""));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialogProgress.isShowing()) {
                dialogProgress.dismiss();
                Intent i = new Intent(RomaneioActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    private void sendEmail() {
        //Getting content for email

        String email = getIntent().getExtras().getString("email");

        //String email = "v.novais@outlook.com";
        String subject = "Confirmação de Doação de Alimentos";
        long codigo = 0;
        codigo = (long) (100000 + Math.random() * 999999);

        message = codigo + "";
        String table = "<h3>Lista de alimentos doados para a Associação Prato Cheio:</h3><br>" +
                "<TABLE BORDER=1>" +
                "<tr><td><FONT size =\"4\" face=\"calibri light\"> Código </FONT></td>" +
                "<td><FONT size =\"4\" face=\"calibri light\"> Descrição </FONT></td>" +
                "<td><FONT COLOR=\"#FF000\" size =\"4\" face=\"Calibri light\">Quantidade</FONT></td>" +
                "<td><FONT COLOR=\"#FF000\" size =\"4\" face=\"Calibri light\">Peso</FONT></td></tr>";
        for (Produtos p : lista) {
            String aux = String.format("%.3f", p.getQtde()).replaceAll("[.]", ",");
            String unidMedida = "";
            if (p.getUnidMedida() == 1) {
                unidMedida = "Kg";
            } else {
                unidMedida = "Emb.";
            }
            table = table + "<tr><td><FONT size =\"4\" face=\"calibri light\">" + p.getCod() + "</FONT></td>" +
                    "<td><FONT size =\"4\" face=\"calibri light\">" + p.getNome() + "</FONT></td>" +
                    "<td><FONT COLOR=\"#FF000\" size =\"4\" face=\"calibri light\">" + aux + "</FONT></td>" +
                    "<td><FONT COLOR=\"#FF000\" size =\"4\" face=\"calibri light\">" + unidMedida + "</FONT></td></tr>";
        }
        String total = String.format("%.3f", dao.totalProdutosDoados(getIntent().getExtras().getInt("idFilial"), getIntent().getExtras().getInt("idProgramacao"))).replaceAll("[.]", ",");

        table = table + "<tr><td><FONT size =\"4\" face=\"calibri light\"> Total </FONT></td>" +
                "<td colspan=\"3\" align=\"right\"><FONT COLOR=\"#FF000\" size =\"4\" face=\"calibri light\" colspan=\"2\"> " + total + " Kg </FONT></td></tr>";


        //Creating SendMail object

        SendMailRomaneio sm = new SendMailRomaneio(this, email, subject, table, message);

        //Executing sendmail to send email
        sm.execute();
    }
}
