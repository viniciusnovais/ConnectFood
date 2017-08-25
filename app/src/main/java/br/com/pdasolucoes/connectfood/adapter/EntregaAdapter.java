package br.com.pdasolucoes.connectfood.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.pdasolucoes.connectfood.R;
import br.com.pdasolucoes.connectfood.model.Distribuicao;

/**
 * Created by PDA on 20/02/2017.
 */

public class EntregaAdapter extends RecyclerView.Adapter<EntregaAdapter.MyViewHolder> {

    private Context context;
    private List<Distribuicao> lista;
    private LayoutInflater layoutInflater;
    private static ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public EntregaAdapter(Context context, List<Distribuicao> lista) {
        this.context = context;
        this.lista = lista;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = layoutInflater.inflate(R.layout.list_item_entrega, parent, false);
        EntregaAdapter.MyViewHolder mvh = new EntregaAdapter.MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

            holder.editCod.setText(lista.get(position).getProdutos().getCod() + "");

            holder.editDesc.setText(lista.get(position).getProdutos().getNome());

            String aux = String.format("%.3f", lista.get(position).getQtde()).replaceAll("[.]", ",");
            holder.editQtde.setText(aux);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public Distribuicao getItem(int position) {
        return lista.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView editCod, editDesc, editQtde;

        public MyViewHolder(View itemView) {
            super(itemView);

            editCod = (TextView) itemView.findViewById(R.id.editCodigo);
            editDesc = (TextView) itemView.findViewById(R.id.editDescricao);
            editQtde = (TextView) itemView.findViewById(R.id.editQtde);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null)
                itemClickListener.onItemClick(getAdapterPosition());
        }
    }
}
