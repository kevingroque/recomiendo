package com.roque.app.recomiendo.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.roque.app.recomiendo.R;
import com.roque.app.recomiendo.activities.PhotoDetailActivity;
import com.roque.app.recomiendo.adapters.GalleryAdapter;
import com.roque.app.recomiendo.commons.RecyclerItemGalleryClickListener;
import com.roque.app.recomiendo.models.PhotoGallery;
import com.roque.app.recomiendo.models.Site;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GallerySiteFragment extends Fragment {

    private GalleryAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<PhotoGallery> mGalleryList = new ArrayList<>();
    private ArrayList<String> mUrlList = new ArrayList<>();

    public GallerySiteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery_site, container, false);

        mUrlList = getArguments().getStringArrayList("URL_ARRAY");

        for (int i = 0; i < mUrlList.size(); i++) {
            PhotoGallery photoGallery = new PhotoGallery();
            photoGallery.setPhotoId(String.valueOf(i));
            photoGallery.setPhotoUrl(mUrlList.get(i));
            mGalleryList.add(photoGallery);

        }


        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_sitesdetails_gallery);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setHasFixedSize(true);


        mAdapter = new GalleryAdapter(getActivity(), mGalleryList);
        mRecyclerView.setAdapter(mAdapter);


        mRecyclerView.addOnItemTouchListener(new RecyclerItemGalleryClickListener(getActivity(),
                new RecyclerItemGalleryClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        Intent intent = new Intent(getActivity(), PhotoDetailActivity.class);
                        intent.putParcelableArrayListExtra("data", mGalleryList);
                        intent.putExtra("pos", position);
                        startActivity(intent);

                    }
                }));

        return view;
    }


}
