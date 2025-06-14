package model;

import java.math.BigDecimal;
import java.util.Random;

public class Account {

    private User user;
    private long accountNumber;
    private String accountName;
    private BigDecimal balance;
    private AccountType accountType;
    private AccountStatus status;


    public Account() {
    }

    public Account( User user, AccountType accountType) {
        this.user = user;
        this.accountNumber = new Random().nextLong(1000000000, 9999999999L) ;
        this.accountName = user.getFirstName() + " " + user.getLastName();
        this.balance = BigDecimal.valueOf(0.00);
        this.accountType = accountType;
        this.status = AccountStatus.ACTIVE;
    }


    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
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



}
