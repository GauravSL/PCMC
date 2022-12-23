package com.example.pcmcdiwyang;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class Utils {

    public static boolean isInternetAvailable(Context context) {
        boolean status = false;
        ConnectivityManager cm = (ConnectivityManager)           context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        } else {
            Toast.makeText(context, context.getResources().getText(R.string.txt_no_internet), Toast.LENGTH_LONG).show();
            return false;
        }
        return status;
    }

    public static class ImageUtils{
        public static Bitmap convert(String base64Str) throws IllegalArgumentException
        {
            byte[] decodedBytes = Base64.decode(
                    base64Str.substring(base64Str.indexOf(",")  + 1),
                    Base64.DEFAULT
            );

            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }

        public static String convert(Bitmap bitmap)
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        }
    }
}
