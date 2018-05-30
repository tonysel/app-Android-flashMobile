package edmt.dev.androidgridlayout;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "SELLITTO_SECURITY";
    
    private String baseURI = "http://10.0.2.2:8182/groupsRegApplication/";

    private EditText username;
    private EditText password;
    private Gson gson;

    public class AccediResourceTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Gson gson = new Gson();
            ClientResource cr;
            cr = new ClientResource(baseURI + params[0]);
            String response = null;
            User user = new User(params[1], params[2]);

            // Add the client authentication to the call
            ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
            ChallengeResponse authentication = new ChallengeResponse(scheme, params[1], params[2]);
            cr.setChallengeResponse(authentication);

            try {
                response = cr.put(gson.toJson(user)).getText();
            } catch (ResourceException | IOException e) {
                if (org.restlet.data.Status.CLIENT_ERROR_UNAUTHORIZED.equals(cr.getStatus())) {
                    // Unauthorized access
                    Log.e(TAG, "Access unauthorized by the server, check your credentials");
                } else {
                    String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " + cr.getStatus().getReasonPhrase();
                    Log.e(TAG, text);
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            Gson gson = new Gson();
            String result = gson.fromJson(res, String.class);
            if (result != null) {
                if (result.equalsIgnoreCase("OK")) {
                    Toast.makeText(getApplicationContext(), "Done Access", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean("authenticated", true);
                    editor.putString("username", username.getText().toString());
                    editor.putString("password", password.getText().toString());

                    editor.commit();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Not Valid User", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("authenticated", false);
                    editor.putString("username", "");
                    editor.putString("password", "");

                    editor.commit();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Not Valid User", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("authenticated", false);
                editor.putString("username", "");
                editor.putString("password", "");

                editor.commit();

            }
        }
    }

    public class RegistratiResourceTask extends AsyncTask<String, Void, String> {
        private String response;

        @Override
        protected String doInBackground(String... params) {

            ClientResource cr;
            cr = new ClientResource(baseURI + params[0]);
            String response = null;
            User user = new User(params[1], params[2]);
            if (params[1].isEmpty() || params[2].isEmpty()) {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Insert all data", Toast.LENGTH_SHORT).show();
                    }

                });
            } else {
                try {
                    Log.e(TAG, "Entro");
                    response = cr.post(gson.toJson(user, User.class)).getText();
                } catch (IOException e) {
                    String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " + cr.getStatus().getReasonPhrase();
                    Log.e(TAG, text);
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            if (res != null)
                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Login");

        username =  findViewById(R.id.usernameText);
        password =  findViewById(R.id.passwordText);
        gson = new Gson();
    }

    public void registration(View v) {
        new RegistratiResourceTask().execute("users", username.getText().toString(), password.getText().toString());
    }

    public void access (View v){
        new AccediResourceTask().execute("users",username.getText().toString(), password.getText().toString());
    }

}