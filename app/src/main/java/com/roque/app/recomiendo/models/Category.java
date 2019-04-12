package com.roque.app.recomiendo.models;

public class Category extends CategoryId{

    private String nameCategory;
    private String descriptionCategory;
    private String url_imagen;
    private boolean selected;

    public Category() {
    }

    public Category(String nameCategory, String descriptionCategory, String url_imagen, boolean selected) {
        this.nameCategory = nameCategory;
        this.descriptionCategory = descriptionCategory;
        this.url_imagen = url_imagen;
        this.selected = selected;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public String getDescriptionCategory() {
        return descriptionCategory;
    }

    public void setDescriptionCategory(String descriptionCategory) {
        this.descriptionCategory = descriptionCategory;
    }

    public String getUrl_imagen() {
        return url_imagen;
    }

    public void setUrl_imagen(String url_imagen) {
        this.url_imagen = url_imagen;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
