package com.example.android.journal.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

@IgnoreExtraProperties
public class Entry implements Parcelable{

    public String id;
    public String uid;
    public String author;
    public String body;
    public Date updatedAt;


    public Entry() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Entry(String uid, String author, String body, Date updatedAt) {
        this.uid = uid;
        this.author = author;
        this.body = body;
        this.updatedAt = updatedAt;
    }

    protected Entry(Parcel in) {
        id = in.readString();
        uid = in.readString();
        author = in.readString();
        body = in.readString();
        updatedAt = (Date) in.readValue(Date.class.getClassLoader());
    }

    public static final Creator<Entry> CREATOR = new Creator<Entry>() {
        @Override
        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        @Override
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("uid", uid);
        result.put("author", author);
        result.put("body", body);
        result.put("updatedAt", updatedAt);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.uid);
        parcel.writeString(this.author);
        parcel.writeString(this.body);
        parcel.writeValue(this.updatedAt);
    }
}
