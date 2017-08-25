package br.com.pdasolucoes.connectfood.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import br.com.pdasolucoes.connectfood.R;
import br.com.pdasolucoes.connectfood.dao.DistribuicaoDao;
import br.com.pdasolucoes.connectfood.model.Distribuicao;
import br.com.pdasolucoes.connectfood.model.Programacao;

/**
 * Created by PDA on 02/03/2017.
 */

public class ListaFilialDistribuir extends RecyclerView.Adapter<ListaFilialDistribuir.MyViewHolder> {
    private Context context;
    private List<Programacao> lista;
    private LayoutInflater layoutInflater;
    private static ItemChangeListener itemChangeListener;

    public interface ItemChangeListener {
        void onItemClick(String text, int position);
    }

    public void setChangeTextListener(ItemChangeListener itemChangeListener) {
        this.itemChangeListener = itemChangeListener;
    }

    public ListaFilialDistribuir(Context context, List<Programacao> lista) {
        this.context = context;
        this.lista = lista;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.list_item_filial_distribuicao, parent, false);
        ListaFilialDistribuir.MyViewHolder mvh = new ListaFilialDistribuir.MyViewHolder(v);

        //mvh.setIsRecyclable(false);

        return mvh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        //holder.setIsRecyclable(false);
        holder.tvNome.setText(lista.get(position).getRazaoSocial().toString());

        holder.editQte.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (holder.editQte.getEditableText().toString().equals("")) {
                    itemChangeListener.onItemClick("0", position);
                } else {
                    itemChangeListener.onItemClick(holder.editQte.getText().toString(), position);
                }
            }
        });
    }

    public Programacao getItem(int position) {
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNome;
        public EditText editQte;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvNome = (TextView) itemView.findViewById(R.id.tvNomeFilial);
            editQte = (EditText) itemView.findViewById(R.id.editQtdeDistribuir);
        }
    }
}
