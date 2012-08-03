package it.sasabz.android.sasabus;
/*
 * Basic no frills app which integrates the ZBar barcode scanner with
 * the camera.
 * 
 * Created by lisah0 on 2012-02-24
 */


import java.util.Iterator;

import it.sasabz.android.sasabus.classes.CameraPreview;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.classes.PalinaList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Button;
import android.widget.Toast;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;

import android.widget.TextView;
import android.graphics.ImageFormat;

/* Import ZBar Class files */
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

public class ScanCodeActivity extends Activity
{
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;


    ImageScanner scanner;

    private boolean previewing = true;
    
    private boolean barcodeScanned = false;

    static {
        System.loadLibrary("iconv");
    } 

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.standard_imagescan_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
            public void run() {
                if (previewing)
                    mCamera.autoFocus(autoFocusCB);
            }
        };

    private Context getContext()
    {
    	return this.getApplicationContext();
    }
        
        
    private PreviewCallback previewCb = new PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Parameters parameters = camera.getParameters();
                Size size = parameters.getPreviewSize();

                Image barcode = new Image(size.width, size.height, "Y800");
                barcode.setData(data);

                int result = scanner.scanImage(barcode);
                
                if (result != 0) {
                    previewing = false;
                    mCamera.setPreviewCallback(null);
                    mCamera.stopPreview();
                    
                    SymbolSet syms = scanner.getResults();
                    
                    Iterator<Symbol> symbol = syms.iterator();
                    String last_data = "";
                    while(symbol.hasNext())
                    {
                    	barcodeScanned = true;
                    	Symbol item = symbol.next();
                    	last_data = item.getData();
                    	Log.v("QRCODE", "DATA READ: " + last_data);
                    }
                    if(last_data.equals(""))
                    {
                    	Toast.makeText(getContext(), R.string.error_scan_text, Toast.LENGTH_LONG).show();
                    	previewing = true;
                        mCamera.setPreviewCallback(previewCb);
                        mCamera.startPreview();
                        barcodeScanned = false;
                    	
                    }
                    else if(last_data.indexOf("busstop") != -1 || last_data.indexOf("BUSSTOP") != -1)
                    {
                    	String busstopnr = last_data.substring(8);
                    	Palina partenza = PalinaList.getById(Integer.parseInt(busstopnr));
                    	if(partenza != null)
                    	{
                    		finish();
                    		Intent selDest = new Intent(getContext(), SelectDestinazioneLocationActivity.class);
                    		selDest.putExtra("partenza", partenza.getName_de());
                    		startActivity(selDest);
                    	}
                    	else
                    	{
                    		previewing = true;
	                        mCamera.setPreviewCallback(previewCb);
	                        mCamera.startPreview();
	                        barcodeScanned = false;
                    	}
                    }
                    else
                    {
                    	Toast.makeText(getContext(), R.string.error_scan_text, Toast.LENGTH_LONG).show();
                    	previewing = true;
                        mCamera.setPreviewCallback(previewCb);
                        mCamera.startPreview();
                        barcodeScanned = false;
                    }
                   
                }
            }
        };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                autoFocusHandler.postDelayed(doAutoFocus, 1000);
            }
        };
}