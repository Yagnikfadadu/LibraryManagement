package com.yagnikfadadu.librarymanagement.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.yagnikfadadu.librarymanagement.ModalClass.UserModal;
import com.yagnikfadadu.librarymanagement.R;

import org.bson.Document;


public class ProfileFragment extends Fragment {
    String connectionString = "mongodb://library:library@ac-tkj0cxa-shard-00-00.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-01.iwyetkb.mongodb.net:27017,ac-tkj0cxa-shard-00-02.iwyetkb.mongodb.net:27017/?ssl=true&replicaSet=atlas-4hjcpc-shard-0&authSource=admin&retryWrites=true&w=majority";
    MongoClientURI uri = new MongoClientURI(connectionString);
    MongoClient mongoClient = new MongoClient(uri);
    MongoDatabase usersDatabase = mongoClient.getDatabase("users");
    MongoCollection<Document> usersCollection = usersDatabase.getCollection("users");
    TextView profileName;
    TextView profileEnroll;
    TextView profileSpid;
    TextView profileBranch;
    TextView profileCredits;
    TextView profilePhone;
    TextView profileEmail;


    public ProfileFragment() {
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        profileName = view.findViewById(R.id.profile_name);
        profileEnroll = view.findViewById(R.id.profile_enroll);
        profileSpid = view.findViewById(R.id.profile_spid);
        profilePhone = view.findViewById(R.id.profile_phone);
        profileEmail = view.findViewById(R.id.profile_email);
        profileCredits = view.findViewById(R.id.profile_credits);
        profileBranch = view.findViewById(R.id.profile_branch);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared", MODE_PRIVATE);
        String enroll = sharedPreferences.getString("enroll", "");

        Document document = usersCollection.find(Filters.eq("_id", enroll)).first();
        UserModal userModal = new UserModal();
        userModal.setEnrollmentNumber(document.getString("_id"));
        userModal.setName(document.getString("name"));
        userModal.setPhoneNumber(document.getString("phoneNumber"));
        userModal.setSpID(document.getString("spID"));
        userModal.setEmail(document.getString("email"));
        userModal.setCredit(document.getInteger("credit"));
        userModal.setBranch(document.getString("branch"));

        profileName.setText(""+userModal.name);
        profileEnroll.setText("Enrollment No: "+userModal.enrollmentNumber);
        profileEmail.setText("Email : "+userModal.email);
        profileSpid.setText("SPID : "+userModal.spID);
        profileBranch.setText("Branch : "+userModal.branch);
        profileCredits.setText("Credits : "+userModal.credit);
        profilePhone.setText("Phone no: "+userModal.phoneNumber);

        return view;

    }
}