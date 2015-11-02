package be.gling.businessApp.view.widget.technical;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.Date;

import be.gling.businessApp.R;
import be.gling.businessApp.model.util.ImageCache;
import be.gling.businessApp.model.util.KeyGenerator;

/**
 * Created by florian on 15/10/15.
 */
public class ImageFieldView extends LinearLayout implements InputField {

    private final static int REQUEST_IMAGE_CAPTURE = 1;
    private final static int REQUEST_TAKE_PHOTO = 2;
    private static final String PICTURE_SUFIX = "_PICTURE";
    private String storageKey = null;
    private String originalPicturePath = null;


    public ImageFieldView(Context context) {
        this(context, null);
    }

    public ImageFieldView(final Context context, AttributeSet attrs) {
        super(context);

        //build layout
        setOrientation(VERTICAL);
        setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        //create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // **** BUILD WIDGET ****

        //for each field : create a view and insert it
        View view = inflater.inflate(R.layout.field_image_view, null);
        addView(view);


        //create a new picture
        ((Button) findViewById(R.id.form_image_btn_create)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                    ((Activity) context).startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }


                if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        // Error occurred while creating the File
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        ((Activity) context).startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }


            }
        });

        //load a picture a new picture
        view.findViewById(R.id.form_image_btn_load).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        this.originalPicturePath = "file:" + image.getAbsolutePath();
        return image;
    }

    public void managerResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView viewById = (ImageView) findViewById(R.id.picture_result);

            storageKey = KeyGenerator.generateRandomKey(20);

            ImageCache.addTempToMemoryCache(storageKey, imageBitmap);

            viewById.setImageBitmap(imageBitmap);
        }
    }


    @Override
    public void setValue(Object value) {
        //TODO ??
    }

    @Override
    public Object getValue(Class<?> type) {
        return originalPicturePath;
    }

    @Override
    public Integer control(Annotation[] declaredAnnotations) {
        return null;
    }

    @Override
    public void saveToInstanceState(String name, Bundle savedInstanceState) {
        if (storageKey != null) {
            savedInstanceState.putString(name, storageKey);
            savedInstanceState.putString(name + PICTURE_SUFIX, originalPicturePath);
        }
    }

    @Override
    public void restoreFromInstanceState(String name, Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(name)) {

            //load values
            storageKey = savedInstanceState.getString(name);
            originalPicturePath = savedInstanceState.getString(name + PICTURE_SUFIX);

            //load picture
            Bitmap imageBitmap = ImageCache.getTempFromMemCache(storageKey);
            ImageView viewById = (ImageView) findViewById(R.id.picture_result);
            viewById.setImageBitmap(imageBitmap);
        }
    }
}
