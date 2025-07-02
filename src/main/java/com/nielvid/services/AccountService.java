package com.nielvid.services;

import com.nielvid.entities.Account;
import com.nielvid.entities.User;

import java.math.BigDecimal;

public interface AccountService {
      public Account openAccount(User user, String accountType);
      public void makeDeposit(long accountNumber, double amount);
      public void withdrawFund(long accountNumber, double amount);
      public void transferFund(long accountNumber, double amount);
      public BigDecimal viewBalance(long accountNumber);
//    public void viewTransactionHistory();
//    public void closeAccount();
}
