package edu.monash.fit4039.keepmybalance;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.Date;
import static edu.monash.fit4039.keepmybalance.JSONReader.*;
import static edu.monash.fit4039.keepmybalance.Time.*;

/**
 * Created by nathan on 16/5/17.
 */

public class FundChange implements Parcelable{
    private int changeId = -1;
    private String changeType;
    private double amount;
    private ChildCategory childCategoryId;
    private Account accountId;
    private Date changeDate;
    private String locationName;
    private double locationLatitude;
    private double locationLongitude;
    private String description;

    public FundChange() {
    }

    //non-default constructor
    public FundChange(String changeType, double amount, ChildCategory childCategoryId, Account accountId, Date changeDate, String locationName, double locationLatitude, double locationLongitude, String description) {
        this.changeId = 1;
        this.changeType = changeType;
        this.amount = amount;
        this.childCategoryId = childCategoryId;
        this.accountId = accountId;
        this.changeDate = changeDate;
        this.locationName = locationName;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.description = description;
    }

    //non-default constructor (json object from server)
    public FundChange(JSONObject json) {
        this.changeId = getIntegerFromJSONObject(json, "changeId");
        this.changeType = getStringFromJSONObject(json, "changeType");
        this.amount = getDoubleFromJSONObject(json, "amount");
        this.childCategoryId = new ChildCategory(getJSONObjectFromJSONObject(json, "childCategoryId"));
        this.accountId = new Account(getJSONObjectFromJSONObject(json, "accountId"));
        this.changeDate = toDate(getStringFromJSONObject(json, "changeDate"), JSON_DATE_FORMAT);
        this.locationName = getStringFromJSONObject(json, "locationName");
        this.locationLatitude = getDoubleFromJSONObject(json, "locationLatitude");
        this.locationLongitude = getDoubleFromJSONObject(json, "locationLongitude");
        this.description = getStringFromJSONObject(json, "description");
    }

    //non-default constructor (Parcelable interface)
    public FundChange(Parcel in) {
        this.changeId = in.readInt();
        this.changeType = in.readString();
        this.amount = in.readDouble();
        this.childCategoryId = (ChildCategory) in.readValue(ChildCategory.class.getClassLoader());
        this.accountId = (Account) in.readValue(Account.class.getClassLoader());
        this.changeDate = (Date) in.readSerializable();
        this.locationName = in.readString();
        this.locationLatitude = in.readDouble();
        this.locationLongitude = in.readDouble();
        this.description = in.readString();
    }

    //Parcelable interface method
    @Override
    public int describeContents() {
        return 0;
    }

    //Parcelable interface method
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(changeId);
        parcel.writeString(changeType);
        parcel.writeDouble(amount);
        parcel.writeValue(childCategoryId);
        parcel.writeValue(accountId);
        parcel.writeSerializable(changeDate);
        parcel.writeString(locationName);
        parcel.writeDouble(locationLatitude);
        parcel.writeDouble(locationLongitude);
        parcel.writeString(description);
    }

    //Parcelable interface method (CREATOR)
    public static final Creator<FundChange> CREATOR = new Creator<FundChange>() {
        @Override
        public FundChange createFromParcel(Parcel in) {
            return new FundChange(in);
        }

        @Override
        public FundChange[] newArray(int size) {
            return new FundChange[size];
        }
    };

    //the rest methods are getters and setters
    public int getChangeId() {
        return changeId;
    }

    public void setChangeId(int changeId) {
        this.changeId = changeId;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public ChildCategory getChildCategoryId() {
        return childCategoryId;
    }

    public void setChildCategoryId(ChildCategory childCategoryId) {
        this.childCategoryId = childCategoryId;
    }

    public Account getAccountId() {
        return accountId;
    }

    public void setAccountId(Account accountId) {
        this.accountId = accountId;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
