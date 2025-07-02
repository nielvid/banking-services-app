package com.nielvid.entities;

import java.math.BigDecimal;
import java.util.Random;

import com.nielvid.model.AccountStatus;
import com.nielvid.model.AccountType;



public class Account {

    private String userId;
    private String accountNumber;
    private String accountName;
    private BigDecimal balance;
    private AccountType accountType;
    private AccountStatus status;
    private String pin;

    public Account() {
    }

    public Account(User user, AccountType accountType) {
        this.userId = user.getClientId();
        this.accountNumber = String.valueOf(1000000000L + (long) (new Random().nextDouble() * (9999999999L - 1000000000L)));
        this.accountName = user.getFirstName() + " " + user.getLastName();
        this.balance = BigDecimal.valueOf(0.00);
        this.accountType = accountType;
        this.status = AccountStatus.ACTIVE;
    }
     public Account(User user, String accountNumber) {
        this.userId = user.getClientId();
        this.accountNumber = accountNumber;
        this.accountName = user.getFirstName() + " " + user.getLastName();
    }

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        String masked = "****";
        this.pin = pin + masked;
    }

}
