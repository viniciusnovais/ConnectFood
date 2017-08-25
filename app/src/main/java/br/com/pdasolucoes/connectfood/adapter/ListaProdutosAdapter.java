package br.com.pdasolucoes.connectfood.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.StringCharacterIterator;
import java.util.List;

import br.com.pdasolucoes.connectfood.R;
import br.com.pdasolucoes.connectfood.model.Produtos;

/**
 * Created by PDA on 20/02/2017.
 */

public class ListaProdutosAdapter extends RecyclerView.Adapter<ListaProdutosAdapter.MyViewHolder> {
    private Context context;
    private List<Produtos> lista;
    private LayoutInflater layoutInflater;
    private static ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ListaProdutosAdapter(Context context, List<Produtos> lista) {
        this.context = context;
        this.lista = lista;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.list_item_produtos, parent, false);
        ListaProdutosAdapter.MyViewHolder mvh = new ListaProdutosAdapter.MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tvCod.setText(lista.get(position).getCod() + "");

        holder.tvDes.setText(lista.get(position).getNome());

        if (lista.get(position).getUnidMedida()==1)holder.tvUnidMedida.setText("Kg");
        else if (lista.get(position).getUnidMedida()==2)holder.tvUnidMedida.setText("Un");

        String aux = String.format("%.3f",lista.get(position).getQtde()).replaceAll("[.]",",");
        holder.tvQtde.setText(aux);
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public Produtos getItem(int position) {
        return lista.get(position);
    }

    public void insert(Produtos p) {
        lista.add(p);
        notifyDataSetChanged();
    }

    public void remove(Produtos p) {
        lista.remove(p);
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvCod, tvDes, tvQtde, tvUnidMedida;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCod = (TextView) itemView.findViewById(R.id.editCod);
            tvDes = (TextView) itemView.findViewById(R.id.editDescricao);
            tvQtde = (TextView) itemView.findViewById(R.id.editQtde);
            tvUnidMedida = (TextView) itemView.findViewById(R.id.unidMedida);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
