package edmt.dev.androidgridlayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by tonysellitto on 27/01/18.
 */

public class ListGroupsActivity extends AppCompatActivity{
    private SectionsPagerAdapter mSectionsPagerAdapter;

    ProgressDialog progressDialog;
    private static final String TAG = "TONY LOADING GROUPS";
    private String baseURI = "http://10.0.2.2:8182/groupsRegApplication/listGroups";

    static ArrayList<Group> detailsGroup = new ArrayList<>();

    ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listgroups);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarListGroups);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("List Groups");

        receiveGroups();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Choice group by time", Snackbar.LENGTH_LONG)
                        .setAction("", null).show();
            }
        });



    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    /**
     * A placeholder fragment containing a simple view.
     */
    public class ReceiveGroupsTask extends AsyncTask<String, Void, String> {

        Gson gson;
        ArrayList<Group> groupsFromJson = new ArrayList<>();
        private OnTaskCompleted listener;
        @Override
        public void onPreExecute(){
            progressDialog = new ProgressDialog(ListGroupsActivity.this, R.style.full_screen_dialog);
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


            return null;
        }

        @Override
        protected void onPostExecute(String s ){
            // listener.onTaskCompleted();

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

            progressDialog.dismiss();

        }


    }

    public void receiveGroups(){
        new ListGroupsActivity.ReceiveGroupsTask().execute();
    }



    public static class PlaceholderFragment extends Fragment {
        ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();
        ListView listView;
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {

        }


        public static PlaceholderFragment newInstance(int sectionNumber) {


            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            listView = (ListView)rootView.findViewById(R.id.listView);
            if (sectionNumber == 0){
                //detailsGroup.clear();
                data.clear();

                populatePast();

            }else if (sectionNumber == 1){

                //detailsGroup.clear();
                data.clear();

                populatePresent();
            }else if (sectionNumber == 2){
                //rootView = inflater.inflate(R.layout.main_fragment, container, false);
                //detailsGroup.clear();
                data.clear();

                populateFuture();
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> av, View v, int pos, long id)
                {

                    Toast.makeText(getContext(),
                            "Selected "+ data.get(pos).get("name"), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getContext(), ActivityOne.class);

                    i.putExtra("name", data.get(pos).get("name").toString());
                    i.putExtra("date", data.get(pos).get("startDate").toString());
                    i.putExtra("dateEnd", data.get(pos).get("endDate").toString());

                    startActivity(i);
                }
            });

            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);

            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

        public void populatePast() {


            Group group;

            for(int i=0;i<detailsGroup.size();i++) {
                group = detailsGroup.get(i);
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");


                //Date start = null;

                Date end = null;

                Date now = new Date();
                //date = formatter.parse(dateInString);
                try {
                    end = formatter.parse(group.getEndDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (end.compareTo(now) < 0) {

                    HashMap<String, Object> groupsMap = new HashMap<String, Object>();


                    groupsMap.put("image", R.drawable.family_time); // per la chiave image, inseriamo la risorsa dell immagine
                    groupsMap.put("name", group.getTitle()); // per la chiave name,l'informazine sul nome
                    groupsMap.put("description", group.getDescription());
                    //groupsMap.put("startDate",  group.getStartDate().replaceAll("_{1,}", " "));
                    groupsMap.put("startDate", group.getStartDate());
                    groupsMap.put("endDate", group.getEndDate());

                    // per la chiave surname, l'informazione sul cognome
                    data.add(groupsMap);//aggiungiamo la mappa di valori alla sorgente dati
                    //Log.v(TAG, "Questo è il gruppo N°: " + i + " " + group.toString());

                    String[] from = {"image", "name", "startDate", "endDate", "description"}; //dai valori contenuti in queste chiavi
                    int[] to = {R.id.groupImage, R.id.nameGroup, R.id.dateStartGroup, R.id.dateEndGroup, R.id.description};//agli id delle view


                    //costruzione dell adapter

                    SimpleAdapter adapter = new SimpleAdapter(
                            this.getContext(), data, R.layout.row_item, from, to);
                    //utilizzo dell'adapter
                    listView.setAdapter(adapter);
                }

            }

        }


        public void populateFuture() {


            Group group;

            for(int i=0;i<detailsGroup.size();i++) {
                group = detailsGroup.get(i);// per ogni persona all'inteno della ditta
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");


                Date start = null;

                //Date end = null;

                Date now = new Date();
                //date = formatter.parse(dateInString);
                try {
                    start = formatter.parse(group.getStartDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (start.compareTo(now) > 0) {
                    HashMap<String, Object> groupsMap = new HashMap<String, Object>();
                    //creiamo una mappa di valori
                    //Calendar cal = Calendar.getInstance();
                    //cal.setTime(group.getStartDate());


                    groupsMap.put("image", R.drawable.family_time); // per la chiave image, inseriamo la risorsa dell immagine
                    groupsMap.put("name", group.getTitle()); // per la chiave name,l'informazine sul nome
                    groupsMap.put("description", group.getDescription());
                    //groupsMap.put("startDate",  group.getStartDate().replaceAll("_{1,}", " "));
                    groupsMap.put("startDate", group.getStartDate());
                    groupsMap.put("endDate", group.getEndDate());

                    // per la chiave surname, l'informazione sul cognome
                    data.add(groupsMap);//aggiungiamo la mappa di valori alla sorgente dati
                    //Log.v(TAG, "Questo è il gruppo N°: " + i + " " + group.toString());

                    String[] from = {"image", "name", "startDate", "endDate", "description"}; //dai valori contenuti in queste chiavi
                    int[] to = {R.id.groupImage, R.id.nameGroup, R.id.dateStartGroup, R.id.dateEndGroup, R.id.description};//agli id delle view


                    //costruzione dell adapter

                    SimpleAdapter adapter = new SimpleAdapter(
                            this.getContext(), data, R.layout.row_item, from, to);
                    //utilizzo dell'adapter
                    listView.setAdapter(adapter);
                }

            }

        }

        public void populatePresent() {

            Group group;

            for(int i=0;i<detailsGroup.size();i++) {
                group = detailsGroup.get(i);// per ogni persona all'inteno della ditta
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");


                Date start = null;

                Date end = null;

                Date now = new Date();
                //date = formatter.parse(dateInString);
                try {
                    start = formatter.parse(group.getStartDate());
                    end = formatter.parse(group.getEndDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (start.compareTo(now) < 0 && end.compareTo(now) > 0) {
                    HashMap<String, Object> groupsMap = new HashMap<String, Object>();

                    groupsMap.put("image", R.drawable.family_time); // per la chiave image, inseriamo la risorsa dell immagine
                    groupsMap.put("name", group.getTitle()); // per la chiave name,l'informazine sul nome
                    groupsMap.put("description", group.getDescription());
                    //groupsMap.put("startDate",  group.getStartDate().replaceAll("_{1,}", " "));
                    groupsMap.put("startDate", group.getStartDate());
                    groupsMap.put("endDate", group.getEndDate());

                    // per la chiave surname, l'informazione sul cognome
                    data.add(groupsMap);//aggiungiamo la mappa di valori alla sorgente dati
                    //Log.v(TAG, "Questo è il gruppo N°: " + i + " " + group.toString());

                    String[] from = {"image", "name", "startDate", "endDate", "description"}; //dai valori contenuti in queste chiavi
                    int[] to = {R.id.groupImage, R.id.nameGroup, R.id.dateStartGroup, R.id.dateEndGroup, R.id.description};//agli id delle view


                    //costruzione dell adapter

                    SimpleAdapter adapter = new SimpleAdapter(
                            this.getContext(), data, R.layout.row_item, from, to);
                    //utilizzo dell'adapter
                    listView.setAdapter(adapter);
                }

            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

}
