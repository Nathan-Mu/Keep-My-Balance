package edu.monash.fit4039.keepmybalance;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static edu.monash.fit4039.keepmybalance.JSONReader.*;
import static edu.monash.fit4039.keepmybalance.KMBConstant.*;


/**
 * Created by nathan on 9/5/17.
 */

//resources: http://moodle.vle.monash.edu/pluginfile.php/5474353/mod_resource/content/1/FIT5046-Tute8-HttpURLConnection-2017.pdf
public class RestClient {
    private static final String BASE_URI = "http://"+ IP_ADDRESS + ":8080/FIT4039DB/webresources";

    //return a JSONObject of user if username and password matches on server side
    public static JSONObject getLoginInfo(String username, String password) {
        final String methodPath = "/restclient.userinfo/checkUserExist/" + username + "/" + password;
        String URLString = BASE_URI + methodPath;
        String textResult = getMethod(URLString, JSON_TYPE);
        return toJSONObject(textResult);
    }

    //return monthly fundchanges
    public static List<JSONObject> getMonthlyFundChange(int month, int year, int userId)
    {
        final String methodPath = "/restclient.fundchange/findMonthlyChangeOrderByDate/" + month + "/" + year + "/" + userId;
        String URLString = BASE_URI + methodPath;
        String textResult = getMethod(URLString, JSON_TYPE);
        return toList(textResult);
    }

    //return monthly expense rate (used in report)
    public static List<JSONObject> getMonthlyExpenseRate(int month, int year, int userId) {
        final String methodPath = "/restclient.fundchange/findMonthlyExpenseRate/" + month + "/" + year + "/" + userId;
        String URLString = BASE_URI + methodPath;
        String textResult = getMethod(URLString, JSON_TYPE);
        return toList(textResult);
    }

    //return all parent categories of a user
    public static List<JSONObject> getAllParentCategories(int userId) {
        final String methodPath = "/restclient.parentcategory/findAllParentCategories/" + userId;
        String URLString = BASE_URI + methodPath;
        String textResult = getMethod(URLString, JSON_TYPE);
        return toList(textResult);
    }

    //return all parent categories belonged to expense of a user
    public static List<JSONObject> getExpenseParentCategories(int userId) {
        final String methodPath = "/restclient.parentcategory/findExpenseParentCategories/" + userId;
        String URLString = BASE_URI + methodPath;
        String textResult = getMethod(URLString, JSON_TYPE);
        return toList(textResult);
    }

    //get all parent categories belonged to income of a user
    public static List<JSONObject> getIncomeParentCategories(int userId) {
        final String methodPath = "/restclient.parentcategory/findIncomeParentCategories/" + userId;
        String URLString = BASE_URI + methodPath;
        String textResult = getMethod(URLString, JSON_TYPE);
        return toList(textResult);
    }

    //get all child categories of a parent category of a user
    public static List<JSONObject> getAllChildCategoriesFromOneParentCategory(int userId, int parentCategoryId) {
        final String methodPath = "/restclient.childcategory/findAllChildCategoriesFromOneParentCategory/" + userId + "/" + parentCategoryId;
        String URLString = BASE_URI + methodPath;
        String textResult = getMethod(URLString, JSON_TYPE);
        return toList(textResult);
    }

    //check if user name exists
    public static boolean isUsernameExist(String username)
    {
        final String methodPath = "/restclient.userinfo/checkUsernameExist/" + username;
        String URLString = BASE_URI + methodPath;
        String textResult = getMethod(URLString, JSON_TYPE);
        return Boolean.valueOf(textResult);
    }

    //return a JSONObject of user if user id and password matches on server side
    public static JSONObject getAutoLogin(String userId, String password) {
        final String methodPath = "/restclient.userinfo/checkAutoLogin/" + userId + "/" + password;
        String URLString = BASE_URI + methodPath;
        String textResult = getMethod(URLString, JSON_TYPE);
        return toJSONObject(textResult);
    }

    //return home page static (incomes and expenses)
    public static double[] getHomePageStatics(int userId)
    {
        final String methodPath = "/restclient.fundchange/findHomePageStatics/" + userId;
        String URLString = BASE_URI + methodPath;
        String textResult = getMethod(URLString, TEXT_TYPE);
        JSONObject json = toJSONObject(textResult);
        double[] array = new double[6];
        for (int i = 0; i < HOME_PAGE_STATICS_SEQ.length; i++) {
            array[i] = getDoubleFromJSONObject(json, HOME_PAGE_STATICS_SEQ[i]);
        }
        return array;
    }

    //get all accounts of a user
    public static List<JSONObject> getAllAccounts(int userId)
    {
        final String methodPath = "/restclient.account/findAllAccounts/" + userId;
        String URLString = BASE_URI + methodPath;
        String textResult = getMethod(URLString, JSON_TYPE);
        return toList(textResult);
    }

    //check if the account is removable
    public static boolean isAccountRemovable(int accountId) {
        final String methodPath = "/restclient.fundchange/isAccountRemovable/" + accountId;
        String URLString = BASE_URI + methodPath;
        String textResult = getMethod(URLString, TEXT_TYPE);
        return Boolean.valueOf(textResult);
    }

    //check if the child category is removable
    public static boolean isChildCategoryRemovable(int childCategoryId) {
        final String methodPath = "/restclient.fundchange/isChildCategoryRemovable/" + childCategoryId;
        String URLString = BASE_URI + methodPath;
        String textResult = getMethod(URLString, TEXT_TYPE);
        return Boolean.valueOf(textResult);
    }

    //HTTP GET method
    private static String getMethod(String URLString, String acceptType) {
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try {
            url = new URL(URLString);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", acceptType);
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textResult;
    }

    //create a new user
    //return true if the user is created successfully
    public static boolean createUser(UserInfo user) {
        final String methodPath = "/restclient.userinfo/";
        return postMethod(methodPath, user);
    }

    //create a fundchange
    //return true if the fund change is created successfully
    public static boolean createFundChange(FundChange fundChange) {
        final String methodPath = "/restclient.fundchange/";
        return postMethod(methodPath, fundChange);
    }

    //create an account
    //return true if the account is created successfully
    public static boolean createAccount(Account account) {
        final String methodPath = "/restclient.account/";
        return postMethod(methodPath, account);
    }

    //create a child category
    //return true if the child category is created successfully
    public static boolean createChildCategory(ChildCategory childCategory) {
        final String methodPath = "/restclient.childcategory";
        return postMethod(methodPath, childCategory);
    }

    //create a parent category
    //return true if the parent category is created successfully
    public static boolean createParentCategory(ParentCategory parentCategory) {
        final String methodPath = "/restclient.parentcategory";
        return postMethod(methodPath, parentCategory);
    }

    //create a transaction
    //return true if the transaction is created successfully
    public static boolean createTransaction(AccountTransaction transaction) {
        final String methodPath = "/restclient.accounttransaction";
        return postMethod(methodPath, transaction);
    }

    //HTTP POST method
    private static boolean postMethod(String methodPath, Object object) {
        URL url = null;
        int responseCode = 400;
        HttpURLConnection conn = null;
        try {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ").create();
            String stringJson = gson.toJson(object);
            url = new URL(BASE_URI + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(stringJson.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(stringJson);
            out.close();
            responseCode = conn.getResponseCode();
            Log.i("error", new Integer(responseCode).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        if (responseCode < 400)
            return true;
        else
            return false;
    }

    //transfer string to a list of JSONObjects
    private static List<JSONObject> toList(String textResult) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        JSONArray jsonArray = toJSONArray(textResult);
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObjects.add(getJSONObjectFromArray(jsonArray, i));
        }
        return jsonObjects;
    }

    //HTTP DELETE method
    private static boolean deleteMethod(String methodPath) {
        URL url = null;
        int responseCode = 400;
        HttpURLConnection conn = null;
        try {
            url = new URL(BASE_URI + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            responseCode = conn.getResponseCode();
            Log.i("error", new Integer(responseCode).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        if (responseCode < 400)
            return true;
        else
            return false;
    }

    //delete an account
    //return true if the account is deleted
    public static boolean deleteAccount(int accountId) {
        final String methodPath = "/restclient.account/" + accountId;
        return deleteMethod(methodPath);
    }

    //delete a fundchange
    //return true if the fundchange is deleted
    public static boolean deleteFundChange(int fundChangeId) {
        final String methodPath = "/restclient.fundchange/" + fundChangeId;
        return deleteMethod(methodPath);
    }

    //delete a parent category
    //return true if the parent category is deleted
    public static boolean deleteParentCategory(int parentCategoryId) {
        final String methodPath = "/restclient.parentcategory/" + parentCategoryId;
        return deleteMethod(methodPath);
    }

    //delete a child category
    //return true if the child category is deleted
    public static boolean deleteChildCategory(int childCategoryId) {
        final String methodPath = "/restclient.childcategory/" + childCategoryId;
        return deleteMethod(methodPath);
    }

    //HTTP PUT method
    public static boolean putMethod(String methodPath, Object object) {
        URL url = null;
        int responseCode = 400;
        HttpURLConnection conn = null;
        try {
            Gson gson = new Gson();
            String stringJson = gson.toJson(object);
            url = new URL(BASE_URI + methodPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(stringJson.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(stringJson);
            out.flush();
            out.close();
            responseCode = conn.getResponseCode();
            Log.i("error", new Integer(responseCode).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        if (responseCode < 400)
            return true;
        else
            return false;
    }

    //update user info
    //return true if updated successfully
    public static boolean updateUser(UserInfo user) {
        final String methodPath = "/restclient.userinfo/" + user.getUserId();
        return putMethod(methodPath, user);
    }

    //update account info
    //return true if updated successfully
    public static boolean updateAccount(Account account) {
        final String methodPath = "/restclient.account/" + account.getAccountId();
        return putMethod(methodPath, account);
    }

    //check if the account exists
    public static boolean isAccountExist(UserInfo user, Account newAccount) {
        List<JSONObject> list = getAllAccounts(user.getUserId());
        for (JSONObject jsonObject: list) {
            Account account = new Account(jsonObject);
            if (account.getAccountType().equalsIgnoreCase(newAccount.getAccountType())) {
                return true;
            }
        }
        return false;
    }
}
