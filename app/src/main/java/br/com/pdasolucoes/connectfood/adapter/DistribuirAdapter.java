package br.com.pdasolucoes.connectfood.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import br.com.pdasolucoes.connectfood.DistribuirActivity;
import br.com.pdasolucoes.connectfood.R;
import br.com.pdasolucoes.connectfood.dao.AgendaDao;
import br.com.pdasolucoes.connectfood.model.Produtos;
import br.com.pdasolucoes.connectfood.model.Programacao;

/**
 * Created by PDA on 20/02/2017.
 */

public class DistribuirAdapter extends RecyclerView.Adapter<DistribuirAdapter.MyViewHolder> {
    private Context context;
    private List<Produtos> lista;
    private LayoutInflater layoutInflater;

    private static ItemClickListener itemClickListener;

    public interface ItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener ){
        this.itemClickListener=itemClickListener;
    }

    public DistribuirAdapter(Context context, List<Produtos> lista) {
        this.context = context;
        this.lista = lista;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.list_item_distribuir,parent,false);
        MyViewHolder mvh= new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (lista.get(position).getDistribuido()==1){
//            holder.tvCod.setBackgroundResource(R.drawable.border_lista_romaneio_green);
            holder.tvDes.setBackgroundResource(R.drawable.border_lista_romaneio_green);
            holder.tvQtde.setBackgroundResource(R.drawable.border_lista_romaneio_green);
            holder.tvUnidMedida.setBackgroundResource(R.drawable.border_lista_romaneio_green);
        }

//        holder.tvCod.setText(lista.get(position).getCod()+"");

        holder.tvDes.setText(lista.get(position).getNome());

        if (lista.get(position).getUnidMedida()==1)holder.tvUnidMedida.setText("Kg");
        else if (lista.get(position).getUnidMedida()==2)holder.tvUnidMedida.setText("Emb.");

        String aux = String.format("%.3f",lista.get(position).getQtdeAbsoluta()).replaceAll("[.]",",");
        holder.tvQtde.setText(aux);

    }

    public Produtos getItem(int position){
        return lista.get(position);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvCod,tvDes,tvQtde,tvUnidMedida;

        public MyViewHolder(View itemView) {
            super(itemView);
//            tvCod = (TextView) itemView.findViewById(R.id.editCod);
            tvDes = (TextView) itemView.findViewById(R.id.editDescricao);
            tvQtde =(TextView) itemView.findViewById(R.id.editQtde);
            tvUnidMedida = (TextView) itemView.findViewById(R.id.unidMedida);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener!=null){
                itemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
