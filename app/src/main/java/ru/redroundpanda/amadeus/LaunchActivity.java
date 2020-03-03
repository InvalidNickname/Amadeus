package ru.redroundpanda.amadeus;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class LaunchActivity extends AppCompatActivity {

    private final Handler aniHandle = new Handler();
    private ImageView connect, cancel, logo;
    private Boolean isPressed = false;
    private MediaPlayer m;
    private int i = 0;
    final Runnable aniRunnable = new Runnable() {
        public void run() {
            if (i < 39) {
                i++;
                int id = getResources().getIdentifier("logo" + i, "drawable", getPackageName());
                logo.setImageDrawable(getDrawable(id));
                aniHandle.postDelayed(this, 20);
            }
        }
    };

    private boolean isGQSBInstalled() {
        try {
            getApplicationContext().getPackageManager().getApplicationInfo("com.google.android.googlequicksearchbox", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        connect = findViewById(R.id.connect);
        cancel = findViewById(R.id.cancel);
        logo = findViewById(R.id.logo);

        aniHandle.post(aniRunnable);

        connect.setOnClickListener(view -> {
            if (!isPressed && isGQSBInstalled()) {
                isPressed = true;

                connect.setImageResource(R.drawable.connect_selected);

                m = MediaPlayer.create(LaunchActivity.this, R.raw.tone);

                m.setOnPreparedListener(MediaPlayer::start);

                m.setOnCompletionListener(mp -> {
                    mp.release();
                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                    startActivity(intent);
                });
            }
        });

        cancel.setOnClickListener(view -> {
            cancel.setImageResource(R.drawable.cancel_selected);
            onBackPressed();
        });

        logo.setOnClickListener(view -> {
            Intent settingIntent = new Intent(LaunchActivity.this, SettingsActivity.class);
            startActivity(settingIntent);
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LangContext.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (m != null) {
            m.release();
        }

        aniHandle.removeCallbacks(aniRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isPressed = false;
        connect.setImageResource(R.drawable.connect_not_selected);
        cancel.setImageResource(R.drawable.cancel_not_selected);
    }
}