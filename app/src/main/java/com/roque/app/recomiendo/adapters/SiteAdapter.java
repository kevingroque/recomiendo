package com.roque.app.recomiendo.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.roque.app.recomiendo.R;
import com.roque.app.recomiendo.activities.SitesDetailsActivity;
import com.roque.app.recomiendo.models.Site;

import java.util.ArrayList;
import java.util.List;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.ViewHolder>{

    private Context mContext;
    private List<Site> mSiteList;

    public SiteAdapter(Context mContext, List<Site> mSiteList) {
        this.mContext = mContext;
        this.mSiteList = mSiteList;
    }

    @Override
    public SiteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sites, parent, false);
        SiteAdapter.ViewHolder siteHolder = new SiteAdapter.ViewHolder(v);
        mContext = parent.getContext();
        return siteHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SiteAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final String siteId = mSiteList.get(position).SiteId;
        final String categoryId = mSiteList.get(position).getCategoryId();
        final String title = mSiteList.get(position).getNameSite();
        final String description = mSiteList.get(position).getDescriptionSite();
        final String address = mSiteList.get(position).getAddressSite();
        final String district = mSiteList.get(position).getDistrictSite();
        final String phone = mSiteList.get(position).getPhoneSite();
        final double longitude = mSiteList.get(position).getLongitude();
        final double latitude = mSiteList.get(position).getLatitude();
        final double rating = mSiteList.get(position).getRating();
        final int ratingPrice = mSiteList.get(position).getRatingPrice();
        final int ratingFood = mSiteList.get(position).getRatingFood();
        final int ratingService = mSiteList.get(position).getRatingService();
        final ArrayList<String> list_photurl = new ArrayList<>();

        final String recommends = "10K";
        final String url_index_0 = mSiteList.get(position).getUrl_imagen().get(0);


        for (int i = 0 ; i < mSiteList.get(position).getUrl_imagen().size() ; i++){
            list_photurl.add(mSiteList.get(position).getUrl_imagen().get(i));
        }


        holder.setSitesData(title,district,recommends,rating,url_index_0);

        holder.mCardSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(mContext, SitesDetailsActivity.class);
                detailIntent.putExtra("SITE_ID", siteId);
                detailIntent.putExtra("CATEGORY_ID", categoryId);
                detailIntent.putExtra("NAME_SITE", title);
                detailIntent.putExtra("DESCRIPTION_SITE", description);
                detailIntent.putExtra("ADDRESS_SITE", address);
                detailIntent.putExtra("DISTRICT_SITE", district);
                detailIntent.putExtra("PHONE_SITE", phone);
                detailIntent.putExtra("LONGITUDE_SITE", longitude);
                detailIntent.putExtra("LATITUDE_SITE", latitude);
                detailIntent.putExtra("RATING_SITE", rating);
                detailIntent.putExtra("RATING_PREICE_SITE", ratingPrice);
                detailIntent.putExtra("RATING_FOOD_SITE", ratingFood);
                detailIntent.putExtra("RATING_SERVICE_SITE", ratingService);
                detailIntent.putExtra("URL_ARRAY", list_photurl);
                detailIntent.putExtra("URL_INDEX_0", url_index_0);
                mContext.startActivity(detailIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSiteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView mTitle, mDistrict, mRecommends, mRating;
        private ImageView mImageSite;
        private CardView mCardSite;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mCardSite = mView.findViewById(R.id.cv_itemsite_card);

        }

        public void setSitesData(String name, String district, String recomends, double rating, String url_imagen){
            mTitle = mView.findViewById(R.id.txt_itemsites_name);
            mDistrict = mView.findViewById(R.id.txt_itemsites_district);
            mRecommends = mView.findViewById(R.id.txt_itemsites_recommendations);
            mRating = mView.findViewById(R.id.txt_itemsites_rating);
            mImageSite = mView.findViewById(R.id.img_itemsites_huarique);

            mTitle.setText(name);
            mDistrict.setText(district);
            mRecommends.setText(recomends);
            mRating.setText((String.valueOf(rating)));
            Glide.with(mContext).load(url_imagen).into(mImageSite);
        }
    }
}
