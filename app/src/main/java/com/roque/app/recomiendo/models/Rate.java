package com.roque.app.recomiendo.models;

public class Rate extends RateId{

    private String userId;
    private String siteId;
    private int ratingValuePrice;
    private int ratingValueFood;
    private int ratingValueService;
    private int ratingValueSecurity;

    public Rate() {
    }

    public Rate(String userId, String siteId, int ratingValuePrice, int ratingValueFood, int ratingValueService, int ratingValueSecurity) {
        this.userId = userId;
        this.siteId = siteId;
        this.ratingValuePrice = ratingValuePrice;
        this.ratingValueFood = ratingValueFood;
        this.ratingValueService = ratingValueService;
        this.ratingValueSecurity = ratingValueSecurity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public int getRatingValuePrice() {
        return ratingValuePrice;
    }

    public void setRatingValuePrice(int ratingValuePrice) {
        this.ratingValuePrice = ratingValuePrice;
    }

    public int getRatingValueFood() {
        return ratingValueFood;
    }

    public void setRatingValueFood(int ratingValueFood) {
        this.ratingValueFood = ratingValueFood;
    }

    public int getRatingValueService() {
        return ratingValueService;
    }

    public void setRatingValueService(int ratingValueService) {
        this.ratingValueService = ratingValueService;
    }

    public int getRatingValueSecurity() {
        return ratingValueSecurity;
    }

    public void setRatingValueSecurity(int ratingValueSecurity) {
        this.ratingValueSecurity = ratingValueSecurity;
    }
}
