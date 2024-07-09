package com.example.prodajemkupujem.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.prodajemkupujem.R;
import com.example.prodajemkupujem.Utils;
import com.example.prodajemkupujem.databinding.RowImagesPickedBinding;
import com.example.prodajemkupujem.models.ModelImagePicked;

import java.util.ArrayList;

public class AdapterImagesPicked extends RecyclerView.Adapter<AdapterImagesPicked.HolderImagesPicked> {

    
    private RowImagesPickedBinding binding;
    
    private static final String TAG = "IMAGES_TAG";

    
    private Context context;
    
    private ArrayList<ModelImagePicked> imagePickedArrayList;

    private String adId;

    /**Constructor*
     * @param context The context of activity/fragment from where instance of AdapterImagesPicked class is created
     * @param imagePickedArrayList The list of the images picked/captured from Gallery/Camera or from Internet
     * @param adId Id of the Ad (will be used to delete image from firebase and maybe for more)*/
    public AdapterImagesPicked(Context context, ArrayList<ModelImagePicked> imagePickedArrayList, String adId) {
        this.context = context;
        this.imagePickedArrayList = imagePickedArrayList;
        this.adId = adId;
    }

    @NonNull
    @Override
    public HolderImagesPicked onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        
        binding = RowImagesPickedBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderImagesPicked(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderImagesPicked holder, int position) {
        
        ModelImagePicked model = imagePickedArrayList.get(position);

        if (model.getFromInternet()){
            
            String imageUrl = model.getImageUrl();

            Log.d(TAG, "onBindViewHolder: imageUrl: "+imageUrl);

            
            try {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_image_gray)
                        .into(holder.imageIv);
            } catch (Exception e) {
                Log.e(TAG, "onBindViewHolder: ", e);
            }
        } else {
            
            Uri imageUri = model.getImageUri();

            Log.d(TAG, "onBindViewHolder: imageUri: "+imageUri);

            
            try {
                Glide.with(context)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_image_gray)
                        .into(holder.imageIv);
            } catch (Exception e){
                Log.e(TAG, "onBindViewHolder: ", e);
            }

        }

        
        holder.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (model.getFromInternet()){
                    
                    deleteImageFirebase(model, holder, position);
                } else {
                    
                    imagePickedArrayList.remove(model);
                    notifyItemRemoved(position);
                }
            }
        });
    }

    private void deleteImageFirebase(ModelImagePicked model, HolderImagesPicked holder, int position) {
        
        String imageId = model.getId();

        Log.d(TAG, "deleteImageFirebase: adId: "+adId);
        Log.d(TAG, "deleteImageFirebase: imageId: "+imageId);
        
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
        ref.child(adId).child("Images").child(imageId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        
                        Log.d(TAG, "onSuccess: Deleted");
                        Utils.toast(context, "Image Deleted!");

                        try {
                            
                            imagePickedArrayList.remove(model);
                            notifyItemRemoved(position);
                        } catch (Exception e){
                            Log.e(TAG, "onSuccess: ", e);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        
                        Log.e(TAG, "onFailure: ", e);
                        Utils.toast(context, "Failed to delete image due to "+e.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return imagePickedArrayList.size(); 
    }

    /** View holder class to hold/init UI Views of the row_images_picked.xml*/
    class HolderImagesPicked extends RecyclerView.ViewHolder{

        
        ImageView imageIv;
        ImageButton closeBtn;

        public HolderImagesPicked(@NonNull View itemView) {
            super(itemView);

            
            imageIv = binding.imageIv;
            closeBtn = binding.closeBtn;
        }
    }
}
