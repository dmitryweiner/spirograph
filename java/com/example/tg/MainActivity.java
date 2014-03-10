package com.example.tg;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.graphics.*;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.util.Log;

import java.util.Random;

public class MainActivity extends ActionBarActivity
        implements OnSeekBarChangeListener{
    Bitmap bmp = null;
    ImageView iv = null;
    static String TAG = "spirograph";
    private volatile boolean isCalculating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.imageView);

        SeekBar sbr = (SeekBar) findViewById(R.id.sbr);
        SeekBar sbR = (SeekBar) findViewById(R.id.sbR);
        SeekBar sbh = (SeekBar) findViewById(R.id.sbh);
        sbr.setOnSeekBarChangeListener(this);
        sbR.setOnSeekBarChangeListener(this);
        sbh.setOnSeekBarChangeListener(this);
        updateTextValues(sbr);
        updateTextValues(sbR);
        updateTextValues(sbh);
        Log.i(TAG, "App started");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SeekBar sbr = (SeekBar) findViewById(R.id.sbr);
        SeekBar sbR = (SeekBar) findViewById(R.id.sbR);
        SeekBar sbh = (SeekBar) findViewById(R.id.sbh);
        Random r = new Random();
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_random:
                sbr.setProgress(r.nextInt(100));
                sbR.setProgress(r.nextInt(100));
                sbh.setProgress(r.nextInt(100));
                updateTextValues(sbr);
                updateTextValues(sbR);
                updateTextValues(sbh);
                return true;
            default:
                Toast.makeText(MainActivity.this, "Sorry, not implemented yet", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateTextValues(seekBar);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "onStopTrackingTouch " + seekBar.getProgress());
        if (!isCalculating) {
            Handler handler = new Handler();
            handler.post(drawImage);
        } else {
            Log.i(TAG, "Thread is already started");
        }
    }

    public void updateTextValues(SeekBar seekBar) {
        String textValue = String.valueOf(seekBar.getProgress());
        switch (seekBar.getId()){
            case R.id.sbr:
                TextView tvr = (TextView) findViewById(R.id.tvr);
                tvr.setText(textValue);
                break;
            case R.id.sbR:
                TextView tvR = (TextView) findViewById(R.id.tvR);
                tvR.setText(textValue);
                break;
            case R.id.sbh:
                TextView tvh = (TextView) findViewById(R.id.tvh);
                tvh.setText(textValue);
                break;
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        bmp = Bitmap.createBitmap( iv.getWidth(), iv.getHeight(), Bitmap.Config.ARGB_8888);
        Handler handler = new Handler();
        handler.post(drawImage);
    }

    Runnable drawImage = new Runnable() {

        Canvas c;

        @Override
        public void run() {
            Log.i(TAG, "Thread started");
            isCalculating = true;
            if (bmp != null) {

                bmp.eraseColor(Color.rgb(0, 0, 0));
                c = new Canvas(bmp);

                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.rgb(0, 255, 0));
                float x, y, phi = 0;
                SeekBar sbr = (SeekBar) findViewById(R.id.sbr);
                float r = (float) sbr.getProgress();
                SeekBar sbR = (SeekBar) findViewById(R.id.sbR);
                float Rbig = (float) sbR.getProgress();
                SeekBar sbh = (SeekBar) findViewById(R.id.sbh);
                float h = (float) sbh.getProgress();

                float x0 = 0, y0 = 0;
                while (phi <= 1000) {
                    x = (Rbig - r) * (float) Math.cos(phi) + h * (float) Math.cos((Rbig - r) / r * phi);
                    y = (Rbig - r) * (float) Math.sin(phi) - h *  (float) Math.sin((Rbig - r) / r * phi);
                    if (phi == 0) {
                        x0 = x;
                        y0 = y;
                    } else {
                        if (x == x0 && y == y0) {
                            break;
                        }
                    }
                    float minDimension = Math.min(bmp.getWidth(), bmp.getHeight());
                    float i = bmp.getWidth()/2 + (minDimension / 2 / (Math.abs(Rbig - r) + h)) * x * 0.9f;
                    float j = bmp.getHeight()/2 - (minDimension / 2 / (Math.abs(Rbig - r) + h)) * y * 0.9f;
                    c.drawRect(i, j, i + 1, j + 1, paint);
                    phi += 0.05;
                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(TAG, "runOnUiThread begin");
                        ImageView iv = (ImageView) findViewById(R.id.imageView);
                        iv.setImageBitmap(bmp);
                        iv.invalidate();
                        Log.i(TAG, "runOnUiThread end");
                    }
                });
            }
            isCalculating = false;
            Log.i(TAG, "Thread finished");
        }
    };


}
