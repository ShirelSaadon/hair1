package com.example.myapplication5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;


public class CustomerTellsActivity extends AppCompatActivity  {
    private static final String TAG = "CustomerTells";
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private String userGson ;
    private static final int CAMERA_PERMISSION_SETTINGS_REQUSETCODE = 123;
    private static final int STORAGE_PERMISSION_SETTINGS_REQUSETCODE = 133;
    private static final int CAMERA_PICTURE_REQUEST = 124;
    private static final int STORAGE_PICTURE_REQUEST = 125;
    private Bitmap userCustomImage;
    private ShapeableImageView itemPhoto;
    MyData myData =new MyData();
    private ArrayList<Review> reviewList=new ArrayList<Review>();
    ////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        myData.init();
        setContentView(R.layout.list_and_dialog_button);
        //Bundle bundle = getIntent().getExtras();
        //userGson = bundle.getString("USER_EXTRA");
        //user=new Gson().fromJson(userGson, User.class);
         recyclerView = findViewById(R.id.RECYCLER_VIEW);
         reviewsList();
    }

    private void initBtnAddEvent(){
        Log.d(TAG, "initBtnAddEvent: ");
        floatingActionButton=findViewById(R.id.BTN_addEventButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewReviewEvent();
            }
        });
    }

    private void openNewReviewEvent() {
        Intent myIntent = new Intent(this, NewReviewActivity.class);
        startActivity(myIntent);
    }

    public void reviewsList() {
        Log.d(TAG, "reviewsList: ");
        initBtnAddEvent();
        ArrayList<Review> reviewsList=new ArrayList<Review>();
        myData.getInstence().getReviewsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ENTER");
                reviewsList.removeAll(reviewsList);
                if(dataSnapshot.getChildrenCount()==0){
                    Log.d(TAG, "onDataChange: 0 child" );
                }else {
                    populatelist(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void populatelist(DataSnapshot dataSnapshot) {
        reviewList.removeAll(reviewList);
        // MyData.getInstence().getDisableHRef().child(date.toString()).removeValue();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Log.d(TAG, "populatelist: "+snapshot.getClass()+"  "+snapshot.getValue());
            Review review = snapshot.getValue(Review.class);
            reviewList.add(review);
            Log.d(TAG, "populateList: " + review.toString());

            if (reviewList.size() == dataSnapshot.getChildrenCount()) {
                recyclerView.setVisibility(View.VISIBLE);
                RecycleViewGiveAdapter adapter =new RecycleViewGiveAdapter(this,reviewList);
                recyclerView.setAdapter(adapter);
                Log.d(TAG, "populatelist: DONE");
            }
        }
    }



}

