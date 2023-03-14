package com.yagnikfadadu.librarymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.yagnikfadadu.librarymanagement.ModalClass.UserModal;

import org.bson.Document;
import org.json.JSONObject;

public class EmailVerifyActivity extends AppCompatActivity {

    boolean[] allFilled;
    EditText e1;
    EditText e2;
    EditText e3;
    EditText e4;
    EditText e5;
    EditText e6;

    TextView emailTextView;
    TextView statusTextView;

    MaterialButton verifyButton;

    ImageView statusImage;

    String OTP;
    String nameString;
    String emailString;
    String phoneString;
    String enrollString;
    String spIDString;
    String branchString;
    String password;

    String connectionString = "mongodb://library:library@ac-tkj0cxa-shard-00-00.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-01.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-02.iwyetkb.mongodb.net:27017/?ssl=true&replicaSet=atlas-4hjcpc-shard-0&authSource=admin&retryWrites=true&w=majority";
    MongoClientURI uri = new MongoClientURI(connectionString);
    MongoClient mongoClient = new MongoClient(uri);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        nameString = intent.getStringExtra("name");
        emailString = intent.getStringExtra("email");
        phoneString = intent.getStringExtra("phone");
        enrollString = intent.getStringExtra("enroll");
        spIDString = intent.getStringExtra("spid");
        branchString = intent.getStringExtra("branch");
        password = intent.getStringExtra("password");
        boolean isForgotPassword = intent.getBooleanExtra("isForgotPassword",false);

        allFilled = new boolean[6];

        e1 = findViewById(R.id.code1);
        e2 = findViewById(R.id.code2);
        e3 = findViewById(R.id.code3);
        e4 = findViewById(R.id.code4);
        e5 = findViewById(R.id.code5);
        e6 = findViewById(R.id.code6);
        verifyButton = findViewById(R.id.verify_button);
        emailTextView = findViewById(R.id.verify_email);
        statusTextView = findViewById(R.id.status);
        statusImage = findViewById(R.id.status_image);


        emailTextView.setText(emailString);

        e1.addTextChangedListener(new GenericTextWatcher(0,null,e1,e2));
        e2.addTextChangedListener(new GenericTextWatcher(1,e1,e2,e3));
        e3.addTextChangedListener(new GenericTextWatcher(2,e2,e3,e4));
        e4.addTextChangedListener(new GenericTextWatcher(3,e3,e4,e5));
        e5.addTextChangedListener(new GenericTextWatcher(4,e4,e5,e6));
        e6.addTextChangedListener(new GenericTextWatcher(5,e5,e6,null));

        RequestQueue requestQueue = Volley.newRequestQueue(EmailVerifyActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://librarymanagementapi.yagnikpatel.repl.co/?email="+emailString +"&key=my-key", null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("myTag",response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myTag","Response Recived");
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OTP = e1.getText().toString()+e2.getText().toString()+e3.getText().toString()+e4.getText().toString()+e5.getText().toString()+e6.getText().toString();

                if (compareOTP(emailString,OTP)){
                    UserModal userModal = new UserModal(nameString,password,enrollString,phoneString,spIDString,emailString,branchString);
                    if (!isForgotPassword) {
                        insertUser(userModal);
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("shared",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("enroll",userModal.enrollmentNumber);
                    editor.apply();
                    String uri = "@raw/verify_otp";
                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    Drawable res = getResources().getDrawable(imageResource);
                    statusImage.setImageDrawable(res);
                    statusTextView.setText("OTP Verified Successfully");
                    verifyButton.setEnabled(false);
                    verifyButton.setText("Proceed");
                    Intent intent = new Intent(EmailVerifyActivity.this,MainActivity.class);
                    startActivity(intent);
                }else {
                    String uri = "@raw/invalid_image";
                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    Drawable res = getResources().getDrawable(imageResource);
                    statusImage.setImageDrawable(res);
                    statusTextView.setText("Invalid OTP");
                }
            }
        });


    }

    private class GenericTextWatcher implements TextWatcher {
        int pos;
        View previous;
        View current;
        View next;
        GenericTextWatcher(int pos,View previous,View current,View next){
            this.pos = pos;
            this.previous = previous;
            this.current = current;
            this.next = next;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            int currentID = current.getId();
            if (text.length()==1 && (currentID==e1.getId() || currentID==e2.getId() || currentID==e3.getId() || currentID==e4.getId() || currentID==e5.getId() || currentID==e6.getId())){
                if (next!=null)
                    next.requestFocus();
                allFilled[pos] = true;
            }
            if (text.isEmpty() && (currentID==e1.getId() || currentID==e2.getId() || currentID==e3.getId() || currentID==e4.getId() || currentID==e5.getId() || currentID==e6.getId())){
                if (previous!=null) {
                    previous.requestFocus();
                }
                allFilled[pos] = false;
            }
            boolean visibility = true;
            for (boolean en:allFilled){
                if (!en) {
                    visibility = false;
                    verifyButton.setBackgroundColor(Color.parseColor("#B2BABB"));
                    break;
                }
            }
            verifyButton.setEnabled(visibility);
            if (visibility){
                verifyButton.setBackgroundColor(Color.parseColor("#3867D6"));
            }
        }
    }

    boolean insertUser(UserModal userModal){
        try {
            MongoDatabase database = mongoClient.getDatabase("users");
            MongoCollection<Document> collection = database.getCollection("users");

            Document document = new Document("_id", userModal.enrollmentNumber)
                    .append("name", userModal.name)
                    .append("password", userModal.password)
                    .append("phoneNumber", userModal.phoneNumber)
                    .append("spID", userModal.spID)
                    .append("email", userModal.email)
                    .append("branch", userModal.branch)
                    .append("credit", userModal.credit);

            collection.insertOne(document);

            return true;
        }catch (Exception e) {
            Log.d("mongoError",e.toString());
            return false;
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

}