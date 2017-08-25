package br.com.pdasolucoes.connectfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.pdasolucoes.connectfood.adapter.ListaFilialDistribuir;
import br.com.pdasolucoes.connectfood.dao.AgendaDao;
import br.com.pdasolucoes.connectfood.dao.DistribuicaoDao;
import br.com.pdasolucoes.connectfood.dao.ProdutosDao;
import br.com.pdasolucoes.connectfood.model.Distribuicao;
import br.com.pdasolucoes.connectfood.model.Produtos;
import br.com.pdasolucoes.connectfood.model.Programacao;

import static br.com.pdasolucoes.connectfood.R.id.recyclerView;

/**
 * Created by PDA on 02/03/2017.
 */

public class ListaFilialActivity extends AppCompatActivity {

    private AgendaDao daoAgenda;
    private ListaFilialDistribuir adapterFilial;
    private RecyclerView recyclerViewFilial;
    private List<Programacao> listaProgramacao;
    private TextView tvDescricao, tvQtde;
    BigDecimal qtde = new BigDecimal("0");
    int verificacao = 0;
    float[] vetor;
    private Button btSalvar, btHome;
    private DistribuicaoDao distribuicaoDao;
    private ProdutosDao daoProdutos;
    Distribuicao d = new Distribuicao();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        setContentView(R.layout.activity_lista_filial);

        daoAgenda = new AgendaDao(this);
        distribuicaoDao = new DistribuicaoDao(this);
        daoProdutos = new ProdutosDao(this);
        final SharedPreferences preferences = getSharedPreferences(MainActivity.PREF, MODE_PRIVATE);
        listaProgramacao = daoAgenda.listarRecebedores(preferences.getInt("id", 0));

        tvDescricao = (TextView) findViewById(R.id.tvDescricao);
        tvQtde = (TextView) findViewById(R.id.tvQtde);
        btSalvar = (Button) findViewById(R.id.btSalvar);
        btHome = (Button) findViewById(R.id.btHome);
        tvDescricao.setText(getIntent().getExtras().getString("descricao"));
        qtde = new BigDecimal(String.valueOf(getIntent().getExtras().get("qtde")));

        String aux = String.format("%.3f", qtde).replaceAll("[.]", ",");
        tvQtde.setText(aux);

        recyclerViewFilial = (RecyclerView) findViewById(R.id.recyclerViewFilial);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewFilial.setLayoutManager(llm);

        adapterFilial = new ListaFilialDistribuir(this, listaProgramacao);
        recyclerViewFilial.setAdapter(adapterFilial);


        //pegando valor inserido na lista
        adapterFilial.setChangeTextListener(new ListaFilialDistribuir.ItemChangeListener() {
            @Override
            public void onItemClick(String text, int position) {

                String aux = text.replaceAll("[,]", ".");
                BigDecimal soma = new BigDecimal("0"), resultado;
                try {
                    vetor[position] = Float.parseFloat(aux);

                    for (int i = 0; i < vetor.length; i++) {

                        soma = soma.add(new BigDecimal(vetor[i] + ""));
                    }


                    resultado = qtde.subtract(soma);
                    String aux2 = String.format("%.3f", resultado).replaceAll("[.]", ",");
                    tvQtde.setText(aux2);

                    if (resultado.compareTo(new BigDecimal("0")) == 1) {
                        //Toast.makeText(getApplicationContext(), "Faltam " + resultado + " produtos para distribuir", Toast.LENGTH_SHORT).show();
                        verificacao = 0;
                    } else if (new BigDecimal("0").compareTo(resultado) == 1) {
                        //Toast.makeText(getApplicationContext(), "Excesso de " + resultado + " produtos distribuidos", Toast.LENGTH_SHORT).show();
                        verificacao = 0;
                    } else {
//                        if (getIntent().hasExtra("romaneioAltera")) {
//                            Toast.makeText(getApplicationContext(), "Distribuição OK", Toast.LENGTH_SHORT).show();
//                            verificacao = 2;
//                        } else {
                        Toast.makeText(getApplicationContext(), "Distribuição OK", Toast.LENGTH_SHORT).show();
                        verificacao = 1;
                        //}

                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        });


        btSalvar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (verificacao == 0) {
                                                Toast.makeText(getApplicationContext(), "Verifique se distribuiu corretamente", Toast.LENGTH_SHORT).show();
                                            }
                                            if (verificacao > 0) {
                                                if (verificacao == 1) {
                                                    int i = 0;
                                                    BigDecimal qtdeProduto = ((Produtos) getIntent().getSerializableExtra("produto")).getQtdeAbsoluta();
                                                    for (Programacao p : daoAgenda.listarRecebedores(preferences.getInt("id", 0))) {

                                                        int cod = ((Produtos) getIntent().getSerializableExtra("produto")).getCod();
                                                        int idProduto = ((Produtos) getIntent().getSerializableExtra("produto")).getId();

                                                        Produtos produto = new Produtos();
                                                        produto.setId(idProduto);
                                                        qtdeProduto = qtdeProduto.subtract(new BigDecimal(vetor[i]));
                                                        produto.setQtde(qtdeProduto);

                                                        List<Distribuicao> listaBusca = distribuicaoDao.existeProdutoDistribuido(p.getIdFilial(), cod);
                                                        //alterando produto distribuido a filial
                                                        if (listaBusca.size() > 0) {

                                                            Distribuicao distribuicao = new Distribuicao();
                                                            distribuicao.setId(listaBusca.get(0).getId());
                                                            distribuicao.setQtde(new BigDecimal(vetor[i]));
                                                            distribuicaoDao.alterar(distribuicao);

                                                        } else {
                                                            //adiciona produto para uma nova filial
                                                            d.setIdFilial(p.getIdFilial());
                                                            d.setQtde(new BigDecimal(vetor[i]));
                                                            d.setProdutos((Produtos) getIntent().getSerializableExtra("produto"));
                                                            d.setIdProgramacao(p.getId());

                                                            distribuicaoDao.incluir(d);
                                                        }

                                                        //se o valor digitado for maior que zero, já pode mudar o status da programação para efetuado
                                                        //que quer dizer que vai exister esse produto distribuido
                                                        if (vetor[i] > 0) {
                                                            daoAgenda.alterarEfetuada(p.getId());
                                                        }
                                                        //verifica a distribuicao da instituicao e programação pra ver se existe algum produto maior ou igual a zero
                                                        //se existir, também altera a programção para efetuada
                                                        //se não existir, faz com que volte para não efetuada
                                                        if (distribuicaoDao.existeAlgumProdutoDistribuido(p.getIdFilial(), p.getId())) {
                                                            daoAgenda.alterarEfetuada(p.getId());
                                                        } else {
                                                            daoAgenda.alterarNãoEfetuada(p.getId());
                                                        }


                                                        i++;

                                                        daoProdutos.alterarAux(produto);
                                                    }
                                                    Toast.makeText(getApplicationContext(), "Salvo", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(ListaFilialActivity.this, AgendaActivity.class);
                                                    intent.putExtra("tipo", 3);
                                                    startActivity(intent);
                                                    finish();
                                                } else if (verificacao == 2) {
//                                                    int i = 0;
//                                                    for (Distribuicao d : distribuicaoDao.listarIdParaAlterar(getIntent().getExtras().getInt("codProduto"))) {
//
//                                                        d.setId(d.getId());
//                                                        d.setQtde(new BigDecimal(vetor[i]));
//
//                                                        distribuicaoDao.alterar(d);
//                                                        i++;
//                                                    }

                                                }

//                                                Produtos p = new Produtos();
//                                                p.setId(getIntent().getExtras().getInt("idProgramacao"));
//                                                daoProdutos.alterarDistribuicao(p);
                                            }
                                        }
                                    }


        );

        btHome.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(View v) {
                                          Intent i = new Intent(ListaFilialActivity.this, HomeActivity.class);
                                          startActivity(i);
                                          finish();
                                      }
                                  }

        );

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(MainActivity.PREF, MODE_PRIVATE);
        vetor = new float[daoAgenda.listarRecebedores(preferences.getInt("id", 0)).size()];
    }
}
