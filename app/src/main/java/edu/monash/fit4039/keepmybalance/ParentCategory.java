package edu.monash.fit4039.keepmybalance;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;
import static edu.monash.fit4039.keepmybalance.JSONReader.getIntegerFromJSONObject;
import static edu.monash.fit4039.keepmybalance.JSONReader.getStringFromJSONObject;
import static edu.monash.fit4039.keepmybalance.JSONReader.getJSONObjectFromJSONObject;

/**
 * Created by nathan on 16/5/17.
 */

public class ParentCategory implements Parcelable{
    private int parentCategoryId = -1;
    private String parentCategoryName;
    private UserInfo userId;

    public ParentCategory() {
    }

    //non-default constructor
    public ParentCategory(String parentCategoryName, UserInfo userId) {
        this.parentCategoryName = parentCategoryName;
        this.userId = userId;
    }

    //non-default construtor (json object received from server)
    public ParentCategory(JSONObject json)
    {
        this.parentCategoryId = getIntegerFromJSONObject(json, "parentCategoryId");
        this.parentCategoryName = getStringFromJSONObject(json, "parentCategoryName");
        this.userId = new UserInfo(getJSONObjectFromJSONObject(json, "userId"));
    }

    //non-default constructor (Parcelable interface)
    public ParentCategory(Parcel in)
    {
        this.parentCategoryId = in.readInt();
        this.parentCategoryName = in.readString();
        this.userId = (UserInfo) in.readValue(UserInfo.class.getClassLoader());
    }

    //Parcelable interface method
    @Override
    public int describeContents() {
        return 0;
    }

    //Parcelable interface method
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(parentCategoryId);
        parcel.writeString(parentCategoryName);
        parcel.writeValue(userId);
    }

    //Parcelable interface method (CREATOR)
    public static final Creator<ParentCategory> CREATOR = new Creator<ParentCategory>() {
        @Override
        public ParentCategory createFromParcel(Parcel in) {
            return new ParentCategory(in);
        }

        @Override
        public ParentCategory[] newArray(int size) {
            return new ParentCategory[size];
        }
    };

    //the rest method are getters and setters
    public int getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(int parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    public UserInfo getUserId() {
        return userId;
    }

    public void setUserId(UserInfo userId) {
        this.userId = userId;
    }
}
