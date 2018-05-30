package edmt.dev.androidgridlayout;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import static android.content.ContentValues.TAG;


/**
 * Created by tonysellitto on 02/01/18.
 */

class ImageAdaptedInGrid extends BaseAdapter {
    public Context mContext;
    public String[] imgInDir;
    public String name;
    public String date;

    //Costruttore
    public ImageAdaptedInGrid(Context c, String[] s, String nameP, String dateP)
    {
        mContext = c;
        imgInDir = s;
        name = nameP;
        date = dateP;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imgInDir.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return imgInDir[position];
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ImageView imageView = new ImageView(mContext);

        /*
         * Recupero il percorso della thumb dell'immagine in posizione position
         * nel vettore di stringhe imgInDir
         */

        String thumbPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/PTAlbum/" + name + date + "/thumb/"  + imgInDir[position];


        //Log.v(TAG, name);
        /*
         * Per visualizzare la thumb nella Grid devo creare la bitmap
         */
        // controlliamo se la permission è concessa

            //BitmapFactory.Options options;

            //options = new BitmapFactory.Options();
            //options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(thumbPath);


            imageView.setImageBitmap(bitmap);
            imageView.setLayoutParams(new GridView.LayoutParams(500, 335));
            return imageView;

    }
}

