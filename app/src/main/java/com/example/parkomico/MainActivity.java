package com.example.parkomico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.example.parkomico.Auth.AuthOptionsActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        final VideoView themeVideoView=findViewById(R.id.themeVideoView);

        themeVideoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.theme_video));

        themeVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                themeVideoView.start();
            }
        });

        themeVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startActivity(new Intent(MainActivity.this, AuthOptionsActivity.class));

                finish();

                overridePendingTransition(R.anim.animstart, R.anim.animend);
            }
        });

    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.animstart, R.anim.animend);

    }
}