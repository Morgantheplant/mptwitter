package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import com.codepath.apps.restclienttemplate.constants.DateAbbreviation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Tweet implements Parcelable {
    public String body;

    public long getPostId() {
        return postId;
    }

    public long postId;
    public String createdAt;
    public User user;
    public static Tweet fromJSON(JSONObject jsonObject)  {
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.postId = jsonObject.getLong("id");
            tweet.createdAt = getRelativeTimeAgo(jsonObject.getString("created_at"));
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            return tweet;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return tweet;
    }

    public Tweet(){ }

    public Tweet(String body, long uid, String createdAt, User user){
        this.body = body;
        this.createdAt = createdAt;
        this.postId = uid;
        this.user = user;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray response){
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < response.length(); i++){
            try {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                tweets.add(tweet);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return tweets;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = getAbbreviatedTimeSpan(dateMillis);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    public static String getAbbreviatedTimeSpan(long timeMillis) {
        long span = Math.max(System.currentTimeMillis() - timeMillis, 0);
        if (span >= DateUtils.YEAR_IN_MILLIS) {
            return (span / DateUtils.YEAR_IN_MILLIS) + DateAbbreviation.ABBR_YEAR;
        }
        if (span >= DateUtils.WEEK_IN_MILLIS) {
            return (span / DateUtils.WEEK_IN_MILLIS) + DateAbbreviation.ABBR_WEEK;
        }
        if (span >= DateUtils.DAY_IN_MILLIS) {
            return (span / DateUtils.DAY_IN_MILLIS) + DateAbbreviation.ABBR_DAY;
        }
        if (span >= DateUtils.HOUR_IN_MILLIS) {
            return (span / DateUtils.HOUR_IN_MILLIS) + DateAbbreviation.ABBR_HOUR;
        }
        return (span / DateUtils.MINUTE_IN_MILLIS) + DateAbbreviation.ABBR_MINUTE;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(body);
        out.writeLong(postId);
        out.writeString(createdAt);
        out.writeParcelable(user, flags);
    }

    private Tweet(Parcel in) {
        body = in.readString();
        postId = in.readLong();
        createdAt = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Tweet> CREATOR
            = new Parcelable.Creator<Tweet>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };


}
