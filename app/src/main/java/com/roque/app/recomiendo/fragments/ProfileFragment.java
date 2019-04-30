package com.roque.app.recomiendo.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.roque.app.recomiendo.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private String current_user_id;

    private StorageReference mStorageReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;

    private CircleImageView mProfileImage;
    private TextView mTxtNameUser;
    private Uri mMainImageURI = null;




    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        current_user_id = mFirebaseAuth.getCurrentUser().getUid();

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mProfileImage = (CircleImageView) view.findViewById(R.id.img_profile_photo);
        mTxtNameUser = (TextView) view.findViewById(R.id.txt_profile_name);


        getUserData();


        return view;
    }

    private void getUserData(){
        mFirebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){

                        String getName = task.getResult().getString("name");
                        String getImage = task.getResult().getString("avatar");
                        String getPhone = task.getResult().getString("phone");
                        String getAdreess = task.getResult().getString("address");

                        mMainImageURI = Uri.parse(getImage);

                        mTxtNameUser.setText(getName);
                        //mTelefono.setText(getPhone);
                        //mAdreess.setText(getAdreess);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.ic_profile);

                        Glide.with(getContext()).setDefaultRequestOptions(placeholderRequest).load(getImage).into(mProfileImage);


                    }else {
                        Toast.makeText(getActivity(), "(FIRESTORE Retrieve Error)", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

}
