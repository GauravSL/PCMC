package com.example.pcmcdiwyang;

import android.graphics.Bitmap;
import android.os.Build;

import com.example.pcmcdiwyang.scanners.face.tflite.SimilarityClassifier;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Temp {
    public static Bitmap bitmap;
    public static String recognitionToSend;
    public static SimilarityClassifier.Recognition face;
    public static String fingerPrint;
    public static String iris;
    public static boolean faceRecognitionSuccess;

    public static SimilarityClassifier.Recognition getRecognationData(String json){
        Gson gson = new Gson();

        SimilarityClassifier.Recognition user= gson.fromJson(json, SimilarityClassifier.Recognition.class);

        user.setExtra(null);
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray array = obj.getJSONArray("extra");
            user.setExtra(fillData(array.getJSONArray(0)));
        }catch (Exception e){
            e.printStackTrace();
        }

        return user;
    }

    private static float[][] fillData(JSONArray jsonArray){

        float[][] fData = new float[1][jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                fData[0][i] = Float.parseFloat(jsonArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return fData;
    }

    public static byte[] StringToByteArray(String value){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getDecoder().decode(value);
        }
        return null;
    }

    public static String ByteArrayToString(byte[] value){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(value);
        }
        return "";
    }

}
