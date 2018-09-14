package edu.monash.fit4039.keepmybalance;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by nathan on 22/5/17.
 */
public class AccountTest {
    @Before
    public void setUp() throws Exception {
        UserInfo userInfo = new UserInfo("username", "password");
        Account account = new Account("Account", userInfo);
    }

    @Test
    public void getAccountId() throws Exception {
        UserInfo userInfo = new UserInfo("username", "password");
        Account account = new Account("Account", userInfo);
        Assert.assertEquals(account.getAccountId(), 1);
    }

    @Test
    public void setAccountId() throws Exception {
        UserInfo userInfo = new UserInfo("username", "password");
        Account account = new Account("Account", userInfo);
        account.setAccountId(1);
    }

    @Test
    public void getAccountType() throws Exception {
        UserInfo userInfo = new UserInfo("username", "password");
        Account account = new Account("Account", userInfo);
        Assert.assertEquals(account.getAccountType(), "Account");
    }

    @Test
    public void setAccountType() throws Exception {
        UserInfo userInfo = new UserInfo("username", "password");
        Account account = new Account("Account", userInfo);
        account.setAccountType("type");
    }

    @Test
    public void getUserInfo() throws Exception {
        UserInfo userInfo = new UserInfo("username", "password");
        Account account = new Account("Account", userInfo);
        Assert.assertEquals(account.getUserInfo(), userInfo);
    }

    @Test
    public void setUserInfo() throws Exception {
        UserInfo userInfo = new UserInfo("username", "password");
        Account account = new Account("Account", userInfo);
        UserInfo userInfo2 = new UserInfo("username", "password");
        account.setUserInfo(userInfo2);
    }

}