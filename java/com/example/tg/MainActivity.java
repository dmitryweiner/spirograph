package com.example.tg;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.graphics.*;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.util.*;

public class MainActivity extends ActionBarActivity
        implements OnSeekBarChangeListener{
    Bitmap bmp = null;
    ImageView iv = null;
    String tag = "TG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(tag, "Start!");
        iv = (ImageView) findViewById(R.id.imageView);
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(tag, "Click");
                new Thread(drawImage).start();
            }
        });

        SeekBar sbr = (SeekBar) findViewById(R.id.sbr);
        SeekBar sbR = (SeekBar) findViewById(R.id.sbR);
        SeekBar sbh = (SeekBar) findViewById(R.id.sbh);
        sbr.setOnSeekBarChangeListener(this);
        sbR.setOnSeekBarChangeListener(this);
        sbh.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        bmp = Bitmap.createBitmap( iv.getWidth(), iv.getHeight(), Bitmap.Config.ARGB_8888);
    }

    Runnable drawImage = new Runnable() {

        Canvas c;

        @Override
        public void run() {
            if (bmp != null) {
                bmp.eraseColor(Color.rgb(0, 0, 0));
                Log.i(tag, "Run");
                c = new Canvas(bmp);

                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.rgb(0, 255, 0));
                //bmp.setPixel(r.nextInt(bmp.getWidth()), r.nextInt(bmp.getWidth()),  Color.argb(255, r.nextInt(255), r.nextInt(255), r.nextInt(255)));
                float x, y, phi = 0;
                SeekBar sbr = (SeekBar) findViewById(R.id.sbr);
                float r = (float) sbr.getProgress();
                SeekBar sbR = (SeekBar) findViewById(R.id.sbR);
                float Rbig = (float) sbR.getProgress();
                SeekBar sbh = (SeekBar) findViewById(R.id.sbh);
                float h = (float) sbh.getProgress();
                Log.i(tag, "r = " + r + " R = " + Rbig + " h = " + h);

                while (phi <= 1000) {
                    x = (Rbig - r) * (float) Math.cos(phi) + h * (float) Math.cos((Rbig - r) / r * phi);
                    y = (Rbig - r) * (float) Math.sin(phi) - h *  (float) Math.sin((Rbig - r) / r * phi);
                    c.drawRect(bmp.getWidth()/2 + x, bmp.getHeight()/2 - y, bmp.getWidth()/2 + x + 1, bmp.getHeight()/2 - y + 1, paint);
                    phi += 0.01;
                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(tag, "runOnUiThread");
                        ImageView iv = (ImageView) findViewById(R.id.imageView);
                        iv.setImageBitmap(bmp);
                        iv.invalidate();
                    }
                });
            }
        }
    };


}
