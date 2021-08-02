package com.example.parkomico.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.parkomico.R;

import java.util.Objects;

public class AuthOptionsActivity extends AppCompatActivity {

    public void signInOnClick(View view)
    {
        startActivity(new Intent(AuthOptionsActivity.this, SignInActivity.class));

        overridePendingTransition(R.anim.animstart, R.anim.animend);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_options);

        Objects.requireNonNull(getSupportActionBar()).hide();

    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.animstart, R.anim.animend);

    }
}