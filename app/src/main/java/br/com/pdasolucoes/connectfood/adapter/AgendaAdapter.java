package br.com.pdasolucoes.connectfood.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.zip.Inflater;

import br.com.pdasolucoes.connectfood.R;
import br.com.pdasolucoes.connectfood.model.Programacao;

/**
 * Created by PDA on 15/02/2017.
 */

public class AgendaAdapter extends BaseAdapter {
    private List<Programacao> lista;
    private Context context;
    private RelativeLayout relativeLayout;

    public AgendaAdapter(Context context, List<Programacao> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Programacao getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.list_item_agenda, null);

        relativeLayout = (RelativeLayout) v.findViewById(R.id.rlListaAgenda);
        if (lista.get(position).getTipo() == 3) {
            if (lista.get(position).getEfetuada() == 1) {
                relativeLayout.setBackgroundResource(R.drawable.border_orange);
            } else {
                relativeLayout.setBackgroundResource(R.drawable.border_green);
            }
            TextView tvTipo = (TextView) v.findViewById(R.id.tvTipo);
            tvTipo.setText("Entrega");
            tvTipo.setLines(1);
        }

        TextView tvLocation = (TextView) v.findViewById(R.id.tvLocation);
        tvLocation.setText(lista.get(position).getRazaoSocial());

        //Dia
        TextView tvDia = (TextView) v.findViewById(R.id.tvDia);
        String dia = lista.get(position).getDataSaida();
        String mesString = dia.substring(5, 7);
        dia = dia.substring(8, 10);

        tvDia.setText(dia);

        //Mes

        int mes = Integer.parseInt(mesString);

        switch (mes) {
            case 01:
                mesString = "Jan";
                break;
            case 02:
                mesString = "Fev";
                break;
            case 03:
                mesString = "Mar";
                break;
            case 04:
                mesString = "Abr";
                break;
            case 05:
                mesString = "Mai";
                break;
            case 06:
                mesString = "Jun";
                break;
            case 07:
                mesString = "Jul";
                break;
            case 8:
                mesString = "Ago";
                break;
            case 9:
                mesString = "Set";
                break;
            case 10:
                mesString = "Out";
                break;
            case 11:
                mesString = "Nov";
                break;
            case 12:
                mesString = "Dez";
                break;
            default:
                mesString = "Mês Inválido";
        }

        TextView tvMes = (TextView) v.findViewById(R.id.tvMes);
        tvMes.setText(mesString);


        TextView tvHora = (TextView) v.findViewById(R.id.tvHora);
        String hora = lista.get(position).getHoraSaida();
        tvHora.setText(hora + "h");


        return v;
    }
}
