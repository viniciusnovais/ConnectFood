package br.com.pdasolucoes.connectfood;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.FloatRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import br.com.pdasolucoes.connectfood.adapter.ListaProdutosAdapter;
import br.com.pdasolucoes.connectfood.adapter.RomaneioAdapter;
import br.com.pdasolucoes.connectfood.dao.AgendaDao;
import br.com.pdasolucoes.connectfood.dao.ConsultaDao;
import br.com.pdasolucoes.connectfood.dao.ProdutosDao;
import br.com.pdasolucoes.connectfood.model.Consulta;
import br.com.pdasolucoes.connectfood.model.Produtos;
import br.com.pdasolucoes.connectfood.model.Programacao;
import br.com.pdasolucoes.connectfood.util.ServiceProdutos;
import br.com.pdasolucoes.connectfood.util.VerificaConexao;

/**
 * Created by PDA on 16/02/2017.
 */

public class ListaProdutosActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    private List<Produtos> lista;
    private List<String> listaCategoria, listaProdutos;
    private List<Integer> listaCodigos, listaInt;
    private Spinner spinnerCategoria, spinnerDescricao;
    private static RecyclerView recyclerView;
    private ListaProdutosAdapter adapter;
    private Button btAgenda, btFinalizar;
    private ProdutosDao dao = new ProdutosDao(this);
    private EditText editQtde, editQtdeAltera, editCodigo;
    private TextView tvKgUn;
    private ImageButton btSalvar;
    private Produtos p = new Produtos();
    private ConsultaDao consultaDao;
    private AgendaDao agendaDao;
    private AlertDialog dialogAltExc, dialogSimNao, dialogAltera, dialogProgress;
    private Button btAltera;
    private String produto;
    private ArrayAdapter<String> arrayDescricao, spinnerAdapterCategoria;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_coleta);
        setContentView(R.layout.activity_lista_produtos);

        spinnerCategoria = (Spinner) findViewById(R.id.spinnerCategoria);
        editCodigo = (EditText) findViewById(R.id.editCodigo);
        spinnerDescricao = (Spinner) findViewById(R.id.spinnerDescricao);
        editQtde = (EditText) findViewById(R.id.editQtde);
        //editCategoria = (EditText) findViewById(R.id.editCategoria);
        //editDescricao = (EditText) findViewById(R.id.editDescricao);
        //tvKgUn = (TextView) findViewById(R.id.kg_un);

        AlertDialog.Builder builderProgress = new AlertDialog.Builder(ListaProdutosActivity.this);
        View vProgress = View.inflate(ListaProdutosActivity.this, R.layout.progress_bar, null);
        builderProgress.setView(vProgress);
        dialogProgress = builderProgress.create();
        dialogProgress.setCanceledOnTouchOutside(false);
        dialogProgress.setCancelable(false);

        //DIALOG ALTERAR EXCLUIR
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] itens = new String[]{getString(R.string.alterar), getString(R.string.excluir)};
        builder.setItems(itens, ListaProdutosActivity.this);
        dialogAltExc = builder.create();

        //DIALOG SIM E NÃO
        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirma_excluir);
        builder.setPositiveButton(getString(R.string.sim), this);
        builder.setNegativeButton(getString(R.string.nao), this);
        dialogSimNao = builder.create();

        builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.altera_qtde, null);
        editQtdeAltera = (EditText) v.findViewById(R.id.editQtdeAltera);
        editQtdeAltera.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editQtdeAltera.setKeyListener(DigitsKeyListener.getInstance("01234567890,"));
        btAltera = (Button) v.findViewById(R.id.btAlteraAQtde);

        btAltera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editQtdeAltera.getEditableText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Insira a Quantidade", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String aux = editQtdeAltera.getEditableText().toString().replaceAll("[,]", ".");
                        p.setQtde(new BigDecimal(String.valueOf(aux)));
                        AlteraQtde(p);
                        editQtdeAltera.setText("");
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "Formato Incorreto", Toast.LENGTH_SHORT).show();
                        editQtdeAltera.setText("");
                    }

                }
            }
        });

        builder.setView(v);
        dialogAltera = builder.create();

        btAgenda = (Button) findViewById(R.id.btAgenda);
        btAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListaProdutosActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        btFinalizar = (Button) findViewById(R.id.btFinalizar);
        btFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Programacao programacao = new Programacao();
                consultaDao = new ConsultaDao(ListaProdutosActivity.this);
                agendaDao = new AgendaDao(ListaProdutosActivity.this);
                Toast.makeText(getApplicationContext(), "Coleta Realizada", Toast.LENGTH_SHORT).show();
                programacao.setId(getIntent().getExtras().getInt("idProgramacao"));

                if (getIntent().hasExtra("romaneioAltera") == false) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    String data = sdf.format(new Date());

                    if (data.equals(getIntent().getExtras().getString("dataSaida"))) {
                        consultaDao.incluir(programacao);
                        agendaDao.alterarProg(getIntent().getExtras().getInt("idProgramacao"));
                    }
                }
                Intent i = new Intent(ListaProdutosActivity.this, RomaneioActivity.class);
                i.putExtra("idProgramacao", getIntent().getExtras().getInt("idProgramacao"));
                i.putExtra("idFilial", getIntent().getExtras().getInt("idDoa"));
                i.putExtra("dataInicio", getIntent().getExtras().getString("dataInicio"));
                i.putExtra("horaInicio", getIntent().getExtras().getString("horaInicio"));
                i.putExtra("email", getIntent().getExtras().getString("email"));
                i.putExtra("nomeFilial", getIntent().getExtras().getString("nomeFilial"));
                startActivity(i);
                finish();

            }
        });

    }

    public void AlteraQtde(Produtos p) {
        dao.alterar(p);
        dialogAltera.dismiss();
        recarregaLista();
    }

    @Override
    protected void onStart() {
        super.onStart();

        listaCategoria = new ArrayList<>();
        listaCodigos = new ArrayList<>();
        listaProdutos = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btSalvar = (ImageButton) findViewById(R.id.btSalvar);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        p.setIdFilial(getIntent().getExtras().getInt("idDoa"));

        adapter = new ListaProdutosAdapter(ListaProdutosActivity.this, dao.listarDoacao(getIntent().getExtras().getInt("idDoa"), getIntent().getExtras().getInt("idProgramacao")));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ListaProdutosAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {

                p = adapter.getItem(position);

                if (p.getUnidMedida() == 1) {
                    editQtdeAltera.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                } else {
                    editQtdeAltera.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

                dialogAltExc.show();
            }
        });

        btSalvar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                if (editQtde.getEditableText().toString().equals("")
                                                        || p.getCategoria().equals("----")
                                                        || p.getNome().toString().equals("----")) {
                                                    Toast.makeText(getApplicationContext(), "Campo vazio, insira corretamente", Toast.LENGTH_SHORT).show();
                                                } else {

                                                    String aux = editQtde.getEditableText().toString().replaceAll("[,]", ".");
                                                    if (Float.parseFloat(aux) > 999.999) {
                                                        Toast.makeText(getApplicationContext(), "Valor excedente", Toast.LENGTH_SHORT).show();
                                                        editQtde.setText("");
                                                    } else {
                                                        p.setQtde(new BigDecimal(String.valueOf(aux)));
                                                        p.setIdProgramacao(getIntent().getExtras().getInt("idProgramacao"));
                                                        adapter.insert(p);

                                                        if (dao.existeProduto(p).size() > 0) {

                                                            BigDecimal soma = (dao.existeProduto(p).get(0).getQtde().add(p.getQtde()));

                                                            Produtos produtoAltera = new Produtos();
                                                            produtoAltera.setId(dao.existeProduto(p).get(0).getId());
                                                            produtoAltera.setQtde(soma);

                                                            dao.alterar(produtoAltera);

                                                        } else {
                                                            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

                                                            Date hoje = new Date();
                                                            dao.incluirNaLista(p);

                                                            //dao.incluirNaListaAux(p);
                                                        }

                                                        adapter = new ListaProdutosAdapter(ListaProdutosActivity.this, dao.listarDoacao(getIntent().getExtras().getInt("idDoa"), getIntent().getExtras().getInt("idProgramacao")));
                                                        recyclerView.setAdapter(adapter);

                                                        editQtde.setText("");
                                                    }
                                                }
                                            } catch (NumberFormatException n) {
                                                Toast.makeText(getApplicationContext(), "Formato Incorreto", Toast.LENGTH_SHORT).show();
                                                editQtde.setText("");
                                            }

                                            editCodigo.setText("");
                                            //editDescricao.setVisibility(View.VISIBLE);
                                            //editCategoria.setVisibility(View.VISIBLE);
                                            hideKeyboard(ListaProdutosActivity.this);

                                            spinnerAdapterCategoria = new ArrayAdapter<String>
                                                    (ListaProdutosActivity.this, R.layout.custom_spinner, listaCategoria) {
                                                @Override
                                                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                                    return super.getDropDownView(position, convertView, parent);
                                                }

                                                @Override
                                                public int getCount() {
                                                    return listaCategoria.size();
                                                }

                                            };
                                            spinnerAdapterCategoria.setDropDownViewResource(R.layout.spinner_item);
                                            spinnerCategoria.setAdapter(spinnerAdapterCategoria);
                                        }
                                    }

        );

        AsyncProdutos task = new AsyncProdutos();
        task.execute();

    }

    public void recarregaLista() {
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case 0:
                dialogAltera.show();
                break;
            case 1:
                dialogSimNao.show();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                dao.deleteProdutoDoacao(Integer.parseInt(p.getCod() + ""));
                recarregaLista();
                break;
            case DialogInterface.BUTTON_NEGATIVE:

                break;
            default:
                break;
        }
    }

//    @Override
//    public void run() {
//        LinearLayout ll = (LinearLayout) findViewById(R.id.carregar);
//        ll.setVisibility(View.GONE);
//        linearLayout.setVisibility(View.VISIBLE);
//    }


    public class AsyncProdutos extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialogProgress.show();

        }

        @Override
        protected Object doInBackground(Object[] params) {


            if (VerificaConexao.isNetworkConnected(ListaProdutosActivity.this)) {
                lista = ServiceProdutos.Produtos();
                listaInt = new ArrayList<>();
                for (Produtos p : lista) {
                    listaInt.add(p.getId());
                }
                dao.deleteProduto(listaInt);
            } else {
                //lista = dao.listar(getIntent().getExtras().getInt("idDoa"));
                lista = dao.listar();
            }

            //Incluindo todos os produtos referentes a filial doadora, pra serem coletados
            dao.incluir(lista);


            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (dialogProgress.isShowing()) {
                dialogProgress.dismiss();
            }
            //será usando quando o usarmos o código
            //Buscando por Código
//            editCodigo.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    List<Produtos> produtoLista;
//                    spinnerDescricao.setVisibility(View.GONE);
//                    spinnerCategoria.setVisibility(View.GONE);
//                    //editDescricao.setText("");
//                    //editCategoria.setText("");
//                    tvKgUn.setVisibility(View.GONE);
//                    try {
//                        if (!editCodigo.getText().toString().equals("") && editCodigo.getText().length() == 6) {
//
////                            produtoLista = dao.buscarProdutoPorCod2(Integer.parseInt(editCodigo.getText().toString()),
////                                    getIntent().getExtras().getInt("idDoa"));
//
//                            produtoLista = dao.buscarProdutoPorCod2(Integer.parseInt(editCodigo.getText().toString()));
//
//                            //Setando categoria na edit
//                            //editCategoria.setVisibility(View.VISIBLE);
//                            //editCategoria.setText(produtoLista.get(0).getCategoria());
//
//                            //Setando descrição do produto na edit
//                            //editDescricao.setVisibility(View.VISIBLE);
//                            //editDescricao.setText(produtoLista.get(0).getNome());
//
//                            p.setCod(Integer.parseInt(editCodigo.getText().toString()));
//                            produto = editDescricao.getText().toString();
//                            p.setNome(produto);
//                            p.setUnidMedida(produtoLista.get(0).getUnidMedida());
//
//                            if (produtoLista.get(0).getUnidMedida() == 1) {
//                                tvKgUn.setVisibility(View.VISIBLE);
//                                tvKgUn.setText("Kg");
//                            } else {
//                                tvKgUn.setVisibility(View.VISIBLE);
//                                tvKgUn.setText("Un");
//                                editQtde.setInputType(InputType.TYPE_CLASS_NUMBER);
//                            }
//                        }
//                    } catch (IndexOutOfBoundsException e) {
//                        Toast.makeText(getApplicationContext(), "Produto não encontrado", Toast.LENGTH_SHORT).show();
//                        editDescricao.setText("");
//                        //editCategoria.setText("");
//                    }
//
//                    return false;
//                }
//            });

            //Habilitando os spinner
//            editCategoria.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    editCategoria.setVisibility(View.GONE);
//                    editDescricao.setVisibility(View.VISIBLE);
//                    spinnerCategoria.setVisibility(View.VISIBLE);
//                    //spinnerDescricao.setVisibility(View.VISIBLE);
//                }
//            });

//            editDescricao.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //editCategoria.setVisibility(View.GONE);
//                    editDescricao.setVisibility(View.GONE);
//                    spinnerCategoria.setVisibility(View.VISIBLE);
//                    spinnerDescricao.setVisibility(View.VISIBLE);
//                }
//            });


//            for (Produtos p : dao.listar(getIntent().getExtras().getInt("idDoa"))) {
            listaCategoria.add("----");
            for (Produtos p : dao.listar()) {

                //verificação para não repetir categoria no spinner
                if (listaCategoria.contains(p.getCategoria()) == false) {
                    listaCategoria.add(p.getCategoria());
                }

            }


            spinnerAdapterCategoria = new ArrayAdapter<String>
                    (ListaProdutosActivity.this, R.layout.custom_spinner, listaCategoria) {
                @Override
                public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    return super.getDropDownView(position, convertView, parent);
                }

                @Override
                public int getCount() {
                    return listaCategoria.size();
                }
            };
            spinnerAdapterCategoria.setDropDownViewResource(R.layout.spinner_item);
            spinnerCategoria.setAdapter(spinnerAdapterCategoria);


            //Listando todas as categorias e trazendo os respectivos código e nomes dos produtos
            spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //pegando categoria
                    //editDescricao.setVisibility(View.VISIBLE);
                    // editCodigo.setText("");
                    p.setCategoria(parent.getItemAtPosition(position).toString());
                    //Populando a lista com os produtos refentes a aquelas categorias
                    listaProdutos = new ArrayList<>();
//                    for (Produtos p : dao.buscarProdutoPorCategoria(parent.getItemAtPosition(position).toString(),
//                            getIntent().getExtras().getInt("idDoa"))) {
                    listaProdutos.add("----");
                    for (Produtos p : dao.buscarProdutoPorCategoria(parent.getItemAtPosition(position).toString())) {
                        listaProdutos.add(p.getNome());
                    }
                    arrayDescricao = new ArrayAdapter<>
                            (ListaProdutosActivity.this, R.layout.custom_spinner, listaProdutos);
                    arrayDescricao.setDropDownViewResource(R.layout.spinner_item);
                    spinnerDescricao.setAdapter(arrayDescricao);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            spinnerDescricao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    produto = parent.getItemAtPosition(position).toString();
                    p.setNome(produto);
//                    editCodigo.setText(dao.buscarCodPorProduto(parent.getItemAtPosition(position).toString(),
//                            getIntent().getExtras().getInt("idDoa"))+"");
                    editCodigo.setText(dao.buscarCodPorProduto(parent.getItemAtPosition(position).toString()) + "");
                    p.setCod(Integer.parseInt(editCodigo.getText().toString()));

//                    p.setUnidMedida(dao.buscarUnidadePorCod(Integer.parseInt(editCodigo.getText().toString()),
//                            getIntent().getExtras().getInt("idDoa")));

                    p.setUnidMedida(dao.buscarUnidadePorCod(Integer.parseInt(editCodigo.getText().toString())));

                    if (p.getUnidMedida() == 1) {
                        editQtde.setHint("Kg");
                        //editQtde.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    } else if (p.getUnidMedida() == 2) {
                        editQtde.setHint("Emb");
                        editQtde.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
