package com.example.myapplication5;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;


public class MyData {

    private static MyData instence;
    private static String TAG = "MyData";
    private final int MIN_HOUR = 8;
    private final int MAX_HOUR = 21;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference usersRef = database.getReference("users");
    DatabaseReference reviewsRef = database.getReference("reviews");
    DatabaseReference eventsRef = database.getReference("events");
    DatabaseReference disableHRef = database.getReference("disableHours");

    public MyData(){ };



    public static void init (){
        if(instence==null)
            instence=new MyData();
    }

    public static MyData getInstence() {
        return instence;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public DatabaseReference getUsersRef() {
        return usersRef;
    }

    public DatabaseReference getEventsRef() {
        return eventsRef;
    }

    public DatabaseReference getDisableHRef() {
        return disableHRef;
    }

    public DatabaseReference getReviewsRef() {
        return reviewsRef;
    }
}
