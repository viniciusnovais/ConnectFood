package br.com.pdasolucoes.connectfood;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.pdasolucoes.connectfood.dao.AgendaDao;
import br.com.pdasolucoes.connectfood.dao.ConsultaDao;
import br.com.pdasolucoes.connectfood.dao.DistribuicaoDao;
import br.com.pdasolucoes.connectfood.dao.ProdutosDao;
import br.com.pdasolucoes.connectfood.model.Produtos;

/**
 * Created by PDA on 15/02/2017.
 */

public class HomeActivity extends AppCompatActivity {

    private ImageButton btAgenda, btConsulta, btColetar, btSync, btEntrega, btDistribuir;
    private ConsultaDao daoConsulta;
    private AgendaDao agendaDao;
    private ProdutosDao produtosDao;
    private DistribuicaoDao distribuicaoDao;
    private TextView tvContadorConsulta, tvContadorColeta, tvContadorEntrega, tvContadorSync, tvContadorDistribuir, tvContadorAgenda;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_home);
        setContentView(R.layout.activity_home);
        daoConsulta = new ConsultaDao(this);
        agendaDao = new AgendaDao(this);
        produtosDao = new ProdutosDao(this);
        distribuicaoDao = new DistribuicaoDao(this);


        try {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

            Date hoje = formato.parse(formato.format(new Date()));
            Date data = formato.parse(agendaDao.maiorData());

            if (hoje.after(data)) {
                agendaDao.delete();
                produtosDao.delete();
                produtosDao.deleteAux();
                daoConsulta.delete();
                distribuicaoDao.delete();

            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }


        btAgenda = (ImageButton) findViewById(R.id.btAgenda);
        btAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, AgendaActivity.class);
                i.putExtra("tipo", -1);
                startActivity(i);
            }
        });

        btConsulta = (ImageButton) findViewById(R.id.btConsulta);
        btConsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ConsultaActivity.class);
                startActivity(i);
            }
        });

        btColetar = (ImageButton) findViewById(R.id.btColetar);
        btColetar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, AgendaActivity.class);
                i.putExtra("tipo", 2);
                startActivity(i);

            }
        });

        btSync = (ImageButton) findViewById(R.id.btSync);
        btSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, SyncActivity.class);
                startActivity(i);
                finish();
            }
        });

        btEntrega = (ImageButton) findViewById(R.id.btEntrega);
        btEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, AgendaActivity.class);
                i.putExtra("tipo", 3);
                startActivity(i);
            }
        });

        btDistribuir = (ImageButton) findViewById(R.id.btDistribuir);
        btDistribuir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, DistribuirActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.PREF, MODE_PRIVATE);
        TextView tv = ((TextView) findViewById(R.id.tvWelcome));
        tv.setText("Bem-Vindo, " + sharedpreferences.getString("nome", ""));

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.PREF, MODE_PRIVATE);
        tvContadorConsulta = (TextView) findViewById(R.id.tvContadorConsulta);

        try {
            if (daoConsulta.ContarConsulta(sharedpreferences.getInt("id", 0)) > 0) {
                tvContadorConsulta.setVisibility(View.VISIBLE);
                tvContadorConsulta.setText(daoConsulta.ContarConsulta(sharedpreferences.getInt("id", 0)) + "");
            }
            tvContadorColeta = (TextView) findViewById(R.id.tvContadorColeta);
            if (agendaDao.ContadorColeta(sharedpreferences.getInt("id", 0)) > 0) {
                tvContadorColeta.setVisibility(View.VISIBLE);
                tvContadorColeta.setText(agendaDao.ContadorColeta(sharedpreferences.getInt("id", 0)) + "");
            }
            tvContadorEntrega = (TextView) findViewById(R.id.tvContadorEntrega);
            if (agendaDao.ContadorEntrega(sharedpreferences.getInt("id", 0)) > 0) {
                tvContadorEntrega.setVisibility(View.VISIBLE);
                tvContadorEntrega.setText(agendaDao.ContadorEntrega(sharedpreferences.getInt("id", 0)) + "");
            }

            tvContadorAgenda = (TextView) findViewById(R.id.tvContadorAgenda);
            if (agendaDao.ContadorAgenda(sharedpreferences.getInt("id", 0)) > 0) {
                tvContadorAgenda.setVisibility(View.VISIBLE);
                tvContadorAgenda.setText(agendaDao.ContadorAgenda(sharedpreferences.getInt("id", 0)) + "");
            }
            tvContadorSync = (TextView) findViewById(R.id.tvContadorSync);
            if (agendaDao.listarProgSync(sharedpreferences.getInt("id", 0)).size() > 0) {
                tvContadorSync.setVisibility(View.VISIBLE);
                tvContadorSync.setText(agendaDao.listarProgSync(sharedpreferences.getInt("id", 0)).size() + "");
            }
            tvContadorDistribuir = (TextView) findViewById(R.id.tvContadorDistribuir);
            if (produtosDao.ContadorProdutosDistribuir() > 0) {
                tvContadorDistribuir.setVisibility(View.VISIBLE);
                tvContadorDistribuir.setText(produtosDao.ContadorProdutosDistribuir() + "");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btSair:

                //Deslogar
                SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear().commit();

                Intent i = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(i);
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
//                    SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.PREF_NAME,MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedpreferences.edit();
//                    editor.clear().commit();
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };
}
