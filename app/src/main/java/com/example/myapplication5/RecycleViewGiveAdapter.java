package com.example.myapplication5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecycleViewGiveAdapter extends RecyclerView.Adapter<RecycleViewGiveAdapter.ViewHolder> {

    private static final String TAG = "Dogood";
    private static final String GIVE_ITEM = "giveItem";
    private static final String OWNER_USER = "ownerUser";
    private Context context;
    private ArrayList<Review> reviews;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    //private User myUser;

    public RecycleViewGiveAdapter(Context context, ArrayList<Review> reviews) {
        Log.d(TAG, "RecyclerViewGiveAdapter: Im in adapter with: " + reviews.toString());
        this.context = context;
        this.reviews = reviews;
        //this.myUser = user;
    }

    @NonNull
    @Override
    public RecycleViewGiveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_row, parent, false);
        return new RecycleViewGiveAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewGiveAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Got item: " + reviews.get(position).toString());
        Review temp = reviews.get(position);
        holder.itemName.setText(temp.getName());
        holder.postDate.setText(temp.getDate());
        holder.itemDescription.setText(temp.getDescription());
        ShapeableImageView itemImage = holder.itemPhoto;
        getPhotoFromStorage(itemImage, position);
    }

    /**
     * A method to open the item details activity
     */
//    private void openItemDetails(int position) {
//        Log.d(TAG, "openItemDetails: ");
//        ((ItemDetailsListener) context).getSelectedItem(reviews.get(position), true);
//    }

    /**
     * A method to get the item photo from the storage
     */
    private void getPhotoFromStorage(final ShapeableImageView itemPhoto, int position) {
        Log.d(TAG, "getPhotoFromStorage: Fetching photo from storage");
        String itemID = reviews.get(position).getId();

        String path = "gs://" + context.getString(R.string.google_storage_bucket) + "/" + itemID + ".jpg";
        Log.d(TAG, "getPhotoFromStorage: Fetching: " + path);
        StorageReference gsReference = storage.getReferenceFromUrl(path);

        gsReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: " + uri);
                // create a ProgressDrawable object which we will show as placeholder
                CircularProgressDrawable drawable = new CircularProgressDrawable(context);
                drawable.setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.black);
                drawable.setCenterRadius(30f);
                drawable.setStrokeWidth(5f);
                // set all other properties as you would see fit and start it
                drawable.start();
                Glide.with(context).load(uri).placeholder(drawable).into(itemPhoto);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: Exception: " + exception.getMessage());
            }
        });
    }

    /**
     * This Function converts the String back to Bitmap
     */
    public Bitmap stringToBitMap(String encodedString) {
        Log.d(TAG, "stringToBitMap: Converting string to bitmap");
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            Log.d(TAG, "stringToBitMap: Exception: " + e.getMessage());
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }


    /**
     * An inner class to specify each row contents
     */
    public class ViewHolder extends RecyclerView.ViewHolder { // To hold each row

        MaterialTextView itemName,  itemDescription, postDate;
        ShapeableImageView itemPhoto;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        /**
         * A method to initialize the views
         */
        private void initViews() {
            itemDescription=itemView.findViewById(R.id.reviewRow_LBL_text);
            itemName = itemView.findViewById(R.id.reviewRow_LBL_Name);
            postDate = itemView.findViewById(R.id.reviewRow_LBL_Date);
            itemPhoto = itemView.findViewById(R.id.reviewRow_IMG);
        }


    }
}