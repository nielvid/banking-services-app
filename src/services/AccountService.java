package services;

import model.Account;
import model.AccountType;
import model.User;

public interface AccountService {
      public Account openAccount(User user, String accountType);
      public void makeDeposit(Account account, double amount);
//    public void withdraw(double amount);
//    public void transfer(double amount, Account account);
//    public void viewBalance();
//    public void viewTransactionHistory();
//    public void closeAccount();
}
