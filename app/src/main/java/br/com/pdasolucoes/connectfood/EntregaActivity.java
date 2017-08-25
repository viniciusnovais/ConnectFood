package br.com.pdasolucoes.connectfood;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import br.com.pdasolucoes.connectfood.adapter.EntregaAdapter;
import br.com.pdasolucoes.connectfood.adapter.ListaFilialDistribuir;
import br.com.pdasolucoes.connectfood.dao.AgendaDao;
import br.com.pdasolucoes.connectfood.dao.ConsultaDao;
import br.com.pdasolucoes.connectfood.dao.DistribuicaoDao;
import br.com.pdasolucoes.connectfood.dao.ProdutosDao;
import br.com.pdasolucoes.connectfood.model.Distribuicao;
import br.com.pdasolucoes.connectfood.model.Produtos;
import br.com.pdasolucoes.connectfood.model.Programacao;
import br.com.pdasolucoes.connectfood.util.SendMailRomaneio;
import br.com.pdasolucoes.connectfood.util.ServicePostRomaneio;
import br.com.pdasolucoes.connectfood.util.VerificaConexao;

/**
 * Created by PDA on 20/02/2017.
 */

public class EntregaActivity extends AppCompatActivity {

    private List<Distribuicao> lista;
    private RecyclerView recyclerView;
    private EntregaAdapter adapter;
    private DistribuicaoDao dao;
    private ImageButton btAssinar, btHome, btFinalizar;
    private Button btConfirma;
    private EditText editCodigo;
    private AlertDialog dialogConfirma;
    public String message = "", dataFim, horaFim;
    private ConsultaDao consultaDao;
    private AgendaDao agendaDao;
    private ProdutosDao produtosDao;
    private AlertDialog dialogProgress;
    private Handler handler = new Handler();
    private int progressStatus = 0;
    private static int progress;
    private ProgressBar progressBar;
    public static String[] dataHora;
    private TextView tvInstituicao, tvQtdeTotal, tvCodRomaneio;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_romaneio);
//        TextView tvNomeFilial = (TextView) findViewById(R.id.tvNomeFilial);
//        tvNomeFilial.setText(getIntent().getExtras().getString("nomeFilial"));
        setContentView(R.layout.activity_entrega);

        dao = new DistribuicaoDao(this);
        produtosDao = new ProdutosDao(this);

        btAssinar = (ImageButton) findViewById(R.id.btAssinar);
        btHome = (ImageButton) findViewById(R.id.btHome);
        tvInstituicao = (TextView) findViewById(R.id.tvInstituicao);
        tvQtdeTotal = (TextView) findViewById(R.id.tvTotal);
        tvCodRomaneio = (TextView) findViewById(R.id.tvCodRomaneio);

        //alert progressBar
        AlertDialog.Builder builderProgress = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.progress_bar, null);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        builderProgress.setView(view);
        dialogProgress = builderProgress.create();

//        AlertDialog.Builder builderConfirma = new AlertDialog.Builder(this);
//        View v = View.inflate(this, R.layout.confirma_senha, null);
//        btConfirma = (Button) v.findViewById(R.id.btConfirma);
//        editCodigo = (EditText) v.findViewById(R.id.editCodigo);
//        //editCodigo.setText("");
//        builderConfirma.setView(v);
//        dialogConfirma = builderConfirma.create();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


//USAREI DEPOIS, QUANDO PRECISAR MANDAR O CODIGO PARA A FILIAL
//        btConfirma.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (editCodigo.getText().toString().equals(message)) {
//                    Toast.makeText(getApplicationContext(), "Código confirmado", Toast.LENGTH_SHORT).show();
//                    dialogConfirma.dismiss();
//
//                    btAssinar.setImageResource(R.drawable.ic_assinar_gray);
//                    btAssinar.setClickable(false);
//                    finish();
//                    startActivity(getIntent());
//                    int statusFinalizaRomaneio = 0;
//                    for (Distribuicao d : dao.listarDistribuicaoFilial(getIntent().getExtras().getInt("idEntrega"))) {
//                        dao.alterarStatusAssinado(d);
//
//                        if (d.getFinalizado() == 0) {
//                            dao.alterarStatusFinalizado(d);
//                            statusFinalizaRomaneio = 1;
//                        }
//                    }
//
//                    if (statusFinalizaRomaneio == 1) {
//                        Programacao p = new Programacao();
//                        consultaDao = new ConsultaDao(EntregaActivity.this);
//                        agendaDao = new AgendaDao(EntregaActivity.this);
//                        p.setId(getIntent().getExtras().getInt("idEntrega"));
//
//
//                        if (getIntent().hasExtra("romaneioAltera") == false) {
//                            consultaDao.incluir(p);
//                            agendaDao.alterarProg(getIntent().getExtras().getInt("id"));
//
//                            Toast.makeText(getApplicationContext(), "Romaneio finalizado", Toast.LENGTH_SHORT).show();
//
//                            //Voltar Home depois de assinar
//                            Intent i = new Intent(EntregaActivity.this, HomeActivity.class);
//                            startActivity(i);
//                            finish();
//                        }
//
//                        finish();
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), "Código errado", Toast.LENGTH_SHORT).show();
//                    dialogConfirma.dismiss();
//                }
//            }
//        });


        btHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EntregaActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });


        for (Distribuicao d : dao.listarDistribuicaoFilial(getIntent().getExtras().getInt("idEntrega"), getIntent().getExtras().getInt("idProgramacao"))) {
            if (dao.existeProdutoDistribuido(d.getIdFilial(), d.getCodProduto()).size() > 1) {
                //pegando os valores iguais
                List<Distribuicao> listaBusca = dao.existeProdutoDistribuido(d.getIdFilial(), d.getCodProduto());
                Distribuicao distribuicao = new Distribuicao();
                BigDecimal soma = new BigDecimal("0");
                for (int i = 0; i < listaBusca.size(); i++) {
                    soma.add(listaBusca.get(i).getQtde());
                    distribuicao.setQtde(soma);
                    if (i > 0) {
                        //deletar distribuição
                        dao.deletarDistribuicao(listaBusca.get(i).getId());
                    }
                    distribuicao.setId(listaBusca.get(0).getId());
                    dao.alterar(distribuicao);


                }
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        tvInstituicao.setText(getIntent().getExtras().getString("nomeFilial"));
        String aux = String.format("%.3f", dao.totalDistribuidoInstituicao(getIntent().getExtras().getInt("idEntrega"), getIntent().getExtras().getInt("idProgramacao"))).replaceAll("[.]", ",");
        tvCodRomaneio.setText(dao.buscarCodRomaneio(getIntent().getExtras().getInt("idEntrega"), getIntent().getExtras().getInt("idProgramacao")));
        tvQtdeTotal.setText(aux);

        lista = dao.listarDistribuicaoFilial(getIntent().getExtras().getInt("idEntrega"), getIntent().getExtras().getInt("idProgramacao"));

        adapter = new EntregaAdapter(this, lista);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new EntregaAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent i = new Intent(EntregaActivity.this, ListaFilialActivity.class);
                i.putExtra("descricao", adapter.getItem(position).getProdutos().getNome());
                i.putExtra("qtde", adapter.getItem(position).getProdutos().getQtdeAbsoluta());
                i.putExtra("romaneioAltera", "EntregaActivity");
                i.putExtra("codProduto", adapter.getItem(position).getProdutos().getCod());
                i.putExtra("produto", adapter.getItem(position).getProdutos());
                startActivity(i);
                finish();

            }
        });

        for (Distribuicao d : lista) {
            if (d.getAssinado() == 1) {
                btAssinar.setImageResource(R.drawable.ic_assinar_gray);
                btAssinar.setClickable(false);
            } else {
                btAssinar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
                        Date hoje = new Date();
                        String codRomaneio = formato.format(hoje).replaceAll("[/]", "");
                        codRomaneio = codRomaneio + getIntent().getExtras().getInt("idProgramacao");

                        for (Distribuicao d : lista) {
                            d.setCodRomaneio(codRomaneio);
                            dao.inserirCodRomaneio(d);
                        }

                        if (VerificaConexao.isNetworkConnected(EntregaActivity.this)) {
                            sendEmail();
                            Toast.makeText(getApplicationContext(), "Enviando Email", Toast.LENGTH_SHORT).show();
                            AsyncEnviarEntrega task = new AsyncEnviarEntrega();
                            task.execute();
                            //dialogConfirma.show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Não esqueça de enviar as informações no Sync", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(getIntent());
                            Intent i = new Intent(EntregaActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                        btAssinar.setImageResource(R.drawable.ic_assinar_gray);
                        btAssinar.setClickable(false);
                        int statusFinalizaRomaneio = 0;
                        for (Distribuicao d : dao.listarDistribuicaoFilial(getIntent().getExtras().getInt("idEntrega"), getIntent().getExtras().getInt("idProgramacao"))) {
                            dao.alterarStatusAssinado(d);

                            for (Produtos p : produtosDao.listarTodosAuxProduto()) {
                                Produtos produtos = new Produtos();
                                if (d.getCodProduto() == p.getCod()) {
                                    produtos.setId(p.getId());
                                    produtos.setQtdeAbsoluta(p.getQtdeAbsoluta().subtract(d.getQtde()));
                                    produtosDao.alterarAuxAbsoluto(produtos);
                                }
                            }

                            if (d.getFinalizado() == 0) {
                                dao.alterarStatusFinalizado(d);
                                statusFinalizaRomaneio = 1;
                            }
                        }

                        if (statusFinalizaRomaneio == 1) {
                            Programacao p = new Programacao();
                            consultaDao = new ConsultaDao(EntregaActivity.this);
                            agendaDao = new AgendaDao(EntregaActivity.this);
                            p.setId(getIntent().getExtras().getInt("idProgramacao"));


                            if (getIntent().hasExtra("romaneioAltera") == false) {
                                consultaDao.incluir(p);
                                agendaDao.alterarProg(getIntent().getExtras().getInt("idProgramacao"));

                                Toast.makeText(getApplicationContext(), "Romaneio finalizado", Toast.LENGTH_SHORT).show();
                            }

                            //Pegando hora fim do romaneio entrega (assinatura)
                            SimpleDateFormat sdf = new SimpleDateFormat("HH : mm");
                            SimpleDateFormat sdfData = new SimpleDateFormat("yyyy/MM/dd");
                            horaFim = sdf.format(new Date());
                            dataFim = sdfData.format(new Date());
                            String dataInicio = getIntent().getExtras().getString("dataInicio");
                            String horaInicio = getIntent().getExtras().getString("horaInicio");
                            agendaDao.alterarDataHoraInicio(getIntent().getExtras().getInt("idProgramacao"), dataInicio, horaInicio);
                            agendaDao.alterarDataHoraFim(getIntent().getExtras().getInt("idProgramacao"), dataFim, horaFim);
                        }
                    }
                });
            }
        }

        if (lista.size() < 1) {
            btAssinar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EntregaActivity.this, "Não existem produtos", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class AsyncEnviarEntrega extends AsyncTask<String, Void, String> {

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
            agendaDao = new AgendaDao(EntregaActivity.this);
            SharedPreferences preferences = getSharedPreferences(MainActivity.PREF, MODE_PRIVATE);
            Programacao p = agendaDao.PegaProgramacaoAtual2(preferences.getInt("id", 0));

            for (Distribuicao d : dao.listarDistribuicaoFilial(p.getIdFilial(), getIntent().getExtras().getInt("idProgramacao"))) {
                try {
                    if (p.getSync() == 0) {
                        agendaDao.alteraSync(p.getId());
                        String aux = String.format("%.3f", d.getQtde());
                        ServicePostRomaneio.post(p.getRazaoSocial(), d.getProdutos().getCod(), d.getProdutos().getCategoria(),
                                d.getProdutos().getNome(), aux, p.getTipo(), p.getDataSaida(), p.getHoraSaida(), p.getDataChegada(), p.getHoraChegada(), preferences.getString("nome", ""));
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
                Intent i = new Intent(EntregaActivity.this, HomeActivity.class);
                startActivity(i);
            }
        }

    }

    private void sendEmail() {
        //Getting content for email

        String email = getIntent().getExtras().getString("email");
        //String email = "v.novais@outlook.com";
        String subject = "Confirmação de Recebimento de Doação de Alimentos";
        long codigo;
        codigo = (long) (100000 + Math.random() * 999999);

        message = codigo + "";
        String table = "<h3>Lista de alimentos recebidos através da Associação Prato Cheio por esta instituição:</h3><br>" +
                "<TABLE BORDER=1>" +
                "<tr><td><FONT size =\"4\" face=\"calibri light\"> Código </FONT></td>" +
                "<td><FONT size =\"4\" face=\"calibri light\">" + "Descrição" + "</FONT></td>" +
                "<td><FONT COLOR=\"#FF000\" size =\"4\" face=\"calibri light\">" + "Quantidade" + "</FONT></td>" +
                "<td><FONT COLOR=\"#FF000\" size =\"4\" face=\"calibri light\"> Peso</FONT></td></tr>";
        for (Distribuicao d : lista) {
            String unidMedida = "";
            if (d.getProdutos().getUnidMedida() == 1) {
                unidMedida = "Kg";
            } else {
                unidMedida = "Emb.";
            }
            if (d.getQtde().compareTo(new BigDecimal("0")) == 1) {
                String aux = String.format("%.3f", d.getQtde()).replaceAll("[.]", ",");
                table = table + "<tr><td><FONT size =\"4\" face=\"calibri light\">" + d.getProdutos().getCod() + "</FONT></td>" +
                        "<td><FONT size =\"4\" face=\"calibri light\">" + d.getProdutos().getNome() + "</FONT></td>" +
                        "<td><FONT COLOR=\"#FF000\" size =\"4\" face=\"calibri light\">" + aux + "</FONT></td>" +
                        "<td><FONT COLOR=\"#FF000\" size =\"4\" face=\"calibri light\">" + unidMedida + "</FONT></td></tr>";
            }
        }
        String total = String.format("%.3f", dao.totalDistribuidoInstituicao(getIntent().getExtras().getInt("idEntrega"), getIntent().getExtras().getInt("idProgramacao"))).replaceAll("[.]", ",");
        table = table + "<tr><td><FONT size =\"4\" face=\"calibri light\"> Total </FONT></td>" +
                "<td colspan=\"3\" align=\"right\"><FONT COLOR=\"#FF000\" size =\"4\" face=\"calibri light\" colspan=\"2\"> " + total + " Kg</FONT></td></tr>";


        //Creating SendMail object
        SendMailRomaneio sm = new SendMailRomaneio(this, email, subject, table, message);

        //Executing sendmail to send email
        sm.execute();
    }
}
