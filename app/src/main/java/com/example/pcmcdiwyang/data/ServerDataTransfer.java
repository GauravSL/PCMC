package com.example.pcmcdiwyang.data;


import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ServerDataTransfer {

    String TAG = ServerDataTransfer.class.toString();
    private String bodyData = null;
    //private String BASE_URL= "http://192.168.29.53:8080/ServerHealthBuddy/rest/healthbuddy/";
    private String BASE_URL= "http://192.168.213.215:8080/ServerHealthBuddy/rest/healthbuddy/";
   // private String BASE_URL= "http://192.168.213.215:8080/ServerHealthBuddy/rest/healthbuddy/";


    public Response accessAPI(String url, String methodType, String jsonData) throws IOException {

        //url = BASE_URL + url;
        url = BASE_URL + url;
        //methodType = methodType;
        HashMap<String, String> data = null;
        //bodyData = "{\"message\":\"Hello\"}";
        bodyData = jsonData;
        StringBuilder response = new StringBuilder();
        Response responseObject = new Response();
        Log.v(TAG, " URL " + url);

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};


        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        if (sslContext != null && httpURLConnection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) httpURLConnection;
            httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());

            //To be skipped in case of production
           /* httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession sslSession) {

                    Log.v("RestUtilImpl", "Approving certificate for " + hostname);
                    return true;
                }
            });*/
            httpURLConnection = httpsURLConnection;
        }

        httpURLConnection.setConnectTimeout(120000);
        httpURLConnection.setReadTimeout(120000);
        httpURLConnection.setRequestMethod(methodType);


        if (data != null) {

            if (data.containsKey("Authorization")) {
                //Log.v(TAG, "Token Refresh bearer token " + data.get("Authorization"));

                httpURLConnection.setRequestProperty("Authorization", data.get("Authorization"));
                data.remove("Authorization");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf8");
            }
            /*if (shoulOverrideToGet)
                httpURLConnection.setRequestProperty("X-HTTP-Method-Override", "GET");*/

            if (!data.isEmpty()) {
                Uri.Builder uriBuilder = new Uri.Builder();
                for (String headerName : data.keySet()) {
                    uriBuilder.appendQueryParameter(headerName, data.get(headerName));
                }
                httpURLConnection.getOutputStream().write(uriBuilder.build().getEncodedQuery().getBytes());
            }
            /* Use when CSFR support is needed and add (&& !url.contains(Constants.USER_FORGOT_PASSWORD)
            this condition to above if loop
            if (!data.isEmpty() && url.contains(Constants.USER_FORGOT_PASSWORD)){
                for (String headerName : data.keySet()) {
                    httpURLConnection.setRequestProperty(headerName, data.get(headerName));
                }
            }*/
        }

        if (bodyData != null) {
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.getOutputStream().write(bodyData.getBytes());
        }

        int responseCode = httpURLConnection.getResponseCode();
        Log.v(TAG, "response code " + responseCode);
        responseObject.setStatusCode(responseCode);

        switch (responseCode) {

            case HttpURLConnection.HTTP_OK:
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                if (response.length() == 0) {
                    response.append("200");
                }
                /* Use this code when CSFR support needed
                if (url.contains(Constants.GET_TOKEN)){
                    response = new StringBuilder();
                    response.append(processResponseHeaders(httpURLConnection.getHeaderFields()));
                }*/

                if (url.contains("/document/download/")){
                    // Log.e("Gaurav", FILE_PATH);
                    //  downloadFile("", httpURLConnection.getInputStream(), httpURLConnection.getContentLength());
                }
                break;

            case HttpURLConnection.HTTP_UNAVAILABLE:

                //Utils.serviceUnavailableMessage();
                break;

            case HttpURLConnection.HTTP_CREATED:
                String responseline;
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((responseline = bufferedreader.readLine()) != null) {
                    response.append(responseline);
                }
                if (response.length() == 0) {
                    response.append("200");
                }
                break;

            case HttpsURLConnection.HTTP_UNAUTHORIZED:

                Log.v("DataTransfer", " Token refresh for " + url);

                break;
            case HttpURLConnection.HTTP_BAD_REQUEST:

                String badResponseline;
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                while ((badResponseline = reader.readLine()) != null) {
                    response.append(badResponseline);
                }

                break;
            case HttpURLConnection.HTTP_BAD_METHOD: {
                String badResponseline1;
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                while ((badResponseline = reader1.readLine()) != null) {
                    response.append(badResponseline);
                }
            }


            break;
            case HttpURLConnection.HTTP_INTERNAL_ERROR:

                String errorResponse = "";
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                while ((badResponseline = errorReader.readLine()) != null) {
                    response.append(badResponseline);
                }

                try {

                    JSONObject errorJSON = new JSONObject(response.toString());
                    if (errorJSON.has("error")) {
                        // Utils.showErrorMessage(Dashboard.context,"errorResponse",errorJSON.getString("message"));
                        //Utils.showErrorMessage(Dashboard.context, errorJSON.getString("error"), errorJSON.getString("message"));
                        //Toast.makeText(ResilincApplication.getContext(),errorJSON.getString("error"),Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //   Utils.showErrorMessage(Dashboard.context,"Something went wrong. Try again later!","");

                break;

            case HttpURLConnection.HTTP_FORBIDDEN:

                String forbiddenResponse = "";
                BufferedReader forbiddenReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                while ((badResponseline = forbiddenReader.readLine()) != null) {
                    response.append(badResponseline);
                }

                try {

                    JSONObject errorJSON = new JSONArray(response.toString()).getJSONObject(0);
                    if (errorJSON.has("errorDescription")) {
                        //Utils.showErrorMessage(Dashboard.context,"errorResponse",errorJSON.getString("message"));
                    } else {

                        String erroMessage = errorJSON.getString("errorCode");
                        String messageDescription = "";
                        if (erroMessage.contains("INVALID_OPERATION_WITH_EXPIRED_PASSWORD") || erroMessage.contains("MUTUAL_AUTHENTICATION_FAILED")) {

                            messageDescription = "Password expired";

                            /*if(!LoginActivity.isRunning){
                                Intent loginIntent = new Intent(ResilincApplication.getApplication().getApplicationContext(), LoginActivity.class);
                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                ResilincApplication.getApplication().getApplicationContext().startActivity(loginIntent);
                                ((Dashboard)Dashboard.context).finish();
                                new Utils().deleteAccount(Dashboard.context,ResilincApplication.getApplication().getAuthToken());
                            }*/
                        }

                        // Utils.showErrorMessage(Dashboard.context,messageDescription,errorJSON.getString("message"));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
        responseObject.setResponse(response.toString());
        Log.v("DataTransfer", url + " response : " + response);

        return responseObject;
    }
}
