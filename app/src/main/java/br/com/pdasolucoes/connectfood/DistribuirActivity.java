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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.pdasolucoes.connectfood.adapter.DistribuirAdapter;
import br.com.pdasolucoes.connectfood.adapter.DistribuirFilialAdapter;
import br.com.pdasolucoes.connectfood.adapter.ListaFilialDistribuir;
import br.com.pdasolucoes.connectfood.adapter.ListaProdutosAdapter;
import br.com.pdasolucoes.connectfood.dao.AgendaDao;
import br.com.pdasolucoes.connectfood.dao.ProdutosDao;
import br.com.pdasolucoes.connectfood.model.Produtos;
import br.com.pdasolucoes.connectfood.model.Programacao;

/**
 * Created by PDA on 20/02/2017.
 */

public class DistribuirActivity extends AppCompatActivity {

    private List<Produtos> lista;
    private RecyclerView recyclerView, recyclerViewInstituicao;
    private DistribuirAdapter adapter;
    private DistribuirFilialAdapter adapterInstituicao;
    private Switch aSwitch;
    private AgendaDao daoAgenda;
    private ProdutosDao dao;
    private TableLayout tlInstituicao, tlProdutos;
    private TextView tvTituloInstituicao;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_distribuicao);
        setContentView(R.layout.activity_distribuir);

        dao = new ProdutosDao(this);
        daoAgenda = new AgendaDao(this);
        final SharedPreferences preferences = getSharedPreferences(MainActivity.PREF, MODE_PRIVATE);
        lista = dao.listarTodosAuxProduto(preferences.getInt("id", 0));
        List<Programacao> listaFilialProgramacao;

        listaFilialProgramacao = daoAgenda.listarRecebedoresNaoEfetuada(preferences.getInt("id", 0));

        aSwitch = (Switch) findViewById(R.id.switchDistribuicao);
//        tvTituloInstituicao = (TextView) findViewById(R.id.tvTituloDistribuicao);
        tlInstituicao = (TableLayout) findViewById(R.id.tableInstituicao);
        tlProdutos = (TableLayout) findViewById(R.id.tableProduto);

        //produtos
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(DistribuirActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        adapter = new DistribuirAdapter(DistribuirActivity.this, lista);
        recyclerView.setAdapter(adapter);

        //Instituicao
        recyclerViewInstituicao = (RecyclerView) findViewById(R.id.recyclerViewIntituicao);

        LinearLayoutManager llm2 = new LinearLayoutManager(this);
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewInstituicao.setLayoutManager(llm2);

        adapterInstituicao = new DistribuirFilialAdapter(this, listaFilialProgramacao);
        recyclerViewInstituicao.setAdapter(adapterInstituicao);

        adapterInstituicao.setOnItemClickListener(new DistribuirFilialAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent i = new Intent(DistribuirActivity.this, ListaFilialProdutoActivity.class);
                i.putExtra("idFilial", adapterInstituicao.getItem(position).getIdFilial());
                i.putExtra("nomeInstituicao", adapterInstituicao.getItem(position).getRazaoSocial());
                i.putExtra("idProgramacao", adapterInstituicao.getItem(position).getId());
                startActivity(i);
                finish();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    //Produto
                    recyclerViewInstituicao.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
//                    tvTituloInstituicao.setText(R.string.distribuicao_produtos);
                    tlInstituicao.setVisibility(View.GONE);
                    tlProdutos.setVisibility(View.VISIBLE);

                    adapter.setOnItemClickListener(new DistribuirAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(int position) {

                            if (adapter.getItem(position).getDistribuido() == 1) {
                                Toast.makeText(getApplicationContext(), "Produto Distribuido", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent i = new Intent(DistribuirActivity.this, ListaFilialActivity.class);
                                i.putExtra("id", adapter.getItem(position).getId());
                                i.putExtra("descricao", adapter.getItem(position).getNome());
                                i.putExtra("qtde", adapter.getItem(position).getQtdeAbsoluta());
                                i.putExtra("produto", adapter.getItem(position));
                                startActivity(i);
                                finish();
                            }
                        }
                    });
                } else {

                    recyclerViewInstituicao.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
//                    tvTituloInstituicao.setText(R.string.distribuicao_instituicao);
                    tlInstituicao.setVisibility(View.VISIBLE);
                    tlProdutos.setVisibility(View.GONE);

                    adapterInstituicao.setOnItemClickListener(new DistribuirFilialAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Intent i = new Intent(DistribuirActivity.this, ListaFilialProdutoActivity.class);
                            Toast.makeText(DistribuirActivity.this, "position" + position, Toast.LENGTH_SHORT).show();
                            i.putExtra("idFilial", adapterInstituicao.getItem(position).getIdFilial());
                            i.putExtra("nomeInstituicao", adapterInstituicao.getItem(position).getRazaoSocial());
                            i.putExtra("idProgramacao", adapterInstituicao.getItem(position).getId());
                            startActivity(i);
                            finish();
                        }
                    });
                }
            }
        });


    }

}
