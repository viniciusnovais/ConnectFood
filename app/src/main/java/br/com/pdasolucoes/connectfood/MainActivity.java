package br.com.pdasolucoes.connectfood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import br.com.pdasolucoes.connectfood.util.ServiceLogin;
import br.com.pdasolucoes.connectfood.model.Usuario;
import br.com.pdasolucoes.connectfood.util.VerificaConexao;

public class MainActivity extends AppCompatActivity {

    private Button btLogin;
    private Usuario usuario;
    public final static String PREF = "LoginPreference";
    private EditText editUsuario, editSenha;
    private String textUsuario = "", textSenha = "";
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_login);
        setContentView(R.layout.activity_main);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.progress_bar, null);
        builder.setView(v);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        editUsuario = (EditText) findViewById(R.id.editEmail);
        editSenha = (EditText) findViewById(R.id.editSenha);

        TextView link = (TextView) findViewById(R.id.linkSite);
        String linkText = "<a href='http://pdasolucoes.com.br'>pdasolucoes.com.br</a>";
        link.setText(Html.fromHtml(linkText));
        link.setMovementMethod(LinkMovementMethod.getInstance());

        SharedPreferences preferences = getSharedPreferences(PREF, MODE_PRIVATE);

        if (preferences != null) {
            textUsuario = preferences.getString("usuario", "");
            textSenha = preferences.getString("senha", "");
            if (textUsuario != "" && textSenha != "") {
                AsyncLogin task = new AsyncLogin();
                task.execute();
            }
        }

        btLogin = (Button) findViewById(R.id.btnLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textUsuario = editUsuario.getEditableText().toString();
                textSenha = editSenha.getEditableText().toString();

                if (textUsuario.length() != 0 && textUsuario.toString() != "") {
                    if (textSenha.length() != 0 & textSenha.toString() != "") {

                        editUsuario.setText("");
                        editSenha.setText("");

                        AsyncLogin task = new AsyncLogin();
                        task.execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Por Favor, coloque usuario e a senha", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Por Favor, coloque usuario e a senha", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class AsyncLogin extends AsyncTask {

        SharedPreferences preferences = getSharedPreferences(PREF, MODE_PRIVATE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            usuario = ServiceLogin.invokeLoginWS(textUsuario, textSenha);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            if (VerificaConexao.isNetworkConnected(MainActivity.this)) {
                if (usuario.getTipoInstituicao() == 1
                        && usuario.getUsuario().equals(textUsuario)
                        && usuario.getSenha().equals(textSenha)) {

                    //Navigate to Home Screen

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("id", usuario.getId());
                    editor.putString("usuario", textUsuario);
                    editor.putString("senha", textSenha);
                    editor.putString("nome", usuario.getNomeUsuario());
                    editor.commit();

                    startActivity(i);
                    finish();
                } else {
                    //Set Error message
                    Toast.makeText(getApplicationContext(), "Falha no Login", Toast.LENGTH_SHORT).show();

                }
            } else if (!preferences.getString("usuario", "").equals("")
                    && !preferences.getString("senha", "").equals("")) {
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Falha no Login", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
