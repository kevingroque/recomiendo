package com.roque.app.recomiendo.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.roque.app.recomiendo.R;
import com.roque.app.recomiendo.adapters.CustomInfoWindowAdapter;
import com.roque.app.recomiendo.models.Site;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;

    private FirebaseFirestore mFirestore;
    private List<Site> mSiteList;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mSiteList = new ArrayList<>();

        return mView;

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map_main_huariques);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setMinZoomPreference(11);

        mFirestore = FirebaseFirestore.getInstance();
        mSiteList = new ArrayList<>();

        mFirestore.collection("Sites").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        String siteId = documentChange.getDocument().getId();
                        Site site = documentChange.getDocument().toObject(Site.class).withId(siteId);

                        Site infoSite = new Site();
                        infoSite.setNameSite(site.getNameSite());
                        infoSite.setUrl_imagen(site.getUrl_imagen());
                        infoSite.setDescriptionSite(site.getDescriptionSite());

                        CustomInfoWindowAdapter infoWindowAdapter = new CustomInfoWindowAdapter(getActivity());
                        mGoogleMap.setInfoWindowAdapter(infoWindowAdapter);

                        LatLng latLng = new LatLng(site.getLatitude(), site.getLongitude());

                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(latLng)
                                .title(site.getNameSite())
                                .snippet(site.getDescriptionSite())
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                        Marker marker = mGoogleMap.addMarker(markerOptions);
                        marker.setTag(infoSite);
                        marker.showInfoWindow();

                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        float zoon = 8;
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoon));
                    }
                }
            }
        });
    }

    private void markerOptions(){

    }
}
