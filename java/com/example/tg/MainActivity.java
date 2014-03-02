package com.example.tg;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.graphics.*;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends ActionBarActivity
        implements OnSeekBarChangeListener{
    Bitmap bmp = null;
    ImageView iv = null;
    private static volatile boolean isCalculating = false;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        /*
            switch (item.getItemId()) {
            case R.id.new_game:
                newGame();
                return true;
            case R.id.help:
                showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
            }
         */
        Toast.makeText(MainActivity.this, "Sorry, not implemented yet", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateTextValues(seekBar);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (bmp != null) {
            bmp.eraseColor(Color.rgb(0, 0, 0));
            iv.setImageBitmap(bmp);
            iv.invalidate();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (!isCalculating) {
            new Thread(drawImage).start();
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
        new Thread(drawImage).start();
    }

    Runnable drawImage = new Runnable() {

        Canvas c;

        @Override
        public void run() {
            if (bmp != null) {
                isCalculating = true;
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
                        ImageView iv = (ImageView) findViewById(R.id.imageView);
                        iv.setImageBitmap(bmp);
                        iv.invalidate();
                        isCalculating = false;
                    }
                });
            }
        }
    };


}
