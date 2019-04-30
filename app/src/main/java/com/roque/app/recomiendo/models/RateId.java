package com.roque.app.recomiendo.models;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class RateId {
    @Exclude
    public String RateId;

    public <T extends RateId> T withId(@NonNull final String id) {
        this.RateId = id;
        return (T) this;
    }
}
