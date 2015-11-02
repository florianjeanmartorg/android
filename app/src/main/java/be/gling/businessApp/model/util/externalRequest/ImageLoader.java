package be.gling.businessApp.model.util.externalRequest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import be.gling.businessApp.model.dto.StoredFileDTO;
import be.gling.businessApp.model.util.ImageCache;

/**
 * Created by florian on 10/10/15.
 */
public class ImageLoader {

    private Exception exception;

    private ImageView imageView;
    private StoredFileDTO storedFileDTO;
    private Bitmap bmp;

    public ImageLoader(ImageView imageView, StoredFileDTO storedFileDTO) {

        if(imageView!=null){
            int width = imageView.getWidth();
            int x=0;
        }
        if (ImageCache.getBitmapFromMemCache(storedFileDTO) != null) {
            imageView.setImageBitmap(ImageCache.getBitmapFromMemCache(storedFileDTO));
        } else {
            this.imageView = imageView;
            this.storedFileDTO = storedFileDTO;
            new LoaderClass().execute();
        }
    }

    private class LoaderClass extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... urls) {
            URL url;
            try {
                url = new URL("https://d1zqatznadrj05.cloudfront.net/" + storedFileDTO.getStoredName());


                bmp = BitmapFactory.decodeStream(url.openStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void feed) {
            ImageCache.addBitmapToMemoryCache(storedFileDTO, bmp);
            if (imageView != null) {
                imageView.setImageBitmap(ImageCache.getBitmapFromMemCache(storedFileDTO));
            }
        }
    }
}
