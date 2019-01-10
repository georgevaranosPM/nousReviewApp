package com.nousanimation.nousreview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.OpenGLRenderer;
import fr.arnaudguyon.smartgl.opengl.RenderPassObject3D;
import fr.arnaudguyon.smartgl.opengl.SmartGLRenderer;
import fr.arnaudguyon.smartgl.opengl.SmartGLView;
import fr.arnaudguyon.smartgl.opengl.SmartGLViewController;
import fr.arnaudguyon.smartgl.opengl.Texture;
import fr.arnaudguyon.smartgl.tools.WavefrontModel;
import fr.arnaudguyon.smartgl.touch.TouchHelperEvent;

import static fr.arnaudguyon.smartgl.touch.TouchHelperEvent.TouchEventType.SINGLEMOVE;


public class ModelViewActivity extends AppCompatActivity implements SmartGLViewController {

    private static final int REQUEST_CODE_WRITE_EXT_STORAGE = 1;
    private static final float ROT_FACTOR = 10;
    private static final float SCALE_FACTOR = 200.0f;
    private static boolean WRITE_EXT_STORAGE_GRANTED = false;

    //Metavlites gia to 3d model
    private SmartGLView mSmartGLView;
    private Object3D currentObj;
    private Texture objTexture;
    private float mPreviousX;
    private float mPreviousY;
    private int previousProcess = 50;

    private int the_path = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_view);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent intentFromList = getIntent();
        the_path = intentFromList.getExtras().getInt("path_for_file");

        final ToggleButton sketch = findViewById(R.id.sketch_button);
        final ToggleButton view = findViewById(R.id.view_button);
        final ImageButton tick = findViewById(R.id.tick_button);
        final ImageButton cancel = findViewById(R.id.cancel_button);
        final SimpleDrawingView drawing = findViewById(R.id.drawingHere);
        final SeekBar scaleSeekBar = findViewById(R.id.scaleBar);
        drawing.setVisibility(View.INVISIBLE);

        mSmartGLView = findViewById(R.id.smartGLView);
        mSmartGLView.setDefaultRenderer(this);
        mSmartGLView.setController(this);

        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.d("", "HAS Write Storage Permissions " + hasWriteStoragePermission);

        if (hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED) {
            Log.d("", "Permission Granted");
            WRITE_EXT_STORAGE_GRANTED = true;
        } else {
            Log.d("", "Requesting permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXT_STORAGE);
        }

        sketch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tick.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                tick.setClickable(true);
                cancel.setClickable(true);
                view.setChecked(false);
                sketch.setChecked(true);
                drawing.setVisibility(View.VISIBLE);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tick.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);
                tick.setClickable(false);
                cancel.setClickable(false);
                view.setChecked(true);
                sketch.setChecked(false);
                drawing.deletePath();
                drawing.setVisibility(View.INVISIBLE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tick.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);
                tick.setClickable(false);
                cancel.setClickable(false);
                view.setChecked(true);
                sketch.setChecked(false);
                drawing.deletePath();
                drawing.setVisibility(View.INVISIBLE);
            }
        });

        tick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (WRITE_EXT_STORAGE_GRANTED) {

                    mSmartGLView.getSmartGLRenderer().takeScreenshot(new OpenGLRenderer.OnTakeScreenshot() {
                        @Override
                        public void screenshotTaken(Bitmap bitmap) {
                            String path = saveBitmap(overlay(bitmap, getRootBitmap()));
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setType("image/png");
                            Uri myUri = Uri.parse("file://" + path);
                            emailIntent.putExtra(Intent.EXTRA_STREAM, myUri);
                            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        }
                    });
                }
            }
        });

        scaleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean b) {
                int diff = progressValue - previousProcess;
                Log.d("HEY", "onProgressChanged: " + diff);
                Log.d("SCALE", "onProgressChanged: " + currentObj.getScaleX());
                currentObj.setScale(currentObj.getScaleX()+(diff/SCALE_FACTOR), currentObj.getScaleY()+(diff/SCALE_FACTOR), currentObj.getScaleZ()+(diff/SCALE_FACTOR));
                previousProcess = progressValue;
                //mSmartGLView.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onPrepareView(SmartGLView smartGLView) {
        SmartGLRenderer renderer = mSmartGLView.getSmartGLRenderer();
        RenderPassObject3D renderPassObject3D = new RenderPassObject3D();
        renderer.addRenderPass(renderPassObject3D);  // add it only once for all 3D Objects
        Context context = smartGLView.getContext();

        objTexture = new Texture(context, R.drawable.texture);

        WavefrontModel model = new WavefrontModel.Builder(context, the_path)
                .addTexture("", objTexture) // "Material001" is defined in the spaceship_obj file
                .create();
        currentObj = model.toObject3D();
        currentObj.setScale(0.02f, 0.02f, 0.02f);  // Adjust the scale if object is too big / too small
        currentObj.setPos(0, 0, -4);   // move the object in front of the camera
        renderPassObject3D.addObject(currentObj);
    }

    @Override
    public void onReleaseView(SmartGLView smartGLView) {
        if (objTexture != null) {
            objTexture.release();
            objTexture = null;
        }
    }

    @Override
    public void onResizeView(SmartGLView smartGLView) {

    }

    @Override
    public void onTick(SmartGLView smartGLView) {

    }

    @Override
    public void onTouchEvent(SmartGLView smartGLView, TouchHelperEvent touchHelperEvent) {

        float x = touchHelperEvent.getX(0);
        float y = touchHelperEvent.getY(0);

        if (touchHelperEvent.getType().equals(SINGLEMOVE)) {
            currentObj.addRotY((x-mPreviousX)/ROT_FACTOR);
            currentObj.addRotX((y-mPreviousY)/ROT_FACTOR);
            smartGLView.requestRender();
            Log.d("KOUNIETAI", "onTouchEvent: " + x + "|" + y);
        }

        mPreviousX = x;
        mPreviousY = y;

    }

    @Override
    protected void onPause() {
        if (mSmartGLView != null) {
            mSmartGLView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSmartGLView != null) {
            mSmartGLView.onResume();
        }
    }

    public String saveBitmap(Bitmap bitmap) {
        String filePath = Environment.getExternalStorageDirectory().toString()
                + File.separator + "Pictures/screenshot.png";
        File image = new File(filePath);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            //sendMail(filePath);
        } catch (FileNotFoundException e) {
            Log.e("File Not Found", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("IO Exception", e.getMessage(), e);
        }
        return filePath;
    }

    public Bitmap getRootBitmap() {
        View v1 = getWindow().getCurrentFocus();
        v1.setDrawingCacheEnabled(true);
        Bitmap returnedBitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        Bitmap resizedBitmap = Bitmap.createBitmap(returnedBitmap, 0, 0, returnedBitmap.getWidth(), returnedBitmap.getHeight());
        return resizedBitmap;
    }

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXT_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permissions", "Granted!");
                    WRITE_EXT_STORAGE_GRANTED = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Permissions", "Refused!");

                }
            }
        }
    }
}

