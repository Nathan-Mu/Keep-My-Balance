package edu.monash.fit4039.keepmybalance;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;
import static edu.monash.fit4039.keepmybalance.JSONReader.*;

/**
 * Created by nathan on 16/5/17.
 */

public class ChildCategory implements Parcelable{
    private int childCategoryId = -1;
    private String childName;
    private ParentCategory parentCategoryId;

    public ChildCategory() {
    }

    //non-default constructor
    //params: child category name, parent category id (parent category object)
    public ChildCategory(String childName, ParentCategory parentCategoryId) {
        this.childCategoryId = 1;
        this.childName = childName;
        this.parentCategoryId = parentCategoryId;
    }

    //non-default constructor
    //params: json object (received from server)
    public ChildCategory(JSONObject json)
    {
        this.childCategoryId = getIntegerFromJSONObject(json, "childCategoryId");
        this.childName = getStringFromJSONObject(json, "childName");
        this.parentCategoryId = new ParentCategory(getJSONObjectFromJSONObject(json, "parentCategoryId"));
    }

    //non-default constructor (Parcelable interface)
    public ChildCategory(Parcel in)
    {
        this.childCategoryId = in.readInt();
        this.childName = in.readString();
        this.parentCategoryId = (ParentCategory) in.readValue(ParentCategory.class.getClassLoader());
    }

    //Parcelable interface method
    @Override
    public int describeContents() {
        return 0;
    }

    //Parcelable interface method
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(childCategoryId);
        parcel.writeString(childName);
        parcel.writeValue(parentCategoryId);
    }

    //Parcelable interface method (CREATOR)
    public static final Parcelable.Creator<ChildCategory> CREATOR = new Parcelable.Creator<ChildCategory>() {
        @Override
        public ChildCategory createFromParcel(Parcel in) {
            return new ChildCategory(in);
        }

        @Override
        public ChildCategory[] newArray(int size) {
            return new ChildCategory[size];
        }
    };

    //the rest methods are getters and setters
    public int getChildCategoryId() {
        return childCategoryId;
    }

    public void setChildCategoryId(int childCategoryId) {
        this.childCategoryId = childCategoryId;
    }

    public String getChildCategoryName() {
        return childName;
    }

    public void setChildCategoryName(String childName) {
        this.childName = childName;
    }

    public ParentCategory getParentCategory() {
        return parentCategoryId;
    }

    public void setParentCategory(ParentCategory parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
}
