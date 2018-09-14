package edu.monash.fit4039.keepmybalance;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by nathan on 9/5/17.
 */

public class UserInfo implements Parcelable{

    private int userId = -1;
    private String username;
    private String password;

    public UserInfo() {
    }

    //non-default constructor
    public UserInfo(String username, String password) {
        this.userId = 1;
        this.username = username;
        this.password = password;
    }

    //non-default constructor (json object from server)
    public UserInfo(JSONObject jsonObject)
    {
        this.userId = JSONReader.getIntegerFromJSONObject(jsonObject, "userId");
        this.username = JSONReader.getStringFromJSONObject(jsonObject, "username");
        this.password = JSONReader.getStringFromJSONObject(jsonObject, "password");
    }

    //non-default constructor (Parcel interface)
    protected UserInfo(Parcel in)
    {
        userId = in.readInt();
        username = in.readString();
        password = in.readString();

    }

    //Parcelable interface method
    @Override
    public int describeContents() {
        return 0;
    }

    //Parcelable interface method
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(userId);
        parcel.writeString(username);
        parcel.writeString(password);
    }

    //Parcelable interface method (CREATOR)
    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    //the rest methods are getter and setter
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
