package br.com.pdasolucoes.connectfood.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.pdasolucoes.connectfood.R;
import br.com.pdasolucoes.connectfood.model.Produtos;
import br.com.pdasolucoes.connectfood.model.Programacao;

/**
 * Created by PDA on 20/02/2017.
 */

public class DistribuirFilialAdapter extends RecyclerView.Adapter<DistribuirFilialAdapter.MyViewHolder> {
    private Context context;
    private List<Programacao> lista;
    private LayoutInflater layoutInflater;

    private static ItemClickListener itemClickListener;

    public interface ItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener ){
        this.itemClickListener=itemClickListener;
    }

    public DistribuirFilialAdapter(Context context, List<Programacao> lista) {
        this.context = context;
        this.lista = lista;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.list_item_distribuir_instituicao,parent,false);
        MyViewHolder mvh= new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tvInstituicao.setText(lista.get(position).getRazaoSocial());

    }

    public Programacao getItem(int position){
        return lista.get(position);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvInstituicao;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvInstituicao = (TextView) itemView.findViewById(R.id.tvInstituicao);
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
