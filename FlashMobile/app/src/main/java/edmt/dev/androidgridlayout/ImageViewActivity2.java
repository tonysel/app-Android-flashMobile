package edmt.dev.androidgridlayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by tonysellitto on 02/01/18.
 */

public class ImageViewActivity2 extends AppCompatActivity {
    private static final int THUMB_HEIGHT = 1450;
    private static final int THUMB_WIDTH = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        // ImageView imageView = (ImageView) findViewById(R.id.imageViewDetail) ;

        ImageButton imageView = (ImageButton) findViewById(R.id.imageButtonDetail);
        final Intent i = getIntent();
        final String nameP = i.getStringExtra("nameP");
        final String dateP = i.getStringExtra("dateP");
        final String dateEnd = i.getStringExtra("dateEnd");
        String nameImage = i.getStringExtra("nameImage");

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "He/She");

        String thumbPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/PTAlbum/" + nameP + dateP + "/thumb/"  + nameImage;


        Bitmap bitmap = BitmapFactory.decodeFile(thumbPath);
        //bitmap = Bitmap.createScaledBitmap(bitmap, THUMB_HEIGHT, THUMB_WIDTH, false);
        bitmap = Bitmap.createBitmap(bitmap);


        imageView.setImageBitmap(bitmap);



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(ImageViewActivity2.this,ActivityTwo.class);

                intent.putExtra("name", nameP);
                intent.putExtra("date", dateP);
                intent.putExtra("dateEnd", dateEnd);


                startActivity(intent);
            }
        });

        Snackbar.make(findViewById(android.R.id.content),  username + " is seeing: " + nameImage, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }).setActionTextColor(Color.RED).show();


    }


}