package br.com.pdasolucoes.connectfood.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.ConnectException;
import java.util.List;

import br.com.pdasolucoes.connectfood.R;
import br.com.pdasolucoes.connectfood.model.Produtos;
import br.com.pdasolucoes.connectfood.model.Programacao;

/**
 * Created by PDA on 16/02/2017.
 */

public class RomaneioAdapter extends BaseAdapter {
    private Context context;
    private List<Produtos> lista;

    public RomaneioAdapter(Context context, List<Produtos> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.list_item_romaneio,null);

        TextView tvCod = (TextView) v.findViewById(R.id.editCod);
        tvCod.setText(lista.get(position).getCod()+"");

        TextView tvDescricao = (TextView) v.findViewById(R.id.editDescricao);
        tvDescricao.setText(lista.get(position).getNome());

        TextView tvUnidMedida = (TextView) v.findViewById(R.id.unidMedida);
        if (lista.get(position).getUnidMedida()==1)tvUnidMedida.setText("Kg");
        else if (lista.get(position).getUnidMedida()==2)tvUnidMedida.setText("Emb.");

        TextView tvQtde = (TextView) v.findViewById(R.id.editQtde);

        String aux = String.format("%.3f",lista.get(position).getQtde()).replaceAll("[.]",",");
        tvQtde.setText(aux);

        return v;
    }
}
