package com.example.parkomico.Auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.parkomico.R;
import com.example.parkomico.Utilities;

import java.util.Objects;
import java.util.Random;

public class SignInActivity extends AppCompatActivity implements TextWatcher {

    TextView termsAndPoliciesTextView;
    EditText signInPhoneNumberEditText;

    public void backOnClick(View view)
    {
        finish();
    }

    public void doneOnClick(View view)
    {
        if(signInPhoneNumberEditText.getText().toString().length()==10) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignInActivity.this);

            alertDialog.setTitle("Confirm").setMessage("We will send a verification code to \n" +
                    "+" + "91" + " " + signInPhoneNumberEditText.getText().toString());

            alertDialog.setPositiveButton("SEND OTP", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SendOTP sendOTP=new SendOTP(SignInActivity.this);
                    sendOTP.doInBackground(createOTP(), signInPhoneNumberEditText.getText().toString().trim());

                }
            });

            alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        } else
        {
            Toast.makeText(SignInActivity.this, "Please enter valid phone number", Toast.LENGTH_SHORT).show();
        }
    }

    public String createOTP()
    {
        Random random=new Random();

        String otp="";

        for(int i=0;i<6;i++)
        {
            otp+=String.valueOf(random.nextInt(10));
        }

        return otp;

    }

    public void initialise()
    {
        termsAndPoliciesTextView=findViewById(R.id.termsAndPoliciesTextView);
        signInPhoneNumberEditText=findViewById(R.id.signInPhoneNumberEditText);

        signInPhoneNumberEditText.addTextChangedListener(this);

        SpannableString spannableString=new SpannableString(getResources().getString(R.string.terms_policy));
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 59, 64, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 84, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_INCLUSIVE);
        termsAndPoliciesTextView.setText(spannableString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Objects.requireNonNull(getSupportActionBar()).hide();

        initialise();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if(s.toString().length()==10)
        {
            signInPhoneNumberEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(SignInActivity.this, R.drawable.check_drawable), null);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                TextViewCompat.setCompoundDrawableTintList(signInPhoneNumberEditText, ColorStateList.valueOf(Color.GREEN));
            }
        } else
        {
            signInPhoneNumberEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.animstart, R.anim.animend);

    }

    static class SendOTP extends AsyncTask<String, Void, Void>
    {

        Context context;
        ProgressDialog progressDialog;

        public SendOTP(Context context)
        {
            this.context=context;
            progressDialog=new ProgressDialog(context);
        }

        @Override
        protected Void doInBackground(final String... strings) {

            progressDialog.setTitle("Sending OTP");

            progressDialog.setCancelable(false);

            progressDialog.show();

            String otpURL="http://66.70.200.49/rest/services/sendSMS/sendGroupSms?AUTH_KEY=3e377f8fcc852e1ceb8262ea7d82913&message="+strings[0]+"&senderId=FILLIP&routeId=1&mobileNos="+strings[1]+"&smsContentType=unicode";

            StringRequest request=new StringRequest(StringRequest.Method.GET, otpURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();

                    context.startActivity(new Intent(context, OTPActivity.class).putExtra("OTP", strings[0])
                            .putExtra("PhoneNumber", "91"+" "+strings[1]));

                    ((Activity) context).overridePendingTransition(R.anim.animstart, R.anim.animend);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(context, String.valueOf(error), Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();

                }
            });

            RetryPolicy policy=new DefaultRetryPolicy(Utilities.maxTimeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(policy);

            RequestQueue queue= Volley.newRequestQueue(context);
            queue.add(request);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(progressDialog!=null && progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }

        }
    }

}