package com.example.myapplication5;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class NewReviewActivity extends AppCompatActivity implements PhotoModeListener {
    private static final String TAG = "CustomerTells";
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private int itemCount = 0;
    private static final String NEW_GIVE_ITEM = "111";
    public static final String CURRENT_USER = "currentUser";
    private static final int CAMERA_PERMISSION_SETTINGS_REQUSETCODE = 123;
    private static final int STORAGE_PERMISSION_SETTINGS_REQUSETCODE = 133;
    private static final int CAMERA_PICTURE_REQUEST = 124;
    private static final int STORAGE_PICTURE_REQUEST = 125;
    private static final int NEW_GIVE_ITEM_RESULT_CODE = 1011;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private Uri filePathUri;

    private static final String CUSTOMER_COUNT = "customerCount";

    private Bitmap userCustomImage;
    private ShapeableImageView itemPhoto;
    private MaterialButton submitBtn;
    private ImageView imageView;
    private TextInputLayout name, description;
    String imageURL = "";
    private SpinnerType category;
    private Context context;
    private MaterialButton save;




    private ArrayList<Review> reviewList = new ArrayList<Review>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_review);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        initViews();

    }

    private void initViews() {
        Log.d(TAG, "initViews: Creating views");
        itemPhoto = findViewById(R.id.NewReview_IMG_itemPhoto);
        itemPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoMode(true);
            }
        });
        name = findViewById(R.id.newReview_LBL_name);
        description = findViewById(R.id.newReview_LBL_Description);
        category = new SpinnerType(findViewById(R.id.newReview_LST_typeSpinner), NewReviewActivity.this);
        save = findViewById(R.id.NewReview_submit);
        category.initSpinner();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkForValidInput();

                finish();
            }
        });
    }


    private void checkForValidInput() {
        Log.d(TAG, "checkForValidInput: Checking user input");
        if (name.getEditText().getText().toString().equals("")) {
            Log.d(TAG, "checkForValidInput: Empty item name");
            name.setError(getString(R.string.enter_name_error));
            save.setEnabled(true);
            return;
        }
        if (description.getEditText().getText().toString().equals("")) {
            Log.d(TAG, "checkForValidInput: Empty item description");
            description.setError(getString(R.string.please_enter_item_decription));
            save.setEnabled(true);
            return;
        }


        if (userCustomImage == null) {
            Log.d(TAG, "checkForValidInput: User did not upload item photo");
            Toast.makeText(this, "Please enter item photo!", Toast.LENGTH_SHORT).show();
            itemPhoto.setStrokeColor(ColorStateList.valueOf(getColor(R.color.removeBtnColor)));
            itemPhoto.setStrokeWidth(20f);
            save.setEnabled(true);
            return;
        }

        uploadToStorage();
        addReview();


    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void uploadToStorage() {
        Review temp = new Review(name.getEditText().getText().toString(), category.getType()
                , description.getEditText().getText().toString(),"Test", imageURL,"12.21");
        if (mImageUri != null) {
            Log.d(TAG, "uploadToStorage: null");
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();
                                    imageURL = downloadUrl;

                                }
                            });
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // mProgressBar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(NewReviewActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewReviewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            // mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }






    public void addReview(){


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Reviews");
        Review temp = new Review(name.getEditText().getText().toString(), category.getType()
                , description.getEditText().getText().toString(),"Test", imageURL,"12.21");
        Log.d(TAG, "addReview: " +imageURL);
        myRef.child(firebaseUser.getUid()).setValue(temp);
        finish();




        }











    private void checkingForCameraPermissions () {
        Log.d(TAG, "checkingForCameraPermissions: checking for users permissions");

        Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.d(TAG, "onPermissionGranted: User given permission");
                        openCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            Log.d(TAG, "onPermissionDenied: User denied permission permanently!");
                            AlertDialog.Builder builder = new AlertDialog.Builder(NewReviewActivity.this);
                            builder.setTitle(getString(R.string.permission_denied))
                                    .setMessage(getString(R.string.permission_denied_explication_camera))
                                    .setNegativeButton(getString(R.string.cancel), null)
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.d(TAG, "onClick: Opening settings activity");
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivityForResult(intent, CAMERA_PERMISSION_SETTINGS_REQUSETCODE);
                                        }
                                    }).show();
                        } else {
                            Log.d(TAG, "onPermissionDenied: User denied permission!");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: Im im activity result");
        switch (requestCode) {
            case CAMERA_PERMISSION_SETTINGS_REQUSETCODE:
                Log.d(TAG, "onActivityResult: I came from app settings: camera");
                break;
            case STORAGE_PERMISSION_SETTINGS_REQUSETCODE:
                Log.d(TAG, "onActivityResult: I came from app settings: storage");
                break;
            case CAMERA_PICTURE_REQUEST:
                Log.d(TAG, "onActivityResult: I came from camera");
                if (data != null) {

                    mImageUri = data.getData();
                    userCustomImage = (Bitmap) data.getExtras().get("data");
                    Log.d(TAG, "onActivityResult: "+userCustomImage);

                    Log.d(TAG, "onActivityResult: "+mImageUri);
                    itemPhoto.setStrokeWidth(30);
                    itemPhoto.setStrokeColor(getColorStateList(R.color.colorPrimary));
                    itemPhoto.setImageBitmap(userCustomImage);

                }


                break;

        }
    }


    @Override
    public void photoMode(Boolean fromCamera) {
        if (fromCamera) {
            Log.d(TAG, "photoMode: Taking picture from camera");
            if (ContextCompat.checkSelfPermission(NewReviewActivity.this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onClick: User already given permission, moving straight to camera");
                openCamera();
            } else {
                checkingForCameraPermissions();
            }
        }

    }

    private void openCamera() {
        Log.d(TAG, "openCamera: opening camera");
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PICTURE_REQUEST);
    }



}

