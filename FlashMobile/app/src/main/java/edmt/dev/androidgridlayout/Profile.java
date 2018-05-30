package edmt.dev.androidgridlayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by tonysellitto on 02/01/18.
 */

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewDetail) ;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile");
        Button logOut = (Button) findViewById(R.id.buttonLogOut);
        TextView hello = (TextView) findViewById(R.id.helloText);
        Boolean authenticated = sharedPreferences.getBoolean("authenticated", false);
        String username = sharedPreferences.getString("username", "");

        if (authenticated == true){
            imageView.setImageResource(R.drawable.sfera);

            hello.setText("Hi, " + username);
        }


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("authenticated", false);
                editor.putString("username", "");

                editor.commit();

                startActivity(new Intent(Profile.this, MainActivity.class));
            }
        });
    }
}