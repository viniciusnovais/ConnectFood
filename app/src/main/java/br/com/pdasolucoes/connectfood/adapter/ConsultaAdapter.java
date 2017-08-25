package br.com.pdasolucoes.connectfood.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import br.com.pdasolucoes.connectfood.R;
import br.com.pdasolucoes.connectfood.dao.DistribuicaoDao;
import br.com.pdasolucoes.connectfood.dao.ProdutosDao;
import br.com.pdasolucoes.connectfood.model.Consulta;
import br.com.pdasolucoes.connectfood.model.Distribuicao;
import br.com.pdasolucoes.connectfood.model.Produtos;

/**
 * Created by PDA on 20/02/2017.
 */

public class ConsultaAdapter extends BaseAdapter {
    private Context context;
    private List<Consulta> lista;
    private RelativeLayout relativeLayout;
    private ImageView imageOk;
    private ProdutosDao daoProduto;
    private DistribuicaoDao daoDistribuicao;

    public ConsultaAdapter(Context context, List<Consulta> lista) {
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
        View v = View.inflate(context, R.layout.list_item_consulta, null);
        daoProduto = new ProdutosDao(context);
        daoDistribuicao = new DistribuicaoDao(context);

        relativeLayout = (RelativeLayout) v.findViewById(R.id.rlListaAgenda);
        imageOk = (ImageView) v.findViewById(R.id.imageOk);

        if (lista.get(position).getIdProgramacao().getTipo() == 3) {
            relativeLayout.setBackgroundResource(R.drawable.border_green);
        }

        for (Produtos p : daoProduto.listarDoacao(lista.get(position).getIdProgramacao().getIdFilial(),lista.get(position).getIdProgramacao().getId())){
            if (p.getFinalizado()==1){
                imageOk.setVisibility(View.VISIBLE);
            }
        }

        for (Distribuicao d : daoDistribuicao.listarDistribuicaoFilial(lista.get(position).getIdProgramacao().getIdFilial())){
            if (d.getFinalizado()==1){
                imageOk.setVisibility(View.VISIBLE);
                TextView tvDescricao = (TextView) v.findViewById(R.id.tvDescricao);
                tvDescricao.setText("Entrega");
                tvDescricao.setLines(1);
            }
        }

        TextView tvLocation= (TextView) v.findViewById(R.id.tvLocation);
        tvLocation.setText(lista.get(position).getIdProgramacao().getRazaoSocial());

        TextView tvDia = (TextView) v.findViewById(R.id.tvDia);
        String dia = lista.get(position).getIdProgramacao().getDataSaida();
        dia=dia.substring(8,10);
        tvDia.setText(dia);

        TextView tvHora = (TextView) v.findViewById(R.id.tvHora);
        String hora = lista.get(position).getIdProgramacao().getHoraSaida();
        tvHora.setText(hora+"h");


        return v;
    }
}
