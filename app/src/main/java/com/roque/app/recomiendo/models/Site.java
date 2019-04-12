package com.roque.app.recomiendo.models;

import java.util.ArrayList;

public class Site extends SiteId{

    private String nameSite;
    private String categoryId;
    private String descriptionSite;
    private String addressSite;
    private String districtSite;
    private String phoneSite;
    private double latitude;
    private double longitude;
    private double rating;
    private int ratingFood;
    private int ratingPrice;
    private int ratingService;
    private ArrayList<String> url_imagen;

    public Site() {
    }

    public Site(String nameSite, String categoryId, String descriptionSite, String addressSite, String districtSite, String phoneSite, double latitude, double longitude, double rating, int ratingFood, int ratingPrice, int ratingService, ArrayList<String> url_imagen) {
        this.nameSite = nameSite;
        this.categoryId = categoryId;
        this.descriptionSite = descriptionSite;
        this.addressSite = addressSite;
        this.districtSite = districtSite;
        this.phoneSite = phoneSite;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.ratingFood = ratingFood;
        this.ratingPrice = ratingPrice;
        this.ratingService = ratingService;
        this.url_imagen = url_imagen;
    }

    public String getNameSite() {
        return nameSite;
    }

    public void setNameSite(String nameSite) {
        this.nameSite = nameSite;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categorySite) {
        this.categoryId = categorySite;
    }

    public String getDescriptionSite() {
        return descriptionSite;
    }

    public void setDescriptionSite(String descriptionSite) {
        this.descriptionSite = descriptionSite;
    }

    public String getAddressSite() {
        return addressSite;
    }

    public void setAddressSite(String addressSite) {
        this.addressSite = addressSite;
    }

    public String getDistrictSite() {
        return districtSite;
    }

    public void setDistrictSite(String districtSite) {
        this.districtSite = districtSite;
    }

    public String getPhoneSite() {
        return phoneSite;
    }

    public void setPhoneSite(String phoneSite) {
        this.phoneSite = phoneSite;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRatingFood() {
        return ratingFood;
    }

    public void setRatingFood(int ratingFood) {
        this.ratingFood = ratingFood;
    }

    public int getRatingPrice() {
        return ratingPrice;
    }

    public void setRatingPrice(int ratingPrice) {
        this.ratingPrice = ratingPrice;
    }

    public int getRatingService() {
        return ratingService;
    }

    public void setRatingService(int ratingService) {
        this.ratingService = ratingService;
    }

    public ArrayList<String> getUrl_imagen() {
        return url_imagen;
    }

    public void setUrl_imagen(ArrayList<String> url_imagen) {
        this.url_imagen = url_imagen;
    }
}
