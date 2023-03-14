package com.yagnikfadadu.librarymanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;

import java.util.Objects;

public class FirstActivity extends AppCompatActivity {

    MaterialButton loginButton;
    MaterialButton createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);

        getSupportActionBar().hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences sharedPreferences = getSharedPreferences("shared",MODE_PRIVATE);
        String enroll = sharedPreferences.getString("enroll","null");

        if (!Objects.equals(enroll, "null")){
            Intent intent = new Intent(FirstActivity.this,MainActivity.class);
            startActivity(intent);
        }

        loginButton = findViewById(R.id.button_login);
        createAccountButton = findViewById(R.id.button_create_account);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FirstActivity.this);
                bottomSheetDialog.setContentView(R.layout.login_bottom_sheet);

                ImageView imageView = bottomSheetDialog.findViewById(R.id.dialog_back);
                TextInputEditText enrollment = bottomSheetDialog.findViewById(R.id.login_enroll);
                TextInputEditText password = bottomSheetDialog.findViewById(R.id.login_password);
                MaterialTextView forgotPassword = bottomSheetDialog.findViewById(R.id.login_forgot_password);
                MaterialButton login = bottomSheetDialog.findViewById(R.id.login_button);

                assert login != null;
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assert enrollment != null;
                        assert password != null;
                        if (!(enrollment.getText().toString().isEmpty() && password.getText().toString().isEmpty())){
                            String stringPassword = fetchUserWithParameter(enrollment.getText().toString(),"password");
                            String md5Value = MD5Generator.getMD5(password.getText().toString());
                            if (stringPassword.equals(md5Value)){
                                SharedPreferences sharedPreferences = getSharedPreferences("shared",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("enroll",enrollment.getText().toString());
                                editor.apply();
                                Intent intent = new Intent(FirstActivity.this,MainActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(FirstActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                assert forgotPassword != null;
                forgotPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FirstActivity.this,ForgetPasswordActivity.class);
                        intent.putExtra("enroll",enrollment.getText().toString());
                        startActivity(intent);
                    }
                });

                assert imageView != null;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FirstActivity.this);
                bottomSheetDialog.setContentView(R.layout.signup_bottom_dialog);

                ImageView imageView = bottomSheetDialog.findViewById(R.id.dialog_back);
                TextInputEditText name = bottomSheetDialog.findViewById(R.id.signup_name);
                TextInputEditText email = bottomSheetDialog.findViewById(R.id.signup_email);
                TextInputEditText phone = bottomSheetDialog.findViewById(R.id.signup_phone);
                TextInputEditText enroll = bottomSheetDialog.findViewById(R.id.signup_enroll);
                TextInputEditText spID = bottomSheetDialog.findViewById(R.id.signup_spid);
                TextInputEditText branch = bottomSheetDialog.findViewById(R.id.signup_branch);
                TextInputEditText password = bottomSheetDialog.findViewById(R.id.signup_password);
                TextInputEditText confirmPassword = bottomSheetDialog.findViewById(R.id.signup_confirm_password);
                MaterialButton createAccount = bottomSheetDialog.findViewById(R.id.signup_button);

                createAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (password.getText().toString().equals(confirmPassword.getText().toString())) {

                            Intent intent = new Intent(FirstActivity.this,EmailVerifyActivity.class);
                            intent.putExtra("email",email.getText().toString());
                            intent.putExtra("name",name.getText().toString());
                            intent.putExtra("phone",phone.getText().toString());
                            intent.putExtra("enroll",enroll.getText().toString());
                            intent.putExtra("spid",spID.getText().toString());
                            intent.putExtra("branch",branch.getText().toString());
                            intent.putExtra("password", MD5Generator.getMD5(password.getText().toString()));
                            startActivity(intent);

                        }else {
                            Toast.makeText(FirstActivity.this, "Password Doesn't Match", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                assert imageView != null;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });


                bottomSheetDialog.show();
            }
        });
    }

    String fetchUserWithParameter(String enrollmentNumber, String param){
        try {
            String connectionString = "mongodb://library:library_admin@ac-bzixxjg-shard-00-00.56pdawn.mongodb.net:27017,ac-bzixxjg-shard-00-01.56pdawn.mongodb.net:27017,ac-bzixxjg-shard-00-02.56pdawn.mongodb.net:27017/?ssl=true&replicaSet=atlas-tyk0hf-shard-0&authSource=admin&retryWrites=true&w=majority";
            MongoClientURI uri = new MongoClientURI(connectionString);
            MongoClient mongoClient = new MongoClient(uri);
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
            System.out.println(e);
            return "null";
        }
    }

}