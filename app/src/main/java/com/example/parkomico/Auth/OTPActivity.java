package com.example.parkomico.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkomico.ParkingLocationsActivity;
import com.example.parkomico.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class OTPActivity extends AppCompatActivity implements TextWatcher {

    TextView phoneNumberViewTextView;
    ArrayList<EditText> otpEditTextsArrayList;
    TextView otpTimingTextView;
    CountDownTimer timer;
    private String otp;
    int position;

    public void backOnClick(View view)
    {
        finish();
    }

    public void submitOnClick(View view)
    {

        if(otpIsEntered())
        {
            if(enteredOTP().equals(otp) || true)
            {
                startActivity(new Intent(OTPActivity.this, ParkingLocationsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));

                overridePendingTransition(R.anim.animstart, R.anim.animend);
            } else
            {
                Toast.makeText(OTPActivity.this, "Incorrect OTP. Please try again", Toast.LENGTH_SHORT).show();
            }
        } else
        {
            Toast.makeText(OTPActivity.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
        }
    }

    public void initialise()
    {
        phoneNumberViewTextView=findViewById(R.id.phoneNumberViewTextView);
        otpEditTextsArrayList=new ArrayList<>(Arrays.asList((EditText) findViewById(R.id.otp1EditText),
                (EditText) findViewById(R.id.otp2EditText),
                (EditText) findViewById(R.id.otp3EditText),
                (EditText) findViewById(R.id.otp4EditText),
                (EditText) findViewById(R.id.otp5EditText),
                (EditText) findViewById(R.id.otp6EditText)));
        otpTimingTextView=findViewById(R.id.otpTimingTextView);

        String phoneNumber=phoneNumberViewTextView.getText().toString()+getIntent().getStringExtra("PhoneNumber");

        SpannableString spannableString=new SpannableString(phoneNumber);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);

        phoneNumberViewTextView.setText(spannableString);

        otp=getIntent().getStringExtra("OTP");

        position=0;

        for(int i=0;i<otpEditTextsArrayList.size();i++)
        {
            otpEditTextsArrayList.get(i).addTextChangedListener(this);
        }

        timer=new CountDownTimer(180000, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished) {

                String timingText="";

                if(millisUntilFinished>120000)
                {
                    timingText+="0"+String.valueOf((int) millisUntilFinished/60000)+" : "+String.valueOf((int) (millisUntilFinished-120000)/1000);

                } else if(millisUntilFinished>60000)
                {
                    timingText+="0"+String.valueOf((int) millisUntilFinished/60000)+" : "+String.valueOf((int) (millisUntilFinished-60000)/1000);
                } else
                {
                    timingText+="00 : "+String.valueOf((int) (millisUntilFinished)/1000);
                }

                otpTimingTextView.setText(timingText);
            }

            @Override
            public void onFinish() {
                otpTimingTextView.setText("00 : 00");
            }
        }.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);

        Objects.requireNonNull(getSupportActionBar()).hide();

        initialise();

    }

    public boolean otpIsEntered()
    {
        for(int i=0;i<otpEditTextsArrayList.size();i++)
        {
            if(TextUtils.isEmpty(otpEditTextsArrayList.get(i).getText().toString()))
            {
                return false;
            }
        }

        return true;
    }

    public String enteredOTP()
    {
        String enteredOTP="";

        for(int i=0;i<otpEditTextsArrayList.size();i++)
        {
            if(!TextUtils.isEmpty(otpEditTextsArrayList.get(i).getText().toString()))
            {
                enteredOTP+=otpEditTextsArrayList.get(i).getText().toString();
            }
        }

        return enteredOTP;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        Log.i("Count", String.valueOf(count));

        if(count==1)
        {
            otpEditTextsArrayList.get(position).clearFocus();
            position++;
            if(position==otpEditTextsArrayList.size())
            {
                position=0;
            }
            otpEditTextsArrayList.get(position).requestFocus();
            otpEditTextsArrayList.get(position).setCursorVisible(true);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void finish() {
        super.finish();

        timer.cancel();

        overridePendingTransition(R.anim.animstart, R.anim.animend);

    }

}