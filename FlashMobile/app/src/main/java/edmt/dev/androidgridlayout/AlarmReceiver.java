package edmt.dev.androidgridlayout;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;



import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tonysellitto on 21/01/18.
 */
public class AlarmReceiver extends BroadcastReceiver {
    int numMessages = 0;

    public static final String NOTIFICATION_CHANNEL_ID = "4566";

    private static final String TAG = "TONY LOADING GROUPS";
    private String baseURI = "http://10.0.2.2:8182/groupsRegApplication/listGroups";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            receiveGroups(context, intent);

        }
        catch (Exception e){
            Log.i("date","error == "+e.getMessage());
        }
    }

    public class ReceiveGroupsTask extends AsyncTask<String, Void, String> {
        int count = 0;
        int groups = 0;
        Gson gson;
        //int number = 0;
        ArrayList<Group> detailsGroup = new ArrayList<>();
        private Context context;
        private Intent intent;
        public ReceiveGroupsTask(Context context , Intent intent){
            this.context = context;
            this.intent = intent;
        }
        @Override
        public void onPreExecute() {


        }

        @Override
        protected String doInBackground(String... params) {

            ClientResource cr;
            // this.listener = listener;
            cr = new ClientResource(baseURI);


            String jsonResponse = null;

            //SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
            //String username = sharedPreferences.getString("username", "");
            //String password = sharedPreferences.getString("password", "");
            //Boolean authenticated = sharedPreferences.getBoolean("autheticated", false);


            //ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
            //ChallengeResponse authentication = new ChallengeResponse(scheme, username, password);
            //cr.setChallengeResponse(authentication);
            try {
                try {
                    jsonResponse = cr.get().getText();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                gson = new Gson();
                detailsGroup = gson.fromJson(jsonResponse, new TypeToken<ArrayList<Group>>() {
                }.getType());

            }catch(ResourceException e1){
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " +
                        cr.getStatus().getReasonPhrase();
                Log.e(TAG, text);
            }
            return null;
        }

        public void onPostExecute(String res){
            Date d = new Date();

            String timeStamp =
                    new SimpleDateFormat("yyyy-MM-dd_HH:mm").format(new Date());
            /*
            if (number == 0){
                number = detailsGroup.size();
            }

            if(number != detailsGroup.size()){
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                                .setSmallIcon(R.drawable.family_time)
                                .setContentTitle("Hi Tony")
                                .setContentText("NEW --- There are new groups to subscribe");

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                String channelId = "4566";
                CharSequence channelName = "channel FlashMob";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationChannel = new NotificationChannel(channelId, channelName, importance);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationChannel.enableLights(true);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationChannel.setLightColor(Color.RED);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationChannel.enableVibration(true);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mNotificationManager.createNotificationChannel(notificationChannel);
                }

                Intent resultIntent = new Intent(context, ListGroupsActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);


                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);

                mNotificationManager.notify(0, mBuilder.build());
            }
*/
            for (Group group : detailsGroup) {
                DateFormat date = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
                //String now = "2018-02-02_13:10";
                count++;
                Date start = null;
                Date now = null;
                try {
                    //start = date.parse(group.getStartDate());
                    start = date.parse(group.getStartDate());
                    now = date.parse(timeStamp);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //if(start.compareTo(d) > 0){
                //  groups++;
                //}

                if(start.compareTo(now) == 0){
                    groups++;
                }
                if (count == detailsGroup.size() && groups > 0) {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                                    .setSmallIcon(R.drawable.family_time)
                                    .setContentTitle("Hi Tony")
                                    .setContentText("NEW --- " + groups + " groups are started");

                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    String channelId = "4566";
                    CharSequence channelName = "channel FlashMob";
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationChannel = new NotificationChannel(channelId, channelName, importance);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationChannel.enableLights(true);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationChannel.setLightColor(Color.RED);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        notificationChannel.enableVibration(true);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mNotificationManager.createNotificationChannel(notificationChannel);
                    }

                    Intent resultIntent = new Intent(context, ListGroupsActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);


                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    mNotificationManager.notify(0, mBuilder.build());

                    count = 0;
                }
            }
        }

    }




    public void receiveGroups(Context context, Intent intent){
        new ReceiveGroupsTask(context, intent).execute();
    }

}