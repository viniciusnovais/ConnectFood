package br.com.pdasolucoes.connectfood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import br.com.pdasolucoes.connectfood.adapter.ListaFilialProdutosAdapter;
import br.com.pdasolucoes.connectfood.adapter.ListaFilialProdutosAdapterTeste;
import br.com.pdasolucoes.connectfood.dao.AgendaDao;
import br.com.pdasolucoes.connectfood.dao.DistribuicaoDao;
import br.com.pdasolucoes.connectfood.dao.ProdutosDao;
import br.com.pdasolucoes.connectfood.model.Distribuicao;
import br.com.pdasolucoes.connectfood.model.Produtos;

/**
 * Created by PDA on 03/05/2017.
 */

public class ListaFilialProdutoActivity extends AppCompatActivity {

    private TextView tvNomeFilial;
    private ListaFilialProdutosAdapter adapter;
    private RecyclerView recyclerView;
    private List<Produtos> lista;
    private ProdutosDao dao;
    private AgendaDao agendaDao;
    private Button btSalvar, btInicio;
    private DistribuicaoDao distribuicaoDao;
    int[] verificacao;
    float[] vetor;
    LinearLayoutManager llm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        setContentView(R.layout.activity_filial_produto);

        dao = new ProdutosDao(this);
        agendaDao = new AgendaDao(this);
        distribuicaoDao = new DistribuicaoDao(this);

        tvNomeFilial = (TextView) findViewById(R.id.tvNomeFilial);
        btSalvar = (Button) findViewById(R.id.btSalvar);
        btInicio = (Button) findViewById(R.id.btHome);

        tvNomeFilial.setText(getIntent().getExtras().getString("nomeInstituicao"));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewProdutoFilial);

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        lista = dao.listarTodosAuxFilial();
        adapter = new ListaFilialProdutosAdapter(this, lista);
        recyclerView.setAdapter(adapter);


        final RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        adapter.setChangePosition(new ListaFilialProdutosAdapter.ChangePosition() {
            @Override
            public void onItemPosition(int position) {
                smoothScroller.setTargetPosition(position);
                llm.startSmoothScroll(smoothScroller);
            }
        });


        adapter.setChangeTextListener(new ListaFilialProdutosAdapter.ItemChangeListener() {

            @Override
            public void onItemClick(String text, int position, BigDecimal qtdeTotal) {

                adapter.notifyItemChanged(position, adapter.getItem(position));
                String aux = text.replaceAll("[,]", ".");
                try {
                    vetor[position] = Float.parseFloat(aux);

                    if (getIntent().hasExtra("romaneioAltera")) {
                        verificacao[position] = 2;
                    } else {
                        verificacao[position] = 1;
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            }
        });


        btSalvar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            for (int i = 0; i < verificacao.length; i++) {
                                                if (verificacao[i] == 0) {
                                                }
                                                if (verificacao[i] > 0) {
                                                    if (verificacao[i] == 1) {

                                                        Produtos p = new Produtos();
                                                        p.setId(adapter.getItem(i).getId());
                                                        p.setQtde(adapter.getItem(i).getQtde().subtract(new BigDecimal(vetor[i])));
                                                        p.setCod(adapter.getItem(i).getCod());

                                                        List<Distribuicao> listaBusca = distribuicaoDao.existeProdutoDistribuido(getIntent().getExtras().getInt("idFilial"), p.getCod());
                                                        //alterando produto distribuido a filial
                                                        if (listaBusca.size() > 0) {

                                                            Distribuicao distribuicao = new Distribuicao();
                                                            distribuicao.setId(listaBusca.get(0).getId());
                                                            distribuicao.setQtde(new BigDecimal(vetor[i]));
                                                            distribuicaoDao.alterar(distribuicao);

                                                        } else {
                                                            Distribuicao d = new Distribuicao();

                                                            d.setIdFilial(getIntent().getExtras().getInt("idFilial"));
                                                            d.setQtde(new BigDecimal(vetor[i]));
                                                            d.setProdutos(adapter.getItem(i));
                                                            d.setIdProgramacao(getIntent().getExtras().getInt("idProgramacao"));

                                                            distribuicaoDao.incluir(d);
                                                            agendaDao.alterarEfetuada(getIntent().getExtras().getInt("idProgramacao"));
                                                        }

                                                        dao.alterarAux(p);


                                                    } else if (verificacao[i] == 2) {
//                                                    int i = 0;
//                                                    for (Distribuicao d : distribuicaoDao.listarIdParaAlterar(getIntent().getExtras().getInt("codProduto"))) {
//
//                                                        d.setId(d.getId());
//                                                        d.setQtde(vetor[i]);
//
//                                                        distribuicaoDao.alterar(d);
//                                                        i++;
//                                                    }

                                                    }
                                                }
                                            }
                                            Toast.makeText(getApplicationContext(), "Salvo", Toast.LENGTH_SHORT).show();
                                            finish();
                                            //                                                Produtos p = new Produtos();
//                                                p.setId(getIntent().getExtras().getInt("id"));
//                                                daoProdutos.alterarDistribuicao(p);
//                                                finish();
                                        }
                                    }


        );

        btInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ListaFilialProdutoActivity.this, HomeActivity.class);
                startActivity(i);
                finish();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        vetor = new float[dao.listarTodosAuxFilial().size()];
        verificacao = new int[dao.listarTodosAuxFilial().size()];
    }

}
