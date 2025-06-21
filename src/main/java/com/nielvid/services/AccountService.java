package com.nielvid.services;

import com.nielvid.entities.Account;
import com.nielvid.entities.User;

import java.math.BigDecimal;

public interface AccountService {
      public Account openAccount(User user, String accountType);
      public void makeDeposit(int accountNumber, double amount);
      public void withdrawFund(int accountNumber, double amount);
      public void transferFund(int accountNumber, double amount);
      public BigDecimal viewBalance(int accountNumber);
//    public void viewTransactionHistory();
//    public void closeAccount();
}
