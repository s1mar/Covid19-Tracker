package com.s1mar.covid19tracker.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MFeedItem implements Parcelable {

    @DocumentId
    private String id;
    private String subject;
    private String desc;

    @ServerTimestamp
    private Date time;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public MFeedItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.subject);
        dest.writeString(this.desc);
        dest.writeLong(this.time != null ? this.time.getTime() : -1);
    }

    protected MFeedItem(Parcel in) {
        this.id = in.readString();
        this.subject = in.readString();
        this.desc = in.readString();
        long tmpTime = in.readLong();
        this.time = tmpTime == -1 ? null : new Date(tmpTime);
    }

    public static final Creator<MFeedItem> CREATOR = new Creator<MFeedItem>() {
        @Override
        public MFeedItem createFromParcel(Parcel source) {
            return new MFeedItem(source);
        }

        @Override
        public MFeedItem[] newArray(int size) {
            return new MFeedItem[size];
        }
    };
}
