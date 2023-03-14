package com.yagnikfadadu.librarymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

public class ForgetPasswordActivity extends AppCompatActivity {

    TextInputEditText enroll;
    TextInputEditText password;
    TextInputEditText confirmPassword;
    TextInputEditText otp;
    MaterialButton getOTPButton;
    MaterialButton resetButton;
    LinearLayout linearLayout;
    String emailString;
    String enrollString;

    String connectionString = "mongodb://library:library@ac-tkj0cxa-shard-00-00.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-01.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-02.iwyetkb.mongodb.net:27017/?ssl=true&replicaSet=atlas-4hjcpc-shard-0&authSource=admin&retryWrites=true&w=majority";
    MongoClientURI uri = new MongoClientURI(connectionString);
    MongoClient mongoClient = new MongoClient(uri);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        getSupportActionBar().hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Intent intent = getIntent();
        String e = intent.getStringExtra("enroll");

        enroll = findViewById(R.id.forget_enroll);
        password = findViewById(R.id.forget_password);
        confirmPassword = findViewById(R.id.forget_confirm_password);
        getOTPButton = findViewById(R.id.forget_get_otp);
        linearLayout = findViewById(R.id.linear_layout);
        otp = findViewById(R.id.forget_user_otp);
        resetButton = findViewById(R.id.forget_reset);

        if (!e.isEmpty()){
            enroll.setText(e);
        }

        getOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 enrollString = enroll.getText().toString();
                if (!enrollString.isEmpty()){
                emailString = fetchUserWithParameter(enrollString,"email");
                    if (emailString!="none"){
                        RequestQueue requestQueue = Volley.newRequestQueue(ForgetPasswordActivity.this);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://librarymanagementapi.yagnikpatel.repl.co/?email="+emailString +"&key=my-key", null,new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });

                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        requestQueue.add(jsonObjectRequest);

                        getOTPButton.setEnabled(false);
                        getOTPButton.setText("Resend OTP");
                        getOTPButton.setBackgroundColor(Color.parseColor("#424242"));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getOTPButton.setEnabled(true);
                                getOTPButton.setBackgroundColor(Color.parseColor("#3867D6"));
                            }
                        },20000);

                        linearLayout.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        linearLayout.requestLayout();

                }else {
                    enroll.setError("No Account found");
                }
            }else{
                    enroll.setError("Enrollment Can't be empty");
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals(confirmPassword.getText().toString())){
                    if (compareOTP(emailString,otp.getText().toString())){
                        updatePassword(enrollString,MD5Generator.getMD5(password.getText().toString()));
                        Toast.makeText(ForgetPasswordActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("shared",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("enroll",enrollString);
                        editor.apply();
                        Intent intent = new Intent(ForgetPasswordActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(ForgetPasswordActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ForgetPasswordActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    boolean updatePassword(String enroll,String password){
        MongoDatabase database = mongoClient.getDatabase("users");
        MongoCollection<Document> collection = database.getCollection("users");

        try {
            Bson filter = Filters.eq("_id",enrollString);
            Bson update = Updates.set("password", password);
            UpdateResult result = collection.updateOne(filter, update);

            return true;
        }catch (Exception e){
            Log.d("myTag",""+e.toString());
            return false;
        }
    }

    String fetchUserWithParameter(String enrollmentNumber, String param){
        try {
            MongoDatabase database = mongoClient.getDatabase("users");
            MongoCollection<Document> collection = database.getCollection("users");
            Document doc = collection.find(Filters.eq("_id", enrollmentNumber)).first();

            String value;
            if (doc != null) {
                value = doc.getString(param);
            } else {
                value = "none";
            }

            return value;
        }catch (Exception e){
            return "null";
        }
    }

    boolean compareOTP(String email,String otp){

        String connectionString = "mongodb://library:library_otp@ac-mskny2c-shard-00-00.mj5rfnf.mongodb.net:27017,ac-mskny2c-shard-00-01.mj5rfnf.mongodb.net:27017,ac-mskny2c-shard-00-02.mj5rfnf.mongodb.net:27017/?ssl=true&replicaSet=atlas-pfqm0g-shard-0&authSource=admin&retryWrites=true&w=majority";
        MongoClientURI uri = new MongoClientURI(connectionString);
        MongoClient mongoClient = new MongoClient(uri);

        MongoDatabase database = mongoClient.getDatabase("otp");
        MongoCollection<Document> collection = database.getCollection("otp");

        Document doc = collection.find(Filters.eq("_id", email)).first();

        if (doc!=null){
            String authCode = doc.getString("authenticationCode");
            if (MD5Generator.getMD5(otp).equals(authCode)){
                return true;
            }else {
                return false;
            }
        }else{
            Log.d("logger","Un-Success");
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}