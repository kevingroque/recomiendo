package com.roque.app.recomiendo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import com.roque.app.recomiendo.adapters.SiteAdapter;
import com.roque.app.recomiendo.models.Category;
import com.roque.app.recomiendo.models.Site;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SitesFragment extends Fragment{

    private String mCategoryId;
    private RecyclerView mRvSites;
    private List<Site> mSiteList;
    private List<Category> mCategoryList;
    private SiteAdapter mSiteAdapter;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;
    private DocumentSnapshot mLastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private Query sitesQuery;

    public SitesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_site, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mSiteList = new ArrayList<>();
        mCategoryList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvSites = (RecyclerView) view.findViewById(R.id.rv_huariques);
        mSiteAdapter = new SiteAdapter(getContext(), mSiteList);
        mRvSites.setHasFixedSize(true);
        mRvSites.setLayoutManager(layoutManager);
        mRvSites.setAdapter(mSiteAdapter);
        getDataAllSites();

        return view;
    }

    private void getDataAllSites() {
        sitesQuery = mFirestore.collection("Sites").orderBy("nameSite", Query.Direction.ASCENDING).limit(10);
        sitesQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    if (isFirstPageFirstLoad) {
                        mLastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        mSiteList.clear();
                    }

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String siteId = doc.getDocument().getId();
                            Site site = doc.getDocument().toObject(Site.class).withId(siteId);

                            if (isFirstPageFirstLoad) {
                                mSiteList.add(site);
                            } else {
                                mSiteList.add(0, site);
                            }
                            mSiteAdapter.notifyDataSetChanged();
                        }
                    }
                    isFirstPageFirstLoad = false;
                }
            }
        });

    }

}
