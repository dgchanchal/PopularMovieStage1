package com.example.android.popularmovie;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dev Chanchal on 3/17/2016.
 */
public class Movie implements Parcelable {
    private String title;
    private String image;
    private String description;
    private String rating;
    private String releaseDate;

    public Movie(String title, String image, String description,
                 String rating, String releaseDate) {
        this.title = title;
        this.image = image;
        this.description = description;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(image);
        out.writeString(description);
        out.writeString(rating);
        out.writeString(releaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Movie(Parcel in) {
        title = in.readString();
        image = in.readString();
        description = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}