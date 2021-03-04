package com.example.myapplication5;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Review implements Serializable {
    String id; // ID sample: "G24" - Give item 24
    String name;
    String category;
    String description;
    String pictures;
    String date;



    public Review() {
    }

    public Review(String id, String name, String category
            , String description, String pictures, String date) {
        this.id = id;
        this.name = name;
        this.category = category;

        this.description = description;
        this.pictures = pictures;
        this.date = date;

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    @Override
    public String toString() {
        return "GiveItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", pictures='" + pictures + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}



