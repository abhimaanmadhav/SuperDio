package it.jaschke.alexandria.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import it.jaschke.alexandria.R;

/**
 * Created by saj on 11/01/15.
 */
public class DownloadImage extends AsyncTask<String, Void, Bitmap>
{
    ImageView bmImage;

    public DownloadImage(ImageView bmImage)
        {
            this.bmImage = bmImage;
        }

    protected Bitmap doInBackground(String... urls)
        {
            String urlDisplay = urls[0];
            Bitmap bookCover = null;
//        try {
            InputStream in = null;
            try
                {
                    in = new java.net.URL(urlDisplay).openStream();
                } catch (IOException e)
                {
                    //catch only know exception so that when things go wrong it can be fixed
                    // rather than showing blank data
                    Log.e("download image", "url " + urlDisplay);
                    e.printStackTrace();
                    return null;
                }
            bookCover = BitmapFactory.decodeStream(in);
//        } catch (Exception e) {
//            Log.e("Error", e.getMessage());
//            e.printStackTrace();
//        }
            return bookCover;
        }

    protected void onPostExecute(Bitmap result)
        {
            //showing a place holder
            if (result != null)
                {
                    bmImage.setImageBitmap(result);
                } else
                {
                    bmImage.setImageResource(R.drawable.error);
                }
        }
}

