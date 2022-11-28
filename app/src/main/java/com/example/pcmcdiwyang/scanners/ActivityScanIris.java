package com.example.pcmcdiwyang.scanners;

import static com.mantra.midirisauth.enums.ImageFormat.BMP;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pcmcdiwyang.R;
import com.mantra.midirisauth.DeviceInfo;
import com.mantra.midirisauth.IrisAnatomy;
import com.mantra.midirisauth.MIDIrisAuth;
import com.mantra.midirisauth.MIDIrisAuthNative;
import com.mantra.midirisauth.MIDIrisAuth_Callback;
import com.mantra.midirisauth.enums.DeviceDetection;
import com.mantra.midirisauth.enums.DeviceModel;
import com.mantra.mis100.IrisData;
import com.mantra.mis100.MIS100;
import com.mantra.mis100.MIS100Event;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActivityScanIris extends AppCompatActivity implements MIDIrisAuth_Callback {

    Button btnInit;
    Button btnUninit;
    Button btnSyncCapture;
    Button btnStopCapture;
    Button btnSubmit;
    Button btnClearLog;
    TextView lblMessage;
    EditText txtEventLog;
    ImageView imgIris;
    EditText edtQuality;
    EditText edtTimeOut;
    LinearLayout buttonContainer;
    private MIDIrisAuth midIrisAuth;
    String deviceName = "";
    int minQuality = 85;
    private final Paint paint = new Paint();
    boolean isAutoCapture = false;
    private Bitmap previewBitmap;
    private DeviceInfo lastDeviceInfo;
    int BmpHeaderlength = 1078;
    private byte[] lastCapIrisData;
    private Thread captureThread = null;
    int timeOut = 10000;


    @Override
    public void OnDeviceDetection(String deviceName, DeviceDetection detection) {
        if (detection == DeviceDetection.CONNECTED) {
            this.deviceName = deviceName;
            SetTextOnUIThread("Device connected"+ deviceName);
        }
        else if (detection == DeviceDetection.DISCONNECTED) {
            try {
                SetTextOnUIThread("Device Not Connected");
                try {
                    midIrisAuth.Uninit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void OnPreview(int ErrorCode, int Quality, byte[] Image, IrisAnatomy irisAnatomy) {
        Log.e("AUTH_SAMPLE", "OnPreview >>");
        try {
            if (ErrorCode == 0 && Image != null) {

                int lowerValue = 0;
                int upperValue = minQuality;
                if(!isAutoCapture) {
                    if (minQuality > 40) {
                        lowerValue = minQuality - 30;
                    } else {
                        lowerValue = 10;
                    }
                }
                else {
                    lowerValue = 55;
                    upperValue = 85;
                }

                if (Quality < lowerValue) {
                    paint.setColor(Color.RED);
                } else if (Quality >= lowerValue && Quality <= upperValue) {
                    paint.setColor(Color.BLUE);
                } else {
                    paint.setColor(Color.GREEN);
                }

                Bitmap bitmap = BitmapFactory.decodeByteArray(Image, 0, Image.length);
                Bitmap bmOverlay = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_4444);

                Canvas canvas = new Canvas(bmOverlay);
                canvas.drawBitmap(bitmap, new Matrix(), null);

                int x = irisAnatomy.irisX;
                int y = irisAnatomy.irisY;
                int r = irisAnatomy.irisR;

                int fy = 0;
                int fx = 0;
//            if (y > 1) fy = 480 - y;s
                fy = y;
//                if (x > 1) {
//                    fx = 640 - x;
//                }
                fx = x;
                canvas.drawCircle(fx, fy, r, paint);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmOverlay.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bitmap.recycle();
                bmOverlay.recycle();
                ShowImage(byteArray);

				/*bitmap = null;
				bmOverlay = null;
				canvas = null;
				byteArray = null;*/

                SetTextOnUIThread("Preview Quality: " + Quality);
            } else {
                SetTextOnUIThread("Preview Error Code: " + ErrorCode + " (" + midIrisAuth.GetErrorMessage(ErrorCode) + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("AUTH_SAMPLE", "<< OnPreview");
    }

    @Override
    public void OnComplete(int ErrorCode, int Quality, byte[] Image, IrisAnatomy anatomy) {
        try {
            if (ErrorCode == 0) {

                String log = "Capture Success";
                String quality = "Quality: " + Quality /*+ " NFIQ: " + NFIQ*/;
                SetTextOnUIThread(log +"\n"+ quality);
                if (scannerAction == ScannerAction.Capture) {
                    int Size = lastDeviceInfo.Width * lastDeviceInfo.Height + BmpHeaderlength;
                    byte[] bImage = new byte[Size];
                    int[] tSize = new int[Size];
                    int ret = midIrisAuth.GetImage(bImage, tSize, 0, BMP);
                    // midFingerAuth.GetTemplate(bImage, tSize, captureTemplateDatas);
                    if (ret == 0) {
//                        lastCapFingerData = bImage;
                        lastCapIrisData = new byte[Size];
                        System.arraycopy(bImage, 0, lastCapIrisData, 0,
                                bImage.length);
//                scannerAction = ScannerAction.MatchAnsi;
                    } else {
//                        setLogs("Please run start capture or auto capture first");
                        SetTextOnUIThread(midIrisAuth.GetErrorMessage(ret));
                    }
                }
            } else {
                imgIris.post(new Runnable() {
                    @Override
                    public void run() {
                        imgIris.setImageResource(R.drawable.eye_icon);
                    }
                });
                SetTextOnUIThread("CaptureComplete: " + ErrorCode + " (" + midIrisAuth.GetErrorMessage(ErrorCode) + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ShowImage(final byte[] image) {
        imgIris.post(new Runnable() {
            @Override
            public void run() {
                if (image != null) {
                    if (previewBitmap != null) {
                        previewBitmap.recycle();
                        previewBitmap = null;
                    }
                    previewBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    imgIris.setImageBitmap(previewBitmap);
                } else {
                    imgIris.setImageResource(R.drawable.eye_icon);
                }
            }
        });
    }


    private enum ScannerAction {
        Capture
    }

    byte[] Enroll_Template;
    ScannerAction scannerAction = ScannerAction.Capture;

    MIS100 mis100 = null;

    private static boolean isCaptureRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_iris);

        /*try {
            File file = new File(Environment.getExternalStorageDirectory() + "/MIS100/");
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdirs();
            }
        } catch (Exception ignored) {
        }*/
        FindFormControls();
        try {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        midIrisAuth = new MIDIrisAuth(this, this);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_scan_iris);
        FindFormControls();

       /* if (isCaptureRunning) {
            if (mis100 != null) {
                mis100.StopAutoCapture();
            }
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mis100 == null) {
                        mis100 = new MIS100(ActivityScanIris.this);
                        mis100.SetApplicationContext(ActivityScanIris.this);
                    } else {
                        InitScanner();
                    }
                } catch (UnsatisfiedLinkError e){
                    SetTextOnUIThread("Unable to Configure Device");
                    buttonContainer.setVisibility(View.GONE);
                }

            }
        }, 2000);*/


    }

    @Override
    protected void onStart() {
            /*if (mis100 == null) {
                mis100 = new MIS100(this);
                try {
                    mis100.SetApplicationContext(ActivityScanIris.this);
                } catch (UnsatisfiedLinkError e){
                    SetTextOnUIThread("Unable to Configure Device");
                    buttonContainer.setVisibility(View.GONE);
                }
            } else {
                InitScanner();
            }*/
        super.onStart();
    }

    protected void onStop() {
       // UnInitScanner();
        if (midIrisAuth.IsCaptureRunning()) {
            stopIrisCapture();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        /*if (mis100 != null) {
            mis100.Dispose();
        }*/
        try {
            midIrisAuth.Uninit();
            midIrisAuth.Dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void FindFormControls() {
        btnInit = (Button) findViewById(R.id.btnInit);
        btnUninit = (Button) findViewById(R.id.btnUninit);
        btnClearLog = (Button) findViewById(R.id.btnClearLog);
        lblMessage = (TextView) findViewById(R.id.lblMessage);
        txtEventLog = (EditText) findViewById(R.id.txtEventLog);
        imgIris = (ImageView) findViewById(R.id.imgIris);
        btnSyncCapture = (Button) findViewById(R.id.btnSyncCapture);
        btnStopCapture = (Button) findViewById(R.id.btnStopCapture);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        edtQuality = (EditText) findViewById(R.id.edtQuality);
        edtTimeOut = (EditText) findViewById(R.id.edtTimeOut);
        buttonContainer =  findViewById(R.id.buttonContainer);
    }

    public void onControlClicked(View v) {

        switch (v.getId()) {
            case R.id.btnInit:
                //InitScanner();
                initIrisScanner();
                break;
            case R.id.btnUninit:
                //UnInitScanner();
                unInitIrisScanner();
                break;
            case R.id.btnSyncCapture:
                /*scannerAction = ScannerAction.Capture;
                if (!isCaptureRunning) {
                    StartSyncCapture();
                }*/
                startIrisCapture();
                break;
            case R.id.btnStopCapture:
                //StopCapture();
                stopIrisCapture();
                break;
            case R.id.btnSubmit:
                setResult();
                break;
            default:
                break;
        }
    }

    private void initIrisScanner(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //String device = spDeviceName.getSelectedItem().toString();
                    DeviceInfo info = new DeviceInfo();
                    int ret = midIrisAuth.Init(DeviceModel.valueFor(deviceName), info);
                    if (ret != 0) {
                        SetTextOnUIThread("Init: " + ret + " (" + midIrisAuth.GetErrorMessage(ret) + ")");
                    } else {
//                                DeviceInfo info = midFingerAuth.getDeviceInfo();
                        lastDeviceInfo = info;
                        SetTextOnUIThread("Init Success");
                       // setDeviceInfo(info);
                    }
                } catch (Exception e) {
                    SetTextOnUIThread("Device not found");
                }
            }
        }).start();
    }

    private void unInitIrisScanner(){
        try {
            int ret = midIrisAuth.Uninit();
            if (ret == 0) {
                SetTextOnUIThread("UnInit Success");
            } else {
                SetTextOnUIThread("UnInit: " + ret + " (" + midIrisAuth.GetErrorMessage(ret) + ")");
            }
            lastDeviceInfo = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            captureThread = null;
        }
    }

    private void startIrisCapture() {
        if (midIrisAuth.IsCaptureRunning()) {
            SetTextOnUIThread("StartCapture Ret: " + MIDIrisAuthNative.CAPTURE_ALREADY_STARTED
                    + " (" + midIrisAuth.GetErrorMessage(MIDIrisAuthNative.CAPTURE_ALREADY_STARTED) + ")");
            return;
        }
        if (lastDeviceInfo == null) {
            SetTextOnUIThread("Please run device init first");
            return;
        }
        try {

            imgIris.setImageResource(R.drawable.red_button_rounded_corner);
			/*new Thread(new Runnable() {
				@Override
				public void run() {*/
            // int compressionRatio = 1;
            int ret = midIrisAuth.StartCapture(timeOut, minQuality);

            SetTextOnUIThread("StartCapture Ret: " + ret + " (" + midIrisAuth.GetErrorMessage(ret) + ")");
				/*}
			}).start();*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopIrisCapture() {
        try {
            int ret = midIrisAuth.StopCapture();
            SetTextOnUIThread("StopCapture: " + ret + " (" + midIrisAuth.GetErrorMessage(ret) + ")");
        } catch (Exception e) {
            SetTextOnUIThread("Error");
        }
    }

   /* private void InitScanner() {
        try {
            int ret = mis100.Init();
            if (ret != 0) {
                SetTextOnUIThread(mis100.GetErrorMsg(ret));
            } else {
                SetTextOnUIThread("Init success");
                String info = "Serial: " + mis100.GetDeviceInfo().SerialNo()
                        + " Make: " + mis100.GetDeviceInfo().Make()
                        + " Model: " + mis100.GetDeviceInfo().Model()
                        + "\nCertificate: " + mis100.GetCertification()
                        + "\nSDK Version: " + mis100.GetSDKVersion();
                SetLogOnUIThread(info);
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Init failed, unhandled exception",
                    Toast.LENGTH_LONG).show();
            SetTextOnUIThread("Init failed, unhandled exception");
        }
    }

    private void StartSyncCapture() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                SetTextOnUIThread("");
                isCaptureRunning = true;
                try {
                    IrisData irisData = new IrisData();
                    int quality = 40;
                    try {
                        quality = Integer.parseInt(edtQuality.getText().toString());
                    } catch (Exception e) {
                    }
                    int timeout = 20000;
                    try {
                        timeout = Integer.parseInt(edtTimeOut.getText().toString());
                    } catch (Exception e) {
                    }
                    int ret = mis100.AutoCapture(irisData, quality, timeout);
                    if (ret != 0) {
                        SetTextOnUIThread(mis100.GetErrorMsg(ret));
                    } else {
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(irisData.K7Image(), 0,
                                irisData.K7Image().length);
                        DisplayIris(bitmap);

                        String log = "\nCapture Success"
                                + "\nQuality :: " + irisData.Quality()
                                + "\nK7ImageLength :: " + irisData.K7Image().length
                                + "\nK7 Width :: " + irisData.K7ImageWidth()
                                + "\nK7 Height :: " + irisData.K7ImageHeight();
                        SetLogOnUIThread(log);
                        SetData2(irisData);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SetTextOnUIThread("Error");
                } finally {
                    isCaptureRunning = false;
                }
            }
        }).start();
    }

    private void StopCapture() {
        try {
            mis100.StopAutoCapture();
        } catch (Exception e) {
            SetTextOnUIThread("Error");
        }
    }

    private void UnInitScanner() {
        if(mis100!=null) {
            try {
                int ret = mis100.UnInit();
                if (ret != 0) {
                    SetTextOnUIThread(mis100.GetErrorMsg(ret));
                } else {
                    SetLogOnUIThread("Uninit Success");
                    SetTextOnUIThread("Uninit Success");
                }
            } catch (Exception e) {
                Log.e("UnInitScanner.EX", e.toString());
            }
        }
    }*/

   /* private void DisplayIris(final Bitmap bitmap) {
        imgIris.post(new Runnable() {
            @Override
            public void  run() {
                imgIris.setImageBitmap(bitmap);
            }
        });
    }*/

    private void WriteFile(String filename, byte[] bytes) {
        try {
            String path = Environment.getExternalStorageDirectory()
                    + "//IrisData";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            path = path + "//" + filename;
            file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(path);
            stream.write(bytes);
            stream.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /*private void ClearLog() {
        txtEventLog.post(new Runnable() {
            public void run() {
                txtEventLog.setText("", TextView.BufferType.EDITABLE);
            }
        });
    }*/

    private void SetTextOnUIThread(final String str) {

        lblMessage.post(new Runnable() {
            public void run() {
                lblMessage.setText(str);
            }
        });
    }

    private void SetLogOnUIThread(final String str) {

        txtEventLog.post(new Runnable() {
            public void run() {
                txtEventLog.append("\n" + str);
            }
        });
    }

    public void SetData2(IrisData irisData) {
        if (scannerAction.equals(ScannerAction.Capture)) {
            Enroll_Template = new byte[irisData.ISOTemplate().length];
            System.arraycopy(irisData.ISOTemplate(), 0, Enroll_Template, 0,
                    irisData.ISOTemplate().length);
        }

        WriteFile("Raw.raw", irisData.RawData());
        WriteFile("K7.bmp", irisData.K7Image());
        WriteFile("Iris.bmp", irisData.IRISImage());
        WriteFile("ISOTemplate.iso", irisData.ISOTemplate());
    }



    private void showSuccessLog() {
        SetTextOnUIThread("Init success");
        String info = "Serial: "
                + mis100.GetDeviceInfo().SerialNo() + "\nMake: "
                + mis100.GetDeviceInfo().Make() + "\nModel: "
                + mis100.GetDeviceInfo().Model() + "\nWidth: "
                + mis100.GetDeviceInfo().Width() + "\nHeight: "
                + mis100.GetDeviceInfo().Height()
                + "\nCertificate: " + mis100.GetCertification();
        SetLogOnUIThread(info);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void setResult(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("data",previewBitmap);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

 /*   @Override
    public void OnDeviceAttached(final int vid, final int pid, boolean hasPermission) {
        *//*if (!hasPermission) {
            SetTextOnUIThread("Permission denied");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (vid == 11279 && pid == 8448) {
                    InitScanner();
                }
            }
        }).start();*//*
    }

    @Override
    public void OnDeviceDetached() {
        *//*UnInitScanner();
        SetTextOnUIThread("Device removed");*//*
    }

    @Override
    public void OnHostCheckFailed(String err) {
        try {
            SetLogOnUIThread(err);
            Toast.makeText(this, err, Toast.LENGTH_LONG).show();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void OnMIS100AutoCaptureCallback(int ErrorCode, int Quality, byte[] irisImage) {
        if (ErrorCode != 0) {
            SetTextOnUIThread("Err :: " + ErrorCode + " (" + mis100.GetErrorMsg(ErrorCode) + ")");
            return;
        }
        SetTextOnUIThread("Quality :: " + Quality);
        final Bitmap bmp = BitmapFactory.decodeByteArray(irisImage, 0, irisImage.length);
        imgIris.post(new Runnable() {
            @Override
            public void run() {
                imgIris.setImageBitmap(bmp);
            }
        });
    }*/
}
