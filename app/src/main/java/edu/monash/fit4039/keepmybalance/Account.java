package edu.monash.fit4039.keepmybalance;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import static edu.monash.fit4039.keepmybalance.JSONReader.getDoubleFromJSONObject;
import static edu.monash.fit4039.keepmybalance.JSONReader.getJSONObjectFromJSONObject;
import static edu.monash.fit4039.keepmybalance.JSONReader.getStringFromJSONObject;
import static edu.monash.fit4039.keepmybalance.JSONReader.getIntegerFromJSONObject;

/**
 * Created by nathan on 16/5/17.
 */

public class Account implements Parcelable{
    private int accountId = -1;
    private String accountType;
    private UserInfo userId;
    private double balance;

    public Account() {
    }

    //non-default constructor
    //param: account type, user id (user object), balance
    public Account(String accountType, UserInfo userId, double balance) {
        this.accountId = 1;
        this.accountType = accountType;
        this.userId = userId;
        this.balance = balance;
    }

    //non-default constructor
    //param: json object (received from server)
    public Account(JSONObject json)
    {
        this.accountId = getIntegerFromJSONObject(json, "accountId");
        this.accountType = getStringFromJSONObject(json, "accountType");
        this.userId = new UserInfo(getJSONObjectFromJSONObject(json, "userId"));
        this.balance = getDoubleFromJSONObject(json, "balance");
    }

    //non-default constructor (Parcelable interface)
    public Account(Parcel in)
    {
        this.accountId = in.readInt();
        this.accountType = in.readString();
        this.userId = (UserInfo)in.readValue(UserInfo.class.getClassLoader());
        this.balance = in.readDouble();
    }

    //Parcelable interface method
    @Override
    public int describeContents() {
        return 0;
    }

    //Parcelable interface method
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(accountId);
        parcel.writeString(accountType);
        parcel.writeValue(userId);
        parcel.writeDouble(balance);
    }

    //Parcelable interface method
    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    //return account id
    public int getAccountId() {
        return accountId;
    }

    //set account id
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    //return account type
    public String getAccountType() {
        return accountType;
    }

    //set account type
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    //return user info
    public UserInfo getUserInfo() {
        return userId;
    }

    //set user info
    public void setUserInfo(UserInfo userId) {
        this.userId = userId;
    }

    //return balance
    public double getBalance() {
        return balance;
    }

    //set balance
    public void setBalance(double balance) {
        this.balance = balance;
    }
}
