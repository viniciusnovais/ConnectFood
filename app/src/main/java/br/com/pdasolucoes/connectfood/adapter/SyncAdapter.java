package br.com.pdasolucoes.connectfood.adapter;

import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import br.com.pdasolucoes.connectfood.R;
import br.com.pdasolucoes.connectfood.SyncActivity;
import br.com.pdasolucoes.connectfood.model.Programacao;

/**
 * Created by PDA on 16/02/2017.
 */

public class SyncAdapter extends BaseAdapter {

    private Context context;
    private List<Programacao> lista;
    private int[] vetorPosition;
    private RelativeLayout relativeLayout;

    private static ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClick(int[] vetorInt);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SyncAdapter(Context context, List<Programacao> lista) {
        this.context = context;
        this.lista = lista;
        vetorPosition = new int[getCount()];
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.list_item_sync, null);

        CheckBox check = (CheckBox) v.findViewById(R.id.checkBox);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageInfo);
        relativeLayout = (RelativeLayout) v.findViewById(R.id.relative1);

        if (lista.get(position).getSync()==1){
            imageView.setImageResource(R.drawable.check_yes);
        }


        if (SyncActivity.POSITION == 1) {
            check.setVisibility(View.VISIBLE);

        } else if (SyncActivity.POSITION == 2) {
            check.setVisibility(View.VISIBLE);
            check.setChecked(true);
            for (int i = 0; i < vetorPosition.length; i++) {
                vetorPosition[i] = 1;
            }
            itemClickListener.onItemClick(vetorPosition);
        }

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    vetorPosition[position] = 1;
                } else {
                    vetorPosition[position] = 0;
                }
                itemClickListener.onItemClick(vetorPosition);
            }
        });


        TextView tvLocation = (TextView) v.findViewById(R.id.tvLocation);
        tvLocation.setText(lista.get(position).getRazaoSocial());

        TextView tvDescricao = (TextView) v.findViewById(R.id.tvDescricao);
        if (lista.get(position).getTipo() == 2) {
            tvDescricao.setText("Coleta");
        } else {
            relativeLayout.setBackgroundResource(R.drawable.border_green);
            tvDescricao.setText("Entrega");
        }
        tvDescricao.setLines(1);

        TextView tvHour = (TextView) v.findViewById(R.id.tvHour);
        tvHour.setText(lista.get(position).getHoraChegada()+" h");

        String dia = lista.get(position).getDataChegada();
        String mesString = dia.substring(5,7);
        dia = dia.substring(8,10);

        int mes = Integer.parseInt(mesString);

        switch (mes){
            case 01:
                mesString = "Jan";
                break;
            case 02:
                mesString ="Fev";
                break;
            case 03:
                mesString="Mar";
                break;
            case 04:
                mesString="Abr";
                break;
            case 05:
                mesString="Mai";
                break;
            case 06:
                mesString="Jun";
                break;
            case 07:
                mesString="Jul";
                break;
            case 8:
                mesString="Ago";
                break;
            case 9:
                mesString="Set";
                break;
            case 10:
                mesString="Out";
                break;
            case 11:
                mesString="Nov";
                break;
            case 12:
                mesString="Dez";
                break;
            default:
                mesString="Mês Inválido";
        }

        TextView tvMes = (TextView) v.findViewById(R.id.tvMes);
        tvMes.setText(mesString);

        TextView tvDia = (TextView) v.findViewById(R.id.tvDia);
        tvDia.setText(dia);

        return v;
    }
}
