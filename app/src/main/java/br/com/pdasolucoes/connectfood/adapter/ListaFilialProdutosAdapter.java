package br.com.pdasolucoes.connectfood.adapter;

import android.content.Context;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.FloatProperty;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebHistoryItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

import br.com.pdasolucoes.connectfood.R;
import br.com.pdasolucoes.connectfood.dao.ProdutosDao;
import br.com.pdasolucoes.connectfood.model.Produtos;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by PDA on 04/05/2017.
 */

public class ListaFilialProdutosAdapter extends RecyclerView.Adapter<ListaFilialProdutosAdapter.MyViewHolder> {


    private Context context;
    private List<Produtos> lista;
    private static ListaFilialProdutosAdapter.ItemChangeListener itemChangeListener;
    private static ChangePosition changePosition;

    public interface ItemChangeListener {
        void onItemClick(String text, int position, BigDecimal qtdeEstoque);
    }

    public void setChangeTextListener(ListaFilialProdutosAdapter.ItemChangeListener itemChangeListener) {
        this.itemChangeListener = itemChangeListener;
    }

    public interface ChangePosition {
        void onItemPosition(int position);
    }

    public void setChangePosition(ChangePosition changePosition) {
        this.changePosition = changePosition;
    }

    public ListaFilialProdutosAdapter(Context context, List<Produtos> lista) {
        this.context = context;
        this.lista = lista;
        //layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public ListaFilialProdutosAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View v = layoutInflater.inflate(R.layout.list_filial_produto, parent, false);

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_filial_produto, parent, false);
        MyViewHolder mvh = new MyViewHolder(v);

        //mvh.setIsRecyclable(false);

        return mvh;
    }


    @Override
    public void onBindViewHolder(final ListaFilialProdutosAdapter.MyViewHolder holder, final int position) {

        final Produtos mItens = this.lista.get(position);

        //Nome da Instituição
        holder.tvDescricao.setText(mItens.getNome());

//            Trocar de item após mudar foco
        holder.editQtde.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    changePosition.onItemPosition(position);
                }
            }
        });

        try {
            BigDecimal valor = new BigDecimal(String.valueOf(mItens.getQtde()));
            BigDecimal qtde =  valor.subtract(new BigDecimal(String.valueOf(mItens.getQtdeDigitada()))) ;
            String aux2 = String.format("%.3f", qtde).replaceAll("[.]", ",");
            holder.tvEstoque.setText(aux2);


        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        holder.editQtde.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (holder.editQtde.getEditableText().toString().equals("")) {
                    mItens.setQtdeDigitada(new BigDecimal("0"));
                } else {
                    try {
                    String text = holder.editQtde.getText().toString();
                    text = text.replaceAll("[,]", ".");
                    mItens.setQtdeDigitada(new BigDecimal(text));


                        String aux = mItens.getQtdeDigitada() + "".replaceAll("[,]", ".");
                        BigDecimal auxBig = new BigDecimal(aux);
                        if (auxBig.compareTo(mItens.getQtde())==1) {
                            holder.editQtde.setText("0");
                            Toast.makeText(context, "Verifique se distribuiu corretamente", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                itemChangeListener.onItemClick(mItens.getQtdeDigitada() + "", position, new BigDecimal(String.valueOf(mItens.getQtde())));
            }
        });
    }

    public Produtos getItem(int position) {
        return lista.get(position);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public long getItemId(int position) {
        return lista.get(position).getId();
    }

    public static class MyViewHolder extends ViewHolder {

        public TextView tvDescricao, tvEstoque;
        private EditText editQtde;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvDescricao = (TextView) itemView.findViewById(R.id.tvDescricao);
            tvEstoque = (TextView) itemView.findViewById(R.id.tvEstoque);
            editQtde = (EditText) itemView.findViewById(R.id.editQtdeDistribuir);

        }
    }
}
