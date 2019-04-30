package com.roque.app.recomiendo.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.clustering.ClusterManager;
import com.roque.app.recomiendo.R;
import com.roque.app.recomiendo.adapters.CustomInfoWindowAdapter;
import com.roque.app.recomiendo.models.Category;
import com.roque.app.recomiendo.models.ClusterMarker;
import com.roque.app.recomiendo.models.Site;
import com.roque.app.recomiendo.utils.ClusterManagerRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback{

    private static final String TAG = "HomeFragment";

    private MapView mMapView;
    private View mView;

    //vars
    private StorageReference mStorageReference;
    private FirebaseFirestore mFirebaseFirestore;
    private GoogleMap mGoogleMap;
    private List<Site> mSiteList;
    private ClusterManager<ClusterMarker> mClusterManager;
    private ClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private LatLng position;
    private String categoryId, mUrlImageCategory;

    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    Bitmap bmp = Bitmap.createBitmap(80, 80, conf);


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
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

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mSiteList = new ArrayList<>();

        mFirebaseFirestore.collection("Sites").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
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
                        categoryId = site.getCategoryId();

                        CustomInfoWindowAdapter infoWindowAdapter = new CustomInfoWindowAdapter(getActivity());
                        mGoogleMap.setInfoWindowAdapter(infoWindowAdapter);

                        position = new LatLng(site.getLatitude(), site.getLongitude());

                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(position)
                                .title(site.getNameSite())
                                .snippet(site.getDescriptionSite())
                                .position(position)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                        Marker marker = mGoogleMap.addMarker(markerOptions);
                        marker.setTag(infoSite);
                        marker.showInfoWindow();


                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                        float zoon = 8;
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoon));
                    }
                }
            }
        });
    }


    private void addMapMarkers(){

        if (mGoogleMap != null){
            if (mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
            }

            if (mClusterManagerRenderer == null){
                mClusterManagerRenderer = new ClusterManagerRenderer(getActivity(), mGoogleMap, mClusterManager);
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            for (Site sites : mSiteList){
                Log.d(TAG, "addMarkers: location : " + position.toString());
                try{
                    int iconMarker = R.drawable.ic_location_24dp; // default marker
                    try{
                        iconMarker = Integer.parseInt(mUrlImageCategory);
                    }catch (NumberFormatException e){
                        Log.d(TAG, "addMarkers: no avatar for: " );
                    }

                    ClusterMarker clusterMarker = new ClusterMarker(position, "", "", iconMarker);
                    mClusterManager.addItem(clusterMarker);
                    mClusterMarkers.add(clusterMarker);

                }catch (NullPointerException e){
                    Log.d(TAG, "addMarkers: NullPointerExceptionr: " + e.getMessage() );
                }
            }

            mClusterManager.cluster();
        }

    }
}
