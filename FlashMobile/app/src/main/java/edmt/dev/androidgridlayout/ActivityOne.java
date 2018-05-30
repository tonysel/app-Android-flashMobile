package edmt.dev.androidgridlayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.os.Bundle;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;


import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityOne extends AppCompatActivity implements OnTaskCompleted {

    private static final String TAG = null;
    //private String baseURI = "http://10.0.2.2:8182/picture/";
    private String baseURI = "http://10.0.2.2:8182/groupsRegApplication/groups/";
    private String baseURI2 = "http://10.0.2.2:8182/groupsRegApplication/subscribers/";


    GridView gridView;
    private static final int THUMB_SIZE = 1500;
    File ptAlbumPath;
    File ptFilePath;
    File thumbPath;
    String[] imgInDir;
    ProgressDialog progressDialog;
    String s;
    String nameP = null;
    String dateP = null;
    String dateEnd =  null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        Intent i= getIntent();


        nameP = i.getStringExtra("name");
        dateP = i.getStringExtra("date");
        dateEnd = i.getStringExtra("dateEnd");
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/PTAlbum/" + nameP + dateP).mkdirs();

        ptAlbumPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/PTAlbum/" + nameP + dateP);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Group");

        gridView = (GridView) findViewById(R.id.gridImageTwo);

        progressDialog = new ProgressDialog(ActivityOne.this,R.style.full_screen_dialog);
        progressDialog.setCancelable(false);
        progressDialog.show();


        FloatingActionButton btn_insert = (FloatingActionButton) findViewById(R.id.scatta);

        Button subscribeButton = (Button) findViewById(R.id.subscribeButton);


        subscribeButton.setOnClickListener(new View.OnClickListener() {
                                               public void onClick(View v) {

                                                   DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");


                                                   Date start = null;

                                                   Date end = null;

                                                   Date now = new Date();
                                                   //date = formatter.parse(dateInString);
                                                   try {
                                                       start = formatter.parse(dateP);
                                                   } catch (ParseException e) {
                                                       e.printStackTrace();
                                                   }

                                                   try {
                                                       end = formatter.parse(dateEnd);
                                                   } catch (ParseException e) {
                                                       e.printStackTrace();
                                                   }

                                                   if(now.compareTo(start) < 0) sendSubscribers();

                                                   else{
                                                       AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityOne.this);
                                                       builder1.setMessage("This function is not available with a past or current group");
                                                       builder1.setCancelable(true);

                                                       builder1.setPositiveButton(
                                                               "Ok",
                                                               new DialogInterface.OnClickListener() {
                                                                   public void onClick(DialogInterface dialog, int id) {
                                                                       dialog.cancel();
                                                                   }
                                                               });

                                                       AlertDialog alert11 = builder1.create();
                                                       alert11.show();
                                                   }
                                               }
                                           }
        );

        btn_insert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "");
                Boolean authenticated = sharedPreferences.getBoolean("autheticated",false);

                if (username == "") {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityOne.this);
                    builder1.setMessage("You have to authenticate to the platform for continue");
                    builder1.setCancelable(true);

                    builder1.setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    builder1.setPositiveButton(
                            "Continue",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(ActivityOne.this, LoginActivity.class);
                                    //intent.putExtra("info","Registration or authentication "+finalI);
                                    startActivity(intent);
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                else{

                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");


                    Date start = null;

                    Date end = null;

                    Date now = new Date();
                    //date = formatter.parse(dateInString);
                    try {
                        start = formatter.parse(dateP);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        end = formatter.parse(dateEnd);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    assert start != null;
                    if ((start.compareTo(now) < 0) && (now.compareTo(end) < 0)){
                        //takePicture();
                        controlTakePicture();
                    }


                }
              /*  SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);

                Boolean authenticated = sharedPreferences.getBoolean("autheticated",false);


                if (authenticated == false) {

                }
                else{takePicture();}*/

            }
        });

        FloatingActionButton btn_take = (FloatingActionButton) findViewById(R.id.insertImage);

        btn_take.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "");
                Boolean authenticated = sharedPreferences.getBoolean("autheticated",false);

                if (username == "") {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityOne.this);
                    builder1.setMessage("You have to authenticate to the platform for continue");
                    builder1.setCancelable(true);

                    builder1.setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    builder1.setPositiveButton(
                            "Continue",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(ActivityOne.this, LoginActivity.class);
                                    //intent.putExtra("info","Registration or authentication "+finalI);
                                    startActivity(intent);
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                else{

                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");


                    Date start = null;

                    Date end = null;

                    Date now = new Date();
                    //date = formatter.parse(dateInString);
                    try {
                        start = formatter.parse(dateP);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        end = formatter.parse(dateEnd);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }



                    if ((start.compareTo(now)) < 0 && (now.compareTo(end) < 0)){
                        //insertImage();
                        controlInsertImage();
                    }
                    else{ AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityOne.this);
                        builder1.setMessage("It's not time for upload image");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();}


                }

            }
        });



        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is granted");
        }

        receiveNewPhotos();
        //receiveSubscribers();




        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id)
            {

                Toast.makeText(getApplicationContext(),
                        "Selected "+imgInDir[pos], Toast.LENGTH_LONG).show();

                Intent i = new Intent(ActivityOne.this, ImageViewActivity.class);

                i.putExtra("nameImage", imgInDir[pos]);
                i.putExtra("nameP", nameP);
                i.putExtra("dateP", dateP);
                i.putExtra("dateEnd", dateEnd);

                startActivity(i);


            }
        });
    }



    public boolean directoryExist() {

        //ptAlbumPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/PTAlbum");

        if (ptAlbumPath.exists())
            return true;
        else
            return false;
    }

    public boolean fileExist(String nameFile) {

        ptFilePath = new File(ptAlbumPath.toString() + "/thumb/" + nameFile);

        if (ptFilePath.exists())
            return true;
        else
            return false;
    }

    /**
     * @param view
     */
    public void takePicture() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.setType("image/*");
        startActivityForResult(intent, 200);
    }

    /**
     * @param view
     */
    public void insertImage() {
        /*
         * Creo l'intent che mi permette di caricare
         * una immagine da un'altra app che gestisce immagini
         * (galleria, dropbox, gestori di file, ...)
         */
        Intent image = new Intent();
        image.addCategory(Intent.CATEGORY_OPENABLE);
        image.setAction(Intent.ACTION_GET_CONTENT);
        image.setType("image/*");
        startActivityForResult(image, 100);


    }


    private String buildFileName() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        String username = sharedPreferences.getString("username", "");

        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "IMG_" + timeStamp + "_" + username;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        /*
         * Questa funzione mi permette di ricevere il risultato dell'intent, quindi,
         * in questo caso, ricevere l'immagine selezionata dall'altra app che
         * gestisce immagini
         */
        if (((requestCode == 100) && (resultCode == Activity.RESULT_OK))) {
            try {
                InputStream iStream = getContentResolver().openInputStream(data.getData());

                /*
                 * creo la bitmap che conterrà l'immagine
                 */

                Bitmap bitmap = BitmapFactory.decodeStream(iStream);

                /*
                 * Non importa se non è la prima volta che carico un'immagine.
                 * Chiamo comunque la funzione mkdirs() per creare la cartella
                 * PTAlbum e la sotto cartella thumb. Infatti questa funzione
                 * controlla se la cartella che si vuole creare esiste, se esiste,
                 * non la ricrea
                 */

                /*
                 * Creo la cartella PTAlbum sapendo che nella variabile
                 * pubblica ptAlbumPath c'è il percorso della cartella
                 * dato che la funzione directoryExist viene chiamata ogni volta
                 * che si avvia la main activity
                 */
                ptAlbumPath.mkdirs();

                /*
                 * Creo la cartella thumb ricordando che la funzione mkdirs()
                 * non crea la cartella se è già esistente
                 */

                new File(ptAlbumPath.toString() + "/thumb").mkdirs();

                /*
                 * Creo la thumb dell'immagine
                 */
                Bitmap thumbBitmap = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, false);

                /*
                 * Creo la thumb in formato PNG chiamandola nella stessa maniera della
                 * bitmap originale perchè, quando clicco sulla thumb nella MainActivity,
                 * si aprirà un'altra activity nella quale richiamerò l'immagine
                 * originale attraverso il nome della thumb.
                 */

                s = buildFileName();
                FileOutputStream oStream = new FileOutputStream(ptAlbumPath.toString() + "/thumb/" + buildFileName() + ".PNG");
                thumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
                oStream.flush();
                oStream.close();

                sendPhoto(s +".PNG");;

                /*
                 * Aggiorno l'interfaccia richiamando la MainActivity
                 */

                Intent i = new Intent(ActivityOne.this, ActivityOne.class);

                i.putExtra("name", nameP);
                i.putExtra("date", dateP);
                i.putExtra("dateEnd" , dateEnd);
                startActivity(i);



        } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            try {


                /*
                 * creo la bitmap che conterrà l'immagine
                 */

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                /*
                 * Non importa se non è la prima volta che carico un'immagine.
                 * Chiamo comunque la funzione mkdirs() per creare la cartella
                 * PTAlbum e la sotto cartella thumb. Infatti questa funzione
                 * controlla se la cartella che si vuole creare esiste, se esiste,
                 * non la ricrea
                 */

                /*
                 * Creo la cartella PTAlbum sapendo che nella variabile
                 * pubblica ptAlbumPath c'è il percorso della cartella
                 * dato che la funzione directoryExist viene chiamata ogni volta
                 * che si avvia la main activity
                 */
                ptAlbumPath.mkdirs();

                /*
                 * Creo la cartella thumb ricordando che la funzione mkdirs()
                 * non crea la cartella se è già esistente
                 */

                new File(ptAlbumPath.toString() + "/thumb").mkdirs();

                /*
                 * Creo la thumb dell'immagine
                 */
                Bitmap thumbBitmap = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, false);

                /*
                 * Creo la thumb in formato PNG chiamandola nella stessa maniera della
                 * bitmap originale perchè, quando clicco sulla thumb nella MainActivity,
                 * si aprirà un'altra activity nella quale richiamerò l'immagine
                 * originale attraverso il nome della thumb.
                 */

                s = buildFileName();
                FileOutputStream oStream = new FileOutputStream(ptAlbumPath.toString() + "/thumb/" + s + ".PNG");
                thumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
                oStream.flush();
                oStream.close();


                sendPhoto(s +".PNG");
                /*
                 * Aggiorno l'interfaccia richiamando la MainActivity
                 */
                Intent i = new Intent(ActivityOne.this, ActivityOne.class);

                i.putExtra("name", nameP);
                i.putExtra("date", dateP);
                i.putExtra("dateEnd" , dateEnd);
                startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onTaskCompleted() {
        Toast.makeText(getApplicationContext(), "Finished", Toast.LENGTH_SHORT).show();
    }

    public class SendPhotoTask extends AsyncTask<String, Void, String> {

        Gson gson;

        @Override
        protected String doInBackground(String... params) {
            gson = new Gson();
            ClientResource cr;
            cr = new ClientResource(baseURI + params[0] + "/" + params[1] + "/" + params[2]);
            String response = null;

            // Add the client authentication to the call
            //ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
            //ChallengeResponse authentication = new ChallengeResponse(scheme, params[1], params[2]);
            //cr.setChallengeResponse(authentication);

            FileRepresentation payload = new FileRepresentation(new File(ptAlbumPath.toString() + "/thumb/" + params[2]), MediaType.IMAGE_PNG);
            try {
                response = cr.post(payload).getText();
            } catch (IOException e) {
                String text = "Error: " + cr.getStatus().getCode() + " - " +
                        cr.getStatus().getDescription()+ " - " + cr.getStatus().getReasonPhrase(); Log.e("SELLITTO_CAMERA",text);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            gson = new Gson();

            String result = gson.fromJson(res, String.class);
            if (result != null) {

                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();

            }
        }
    }

    public class SendSubscriberTask extends AsyncTask<String, Void, String> {

        Gson gson;

        @Override
        protected String doInBackground(String... params) {
            gson = new Gson();
            ClientResource cr3;
            cr3 = new ClientResource(baseURI2  + params[1] + "/" + params[2]);
            String response = null;

            // Add the client authentication to the call
            //ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
            //ChallengeResponse authentication = new ChallengeResponse(scheme, params[1], params[2]);
            //cr.setChallengeResponse(authentication);

            SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");

            String payload = username;
            try {
                response = cr3.post(gson.toJson(payload, String.class)).getText();
            } catch (IOException e) {
                String text = "Error: " + cr3.getStatus().getCode() + " - " +
                        cr3.getStatus().getDescription()+ " - " + cr3.getStatus().getReasonPhrase(); Log.e("SELLITTO_CAMERA",text);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            gson = new Gson();


            if (res != null) {

                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();

            }
        }
    }


    public class ControlTakePictureTask extends AsyncTask<String, Void, ArrayList<String>> {

        Gson gson;

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            ClientResource cr3;
            cr3 = new ClientResource(baseURI2  + params[1] + "/" + params[2]);
            String jsonResponse = null;
            ArrayList<String> partecipants = null;

            // Add the client authentication to the call
            //ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
            //ChallengeResponse authentication = new ChallengeResponse(scheme, params[1], params[2]);
            //cr.setChallengeResponse(authentication);

            try {
                jsonResponse = cr3.get().getText();

                gson = new Gson();
                partecipants = gson.fromJson(jsonResponse, new TypeToken<ArrayList<String>>(){}.getType());


            } catch (ResourceException | IOException e1) {
                String text = "Error: " + cr3.getStatus().getCode() + " - " + cr3.getStatus().getDescription() + " - " +
                        cr3.getStatus().getReasonPhrase();
                Log.e(TAG, text);
            }
            return partecipants;

        }

        @Override
        protected void onPostExecute(ArrayList<String> res) {

            SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");


            if (res.contains(username)) {
                takePicture();
            }
            else{
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityOne.this);
                builder1.setMessage("You don't have subscribed group");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
    }

    public class ControlInsertImageTask extends AsyncTask<String, Void, ArrayList<String>> {

        Gson gson;

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            ClientResource cr3;
            cr3 = new ClientResource(baseURI2  + params[1] + "/" + params[2]);
            String jsonResponse = null;
            ArrayList<String> partecipants = null;

            // Add the client authentication to the call
            //ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
            //ChallengeResponse authentication = new ChallengeResponse(scheme, params[1], params[2]);
            //cr.setChallengeResponse(authentication);

            try {
                jsonResponse = cr3.get().getText();

                gson = new Gson();
                partecipants = gson.fromJson(jsonResponse, new TypeToken<ArrayList<String>>(){}.getType());


            } catch (ResourceException | IOException e1) {
                String text = "Error: " + cr3.getStatus().getCode() + " - " + cr3.getStatus().getDescription() + " - " +
                        cr3.getStatus().getReasonPhrase();
                Log.e(TAG, text);
            }
            return partecipants;

        }

        @Override
        protected void onPostExecute(ArrayList<String> res) {

            SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");


            if (res.contains(username)) {
                insertImage();
            }
            else{
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityOne.this);
                builder1.setMessage("You don't have subscribed group");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        }
    }

    public class ReceiveNewPhotosTask extends AsyncTask<String, Void, String >{

        Gson gson;
        private Activity activity;

        private OnTaskCompleted listener;

        public ReceiveNewPhotosTask(Activity activity,Context context,OnTaskCompleted listener){
            this.activity = activity;
            this.listener = listener;

        }

        @Override
        public void onPreExecute(){
            new File(ptAlbumPath.toString() + "/thumb").mkdir();

        }

        @Override
        protected String doInBackground(String... params) {
            ClientResource cr;
            cr = new ClientResource(baseURI + params[0] + "/" + params[1]);
            ClientResource cr2;
            String jsonResponse = null;
            String jsonResponse2 = null;
            String[] namesFile;

            File file;
            //Representation entity;
            ArrayList<File> files = new ArrayList<>();
            String response = null;
            try {
                try {
                    jsonResponse = cr.get().getText();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                gson = new Gson();
                namesFile = gson.fromJson(jsonResponse, new TypeToken<String[]>() {
                }.getType());
                // c = gson.fromJson(jsonResponse, String[].class);
                for (String s : namesFile) {
                    //Log.v(TAG, s);
                    if (!fileExist(s)) {

                        cr2 = new ClientResource(baseURI + params[0] + "/" + params[1] + "/" + s);


                        file = new File(ptAlbumPath.toString() + "/thumb/" + s);
                        //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        cr2.get(MediaType.IMAGE_PNG).write(new FileOutputStream(file));


                    }

                }
            } catch (ResourceException e1) {
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " +
                        cr.getStatus().getReasonPhrase();
                Log.e(TAG, text);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(String s ){


             listener.onTaskCompleted();
             progressDialog.dismiss();

        }


    }


/*
    public class ReceiveSubscriberTask extends AsyncTask<String, Void, String> {

        Gson gson;
        ArrayList<String> subscribers = new ArrayList<>();

        @Override
        protected String doInBackground(String... params) {

            ClientResource cr;
            // this.listener = listener;
            cr = new ClientResource(baseURI);


            String jsonResponse = null;

            try {
                try {
                    jsonResponse = cr.get().getText();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                gson = new Gson();
                subscribers = gson.fromJson(jsonResponse, new TypeToken<ArrayList<String>>() {
                }.getType());


                // for (Group group : detailsGroup) {
                //Log.v(TAG, group.getTitle());

                //Log.v(TAG, String.valueOf(file.length()));
                //                }
            } catch (ResourceException e1) {
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " +
                        cr.getStatus().getReasonPhrase();
                Log.e(TAG, text);
            }



            return null;
        }
    }

*/

    public void sendSubscribers (){
        new SendSubscriberTask().execute("subscribers", nameP, dateP);
    }

    public void controlTakePicture (){
        new ControlTakePictureTask().execute("subscribers", nameP, dateP);
    }

    public void controlInsertImage (){
        new ControlInsertImageTask().execute("subscribers", nameP, dateP);
    }

    //  public void receiveSubscribers (String user){
    //  new ReceiveSubscriberTask().execute(nameP, dateP, user);
    //}

    public void sendPhoto (String imagePath){
        new SendPhotoTask().execute(nameP, dateP, imagePath);
    }

    public void receiveNewPhotos(){
        new ReceiveNewPhotosTask(ActivityOne.this, this, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted() {
                 /*
                 * E' un controllo fittizio che conferma l'avvenuta creazione
                 * della cartella thumb nel momento in cui è stata caricata
                 * la prima immagine.
                 * La variabile ptAlbumPath è pubblica per la classe e dopo
                 * la funzione directoryExist contiene il percorso della
                 * cartella PTAlbum
                 */
                new File(ptAlbumPath.toString() + "/thumb").mkdir();

                thumbPath = new File(ptAlbumPath.toString() + "/thumb");

                //   FileOutputStream oStream = new FileOutputStream(ptAlbumPath.toString() + "/thumb/" + bitmap.toString() + ".PNG");
                //   nomeImmagine.compress(Bitmap.CompressFormat.PNG, 100, oStream);
                //   oStream.flush();
                //   oStream.close();

                imgInDir = new String[thumbPath.list().length];
                imgInDir = thumbPath.list();


                //String thumbPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/PTAlbum222222/thumb/" + imgInDir[0];
                //Bitmap bitmap = BitmapFactory.decodeFile(thumbPath);


                //imageView.setImageBitmap(bitmap);
                /*
                 * Richiamo la classe imageAdaptedInGrid per visualizzare
                 * le thumb nella gridView
                 */

                gridView.setAdapter(new ImageAdaptedInGrid(ActivityOne.this, imgInDir, nameP, dateP));


            }
        }).execute(nameP, dateP);
    }
}