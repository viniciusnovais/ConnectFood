package br.com.pdasolucoes.connectfood;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by PDA on 16/02/2017.
 */

public class CheckinActivity extends AbsRuntimePermission implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private ImageButton btIniciar, btCheck, btAgenda;
    private EditText editUsuario, editData, editRazaoSocial, editLocal, editTel;
    private int dia, mes, ano, hora, minuto;
    private GoogleApiClient googleApiClient;
    private Geocoder geocoder;
    private List<Address> listAddress;
    private TextView tvValidado, tvForaArea;
    private TextView tvColEntr, tvIniciarColEntr;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_checkin);
        setContentView(R.layout.activity_checkin);

        editUsuario = (EditText) findViewById(R.id.editUsuario);
        editData = (EditText) findViewById(R.id.editDataHora);
        editRazaoSocial = (EditText) findViewById(R.id.editRazaoSocial);
        editLocal = (EditText) findViewById(R.id.editLocal);
        editTel = (EditText) findViewById(R.id.editTelefone);
        tvValidado = (TextView) findViewById(R.id.tvValidacao);
        tvForaArea = (TextView) findViewById(R.id.tvForadaArea);
        tvColEntr = (TextView) findViewById(R.id.legendaColEntr);
        tvIniciarColEntr = (TextView) findViewById(R.id.iniciarColEntr);
        btAgenda = (ImageButton) findViewById(R.id.btColEntr);

        SharedPreferences preferences = getSharedPreferences(MainActivity.PREF, MODE_PRIVATE);
        editUsuario.setText(preferences.getString("nome", ""));

        Calendar calendar = Calendar.getInstance();
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH);
        ano = calendar.get(Calendar.YEAR);

        hora = calendar.get(Calendar.HOUR_OF_DAY);
        minuto = calendar.get(Calendar.MINUTE);

        editData.setText(String.format("%02d/%02d/%04d", dia, mes + 1, ano) + " " + String.format("%02d:%02d", hora, minuto));
        editRazaoSocial.setText(getIntent().getExtras().getString("razaoSocial"));
        editLocal.setText(getIntent().getExtras().getString("local"));
        editTel.setText(getIntent().getExtras().getString("telefone"));

        if (getIntent().getExtras().getInt("tipo") == 2) {
            btAgenda.setImageResource(R.drawable.coleta);
            tvColEntr.setText("COLETA");
            tvIniciarColEntr.setText("INICIAR");
        } else if (getIntent().getExtras().getInt("tipo") == 3) {
            btAgenda.setImageResource(R.drawable.entrega);
            tvColEntr.setText("ENTREGA");
            tvIniciarColEntr.setText("INICIAR");

        }

        btIniciar = (ImageButton) findViewById(R.id.btIniciar);
        btIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getIntent().getExtras().getInt("tipo") == 2) {
                    Intent i = new Intent(CheckinActivity.this, ListaProdutosActivity.class);
                    i.putExtra("idProgramacao", getIntent().getExtras().getInt("idProgramacao"));
                    i.putExtra("idDoa", getIntent().getExtras().getInt("idDoador"));
                    i.putExtra("dataSaida", getIntent().getExtras().getString("dataSaida"));
                    i.putExtra("email", getIntent().getExtras().getString("email"));
                    i.putExtra("nomeFilial", getIntent().getExtras().getString("razaoSocial"));

                    //Pegando a data/hora de inicio da coleta
                    SimpleDateFormat sdf = new SimpleDateFormat("HH : mm");
                    SimpleDateFormat sdfData = new SimpleDateFormat("yyyy/MM/dd");

                    i.putExtra("dataInicio", sdfData.format(new Date()));
                    i.putExtra("horaInicio", sdf.format(new Date()));

                    startActivity(i);
                    finish();
                } else if (getIntent().getExtras().getInt("tipo") == 3) {
                    Intent i = new Intent(CheckinActivity.this, EntregaActivity.class);
                    i.putExtra("idProgramacao", getIntent().getExtras().getInt("idProgramacao"));
                    i.putExtra("idEntrega", getIntent().getExtras().getInt("idDoador"));
                    i.putExtra("nomeFilial", getIntent().getExtras().getString("razaoSocial"));
                    i.putExtra("email", getIntent().getExtras().getString("email"));


                    //Pegando a data de inicio da entrega
                    SimpleDateFormat sdfData = new SimpleDateFormat("yyyy/MM/dd");
                    i.putExtra("dataInicio", sdfData.format(new Date()));

                    //Pegando a hora de inicio da entrega
                    SimpleDateFormat sdf = new SimpleDateFormat("HH : mm");
                    i.putExtra("horaInicio", sdf.format(new Date()));


                    startActivity(i);
                    finish();
                }

            }
        });


        btAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CheckinActivity.this, AgendaActivity.class);
                if (getIntent().getExtras().getInt("tipo") == 2) {
                    i.putExtra("tipo", 2);
                } else if (getIntent().getExtras().getInt("tipo") == 3) {
                    i.putExtra("tipo", 3);
                }
                startActivity(i);
                finish();
            }
        });

        btCheck = (ImageButton) findViewById(R.id.btCheck);
        btCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callConnection();
                Toast.makeText(getApplicationContext(), "checkando...", Toast.LENGTH_SHORT).show();
            }
        });

        requestAppPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CALL_PHONE
        }, R.string.msg, 10);


        editTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + editTel.getText().toString()));
                if (ActivityCompat.checkSelfPermission(CheckinActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private synchronized void callConnection() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        geocoder = new Geocoder(CheckinActivity.this, Locale.getDefault());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        try {
            listAddress = geocoder.getFromLocationName(getIntent().getExtras().getString("local"), 1);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Fora do tempo limite de espera", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        Location l = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);


        Location lo = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        float dist;
        try {

            lo.setLatitude(listAddress.get(0).getLatitude());
            lo.setLongitude(listAddress.get(0).getLongitude());

            dist = l.distanceTo(lo);

            if (dist < 500) {
                tvValidado.setVisibility(View.VISIBLE);
                tvForaArea.setVisibility(View.GONE);
            } else {
                tvValidado.setVisibility(View.GONE);
                tvForaArea.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Distância para seu ponto: " + dist + "m", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            statusCheck();
            tvForaArea.setVisibility(View.VISIBLE);
            tvValidado.setVisibility(View.GONE);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.w("LOG", i + "");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w("LOG", connectionResult + "");
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Seu GPS parece desabilitado, você deseja habilita-lo?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}