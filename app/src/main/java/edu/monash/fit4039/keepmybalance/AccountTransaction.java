package edu.monash.fit4039.keepmybalance;

import java.util.Date;

/**
 * Created by nathan on 10/6/17.
 */

public class AccountTransaction {
    private int transactionId = -1;
    private Account inputAccountId;
    private Account outputAccountId;
    private double amount;
    private Date issueTime;

    public AccountTransaction() {
    }

    //non-default constructor
    //param: input account id (account object), output account id (account object), amount, issue time
    public AccountTransaction(Account inputAccountId, Account outputAccountId, double amount, Date issueTime) {
        this.inputAccountId = inputAccountId;
        this.outputAccountId = outputAccountId;
        this.amount = amount;
        this.issueTime = issueTime;
    }

    //The rest methods are getter and setter of attribute in this class
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public Account getInputAccountId() {
        return inputAccountId;
    }

    public void setInputAccountId(Account inputAccountId) {
        this.inputAccountId = inputAccountId;
    }

    public Account getOutputAccountId() {
        return outputAccountId;
    }

    public void setOutputAccountId(Account outputAccountId) {
        this.outputAccountId = outputAccountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }
}
