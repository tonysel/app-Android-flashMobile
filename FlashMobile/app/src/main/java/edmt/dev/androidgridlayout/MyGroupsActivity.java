package edmt.dev.androidgridlayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tonysellitto on 17/01/18.
 */
public class MyGroupsActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    private static final String TAG = "TONY LOADING GROUPS";
    //private String baseURI = "http://10.0.2.2:8182/groupsRegApplication/groups";
    private String baseURI = "http://10.0.2.2:8182/groupsRegApplication/listGroups";

    ArrayList<Group> detailsGroup = new ArrayList<>();
    ArrayList<Group> detailsGroup2 = new ArrayList<>();

    ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

    ListView listView;

    // @Override
    // public void onTaskCompleted() {
    // }


    public class ReceiveGroupsTask extends AsyncTask<String, Void, String> {

        Gson gson;
        ArrayList<Group> groupsFromJson = new ArrayList<>();
        private OnTaskCompleted listener;
        @Override
        public void onPreExecute(){
            progressDialog = new ProgressDialog(MyGroupsActivity.this, R.style.full_screen_dialog);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            ClientResource cr;
            // this.listener = listener;
            cr = new ClientResource(baseURI);



            String jsonResponse = null;
/*
            SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");
            String password = sharedPreferences.getString("password", "");
            Boolean authenticated = sharedPreferences.getBoolean("autheticated",false);

            ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
            ChallengeResponse authentication = new ChallengeResponse(scheme, username, password);
            cr.setChallengeResponse(authentication);
*/
            try {
                try {
                    jsonResponse = cr.get().getText();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                gson = new Gson();
                detailsGroup = gson.fromJson(jsonResponse, new TypeToken<ArrayList<Group>>(){}.getType());




                // for (Group group : detailsGroup) {
                //Log.v(TAG, group.getTitle());

                //Log.v(TAG, String.valueOf(file.length()));
                //                }
            } catch (ResourceException e1) {
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " +
                        cr.getStatus().getReasonPhrase();
                Log.e(TAG, text);
            }



            progressDialog.dismiss();

            return null;
        }

        @Override
        protected void onPostExecute(String s ){
            // listener.onTaskCompleted();

            SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");

            for (Group group: detailsGroup){
               if (group.getPartecipants().contains(username)){
                    detailsGroup2.add(group);
               }
            }

            populate();

            /*Log.v(TAG, String.valueOf(detailsGroup.size()));
            for (int i = 0; i < data.size(); i++){
                Log.i(TAG,data.get(i).values().toString());
            }*/

        }


    }

    public void receiveGroups(){
        new ReceiveGroupsTask().execute();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarGroups);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Groups");

        listView = (ListView)findViewById(R.id.listView);

        receiveGroups();

/*
        Group g1 = new Group("coco", "none", date, date);
        Group g2 = new Group("cocolla", "none", date, date);
        Group g3 = new Group("cocozza", "none", date, date);
        Group g4 = new Group("curruco", "none", date, date);

        detailsGroup.add(g1);
        detailsGroup.add(g2);
        detailsGroup.add(g3);
        detailsGroup.add(g4);
*/


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id)
            {

                Toast.makeText(getApplicationContext(),
                        "Selected "+ data.get(pos).get("name"), Toast.LENGTH_LONG).show();
                Intent i = new Intent(MyGroupsActivity.this, ActivityTwo.class);

                i.putExtra("name", data.get(pos).get("name").toString());
                i.putExtra("date", data.get(pos).get("startDate").toString());
                i.putExtra("dateEnd", data.get(pos).get("endDate").toString());

                startActivity(i);
            }
        });

    }

    public void populate() {

        Group group;

        //SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss a");

        for(int i=0;i<detailsGroup2.size();i++) {
            group = detailsGroup2.get(i);// per ogni persona all'inteno della ditta
            HashMap<String,Object> groupsMap =new HashMap<String, Object>();
            //creiamo una mappa di valori
            //Calendar cal = Calendar.getInstance();
            //cal.setTime(group.getStartDate());
            group.toString();
            groupsMap.put("image", R.drawable.team_time); // per la chiave image, inseriamo la risorsa dell immagine
            groupsMap.put("name", group.getTitle()); // per la chiave name,l'informazine sul nome
            groupsMap.put("description", group.getDescription());
            groupsMap.put("startDate",  group.getStartDate().toString());
            groupsMap.put("endDate", group.getEndDate().toString());

            // per la chiave surnaname, l'informazione sul cognome
            data.add(groupsMap);//aggiungiamo la mappa di valori alla sorgente dati
            //Log.v(TAG, "Questo è il gruppo N°: " + i + " " + group.toString());

            String[] from={"image","name","startDate", "endDate", "description"}; //dai valori contenuti in queste chiavi
            int[] to={R.id.groupImage,R.id.nameGroup,R.id.dateStartGroup, R.id.dateEndGroup, R.id.description};//agli id delle view





            //costruzione dell adapter

            SimpleAdapter adapter=new SimpleAdapter(
                    MyGroupsActivity.this,data, R.layout.row_item, from, to);
            //utilizzo dell'adapter
            listView.setAdapter(adapter);}
        //agli id delle view



        // Log.v(TAG, "Questo è il gruppo N°: " + i + " " + group.toString());





    }

}