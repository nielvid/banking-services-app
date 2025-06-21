package com.nielvid.services.impl;  

import com.nielvid.services.AccountService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.nielvid.entities.Account;
import com.nielvid.entities.User;
import com.nielvid.model.AccountType;

public class AccountServiceImpl implements AccountService {
    private final String PIN_OK = "OK";
    private final String INVALID_PIN = "Invalid PIN. Please try again.";
    private final String INVALID_ACCOUNT_NUMBER = "Invalid account number. Please try again.";
    private final String INSUFFICIENT_FUNDS = "Insufficient funds. Please try again.";
    private final String INVALID_PIN_LENGTH_OR_FORMAT = "Invalid PIN. Must be exactly 4 digits.";

    private final int MAX_PIN_ATTEMPTS = 3;

    private final UserServiceImpl userService;
    public AccountServiceImpl(){
       this.userService = new UserServiceImpl();
    }

    Scanner scanner  = new Scanner(System.in);

    public void accountOpeningOperation() {
        User user = userService.createUser();
        System.out.println("Enter the type of account you want to create: ");
        String accountType = scanner.next();
        Account account = openAccount(user, accountType);

        System.out.println("Set Transaction PIN for your account: ");
        System.out.println("Enter 4-digits PIN: ");
        String pin = scanner.nextLine();
        setAccountPIN(account, pin);
        System.out.println("Fund your account with a minimum of $100");
        System.out.println("Enter the amount you want to deposit: ");
        double amount = scanner.nextDouble();
        initialDeposit(account, amount);
        doSomethingElse();
    }


    public void deposit() {
        System.out.println("Enter your account number: ");
        int accountNumber = scanner.nextInt();
       if(!validateAccountOwnership(accountNumber)){
           System.exit(1);
       }
        System.out.println("Enter the amount you want to deposit: ");
        double amount = scanner.nextDouble();
        makeDeposit(accountNumber, amount);
        doSomethingElse();
    }


    public void withdraw() {
        System.out.println("Enter your account number: ");
        int accountNumber = scanner.nextInt();
        if(!validateAccountOwnership(accountNumber)){
            System.exit(1);
        }
        System.out.println("Enter the amount you want to withdraw: ");
        double amount = scanner.nextDouble();
        withdrawFund(accountNumber, amount);
        doSomethingElse();
    }

    public void checkBalance() {
        System.out.println("Enter your account number: ");
        int accountNumber = scanner.nextInt();
        viewBalance(accountNumber);
        doSomethingElse();
    }

    public void transfer() {
        System.out.println("Enter your account number: ");
        int accountNumber = scanner.nextInt();
        if(!validateAccountOwnership(accountNumber)){
            System.exit(1);
        }
        System.out.println("Enter the amount you want to transfer: ");
        double amount = scanner.nextDouble();
        transferFund(accountNumber, amount);
        doSomethingElse();
    }


    @Override
    public Account openAccount(User user, String accountTypeString) {
        boolean isValidAccountType = isValidAccountType(accountTypeString);
        if (!isValidAccountType) {
            System.out.println("Invalid account type");
            System.exit(1);
        }

        AccountType accountType = getAccountType(accountTypeString.toUpperCase());
        if (accountType == null) {
            List<AccountType> accountTypes = Arrays.asList(AccountType.values());
            System.out.println("the accountType is invalid, it should be any of : " +  accountTypes );
            System.exit(1);
        }
        Account account = new Account(user, accountType);
        accountDB.add(account);
        System.out.println("Account created successfully!");
        System.out.println("Your account name is: \n  " + account.getAccountName());
        System.out.println("Your account number is: \n  " + account.getAccountNumber());
        return account;
    }

    public void initialDeposit(Account account, double amount) {
        while (amount <= 100) {
            System.out.println("Invalid amount. Please enter amount greater than $100.");
            System.out.println("Enter the amount you want to deposit: ");
            amount = scanner.nextDouble();
        }
        BigDecimal accountBalance = account.getBalance().add(BigDecimal.valueOf(amount));
        account.setBalance(accountBalance);
        System.out.printf("%.2f deposited successfully into your account %s%n",amount, account.getAccountNumber());
    }

    @Override
    public void makeDeposit(int accountNumber, double amount) {
        while (amount <= 0) {
            System.out.println("Invalid amount. Please enter a positive amount.");
            System.out.println("Enter the amount you want to deposit: ");
            amount = scanner.nextDouble();
        }
        Account account = getAccountByAccountNumber(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }
        BigDecimal accountBalance = account.getBalance().add(BigDecimal.valueOf(amount));
        account.setBalance(accountBalance);
        System.out.printf("%.2f deposited successfully into your account %s%n",amount, account.getAccountNumber());
    }


    @Override
    public void withdrawFund(int accountNumber, double amount) {
        while (amount <= 0) {
            System.out.println("Invalid amount. Please enter a positive amount.");
            System.out.println("Enter the amount you want to withdraw: ");
            amount = scanner.nextDouble();
        }

        System.out.println("Enter your PIN: ");
        String pin = scanner.nextLine();
       if(!validatePin(pin).equals(PIN_OK)){
           System.out.println(INVALID_PIN);
           System.exit(1);
       }

        Account account = getAccountByAccountNumber(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }
        String accountPin = account.getPin().substring( 0,4);

        if (accountPin.equals(pin)) {
            System.out.println("PIN is correct.");
        } else {
            System.out.println(INVALID_PIN);
            System.exit(1);
        }

        if( account.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0){
            System.out.println("Insufficient funds.");
            return;
        }

        if( account.getBalance().compareTo(BigDecimal.valueOf(amount)) == 0){
            System.out.println("You can't withdraw all your money. Please leave some for future needs");
            return;
        }
        BigDecimal accountBalance = account.getBalance().subtract(BigDecimal.valueOf(amount));
        account.setBalance(accountBalance);
        System.out.printf("%.2f withdrawn successfully %s%n",amount);
    }


    @Override
    public BigDecimal viewBalance(int accountNumber) {

        System.out.println("Enter your PIN: ");
        String pin = scanner.nextLine();
        if(!validatePin(pin).equals(PIN_OK)){
            System.out.println(INVALID_PIN);
            System.exit(1);
        }

        Account account = getAccountByAccountNumber(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return null;
        }
        String accountPin = account.getPin().substring( 0,4);

        if (accountPin.equals(pin)) {
            System.out.println("PIN is correct.");
        } else {
            System.out.println(INVALID_PIN);
            System.exit(1);
        }

        return account.getBalance();
    }


    @Override
    public void transferFund(int accountNumber, double amount) {
        while (amount <= 0) {
            System.out.println("Invalid amount. Please enter a positive amount.");
            System.out.println("Enter the amount you want to withdraw: ");
            amount = scanner.nextDouble();
        }
        BigDecimal bigDecimalAmount = BigDecimal.valueOf(amount);
        Account sourceAccount = getAccountByAccountNumber(accountNumber);
        if (sourceAccount == null) {
            System.out.println("Account not found.");
            return;
        }
        if( sourceAccount.getBalance().compareTo(bigDecimalAmount) < 0){
            System.out.println("Insufficient funds.");
            return;
        }

        if( sourceAccount.getBalance().compareTo(bigDecimalAmount) == 0){
            System.out.println("You can't transfer all your money. Please leave some for future needs");
            return;
        }
        System.out.println("Enter the beneficiary account number: ");
        int beneficiaryAccountNumber = scanner.nextInt();
        Account recipientAccount = getAccountByAccountNumber(beneficiaryAccountNumber);
        if (recipientAccount == null) {
            System.out.println("beneficiary account number not found.");
            return;
        }
        // Lock both accounts to ensure thread-safety during transfer
        synchronized (sourceAccount) {
            synchronized (recipientAccount) {
                // Confirm the transaction
                confirmTransaction(sourceAccount);

                // Perform the transfer
                sourceAccount.setBalance(sourceAccount.getBalance().subtract(bigDecimalAmount));
                recipientAccount.setBalance(recipientAccount.getBalance().add(bigDecimalAmount));

                // Provide confirmation to the user
                System.out.printf("%.2f transferred to account %d.%n", amount, beneficiaryAccountNumber);
                System.out.printf("Your new balance is %.2f.%n", sourceAccount.getBalance());
            }
        }
    }

    public static boolean isValidAccountType(String accountTypeString) {
        if (accountTypeString == null) {
            return false;
        }

        return Arrays.stream(AccountType.values())
                .anyMatch(type -> type.name().equals(accountTypeString.toUpperCase().trim()));
    }

    public AccountType getAccountType(String accountTypeString) {
        if (accountTypeString == null || accountTypeString.trim().isEmpty()) {
            return null;
        }
        try {
            return AccountType.valueOf(accountTypeString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

   public void promptUserForIntendedAction(){
        System.out.println("what would you like to do?");
        System.out.println("1. Create a new account");
        System.out.println("2. Deposit money");
        System.out.println("3. Withdraw money");
        System.out.println("4. Transfer money");
        System.out.println("5. View account balance");
        System.out.println("6. View transaction history");
        System.out.println("7. Exit");

        System.out.println("Enter the number that matches what want to do: ");
        List<Integer> possibleChoices = Arrays.asList(1,2,3,4,5,6,7);

        Scanner scanner  = new Scanner(System.in);
        int choice = scanner.nextInt();

        while(!possibleChoices.contains(choice)){
            System.out.println("Invalid choice. Please try again.");
            System.out.println("Enter the number that matches what want to do: ");
            choice = scanner.nextInt();
        }
        scanner.close();
        System.out.println("You have selected option: " + choice);
        if(choice == 1){
            accountOpeningOperation();
        }
        if(choice == 2){
            deposit();
        }
        if(choice == 3){
            withdraw();
        }
        if(choice == 4){
            transfer();
        }
        if(choice == 5){
            checkBalance();
        }
        if(choice == 6){
            System.exit(0);
        }

        if(choice == 7){
            System.out.println("Thank you for choosing Niel Digital Bank!");
            System.out.println("Wait while log you out...");
           try {
               Thread.sleep(2000);
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
            System.out.println("You have been logged out successfully!");
            System.exit(0);
        }
    }

    private void doSomethingElse(){
        System.out.println("Would you like to do something else? (y/n)");
        String answer = scanner.next();
        if (answer.equalsIgnoreCase("y")) {
            promptUserForIntendedAction();
        } else {
            System.out.println("Thank you for using Niel Digital Bank!");
            System.exit(0);
        }
    }

    public Account getAccountByAccountNumber(int accountNumber) {
      return accountDB.stream()
                .filter(account -> account.getAccountNumber() == accountNumber)
                .findFirst()
                .orElse(null);
    }

    public void setAccountPIN(Account account, String pin) {
        if(!validatePin(pin).equals(PIN_OK)){
            System.out.println(INVALID_PIN);
            System.out.println("PIN not set. Please try again");
        }
        if(validatePin(pin).equals(PIN_OK)){
            account.setPin(pin);
            System.out.println("PIN successfully set!");
        }
    }



    private boolean validateAccountOwnership(int accountNumber) {

        System.out.println("Enter your PIN: ");
        String pin = scanner.nextLine();
        if(!validatePin(pin).equals(PIN_OK)){
            System.out.println(INVALID_PIN);
            System.exit(1);
        }

        Account account = getAccountByAccountNumber(accountNumber);
        if (account == null) {
            System.out.println("Account not found.");
            return false;
        }
        String accountPin = account.getPin().substring( 0,4);

        if (accountPin.equals(pin)) {
            System.out.println("PIN is correct.");
        } else {
            System.out.println(INVALID_PIN);
            System.exit(1);
        }

        return true;
    }

    public void confirmTransaction(Account account){
        int pinAttempts = 0;
        String pin = null;
        boolean pinOk = false;
        while (pinAttempts < MAX_PIN_ATTEMPTS){
            System.out.println("Enter your 4-digit PIN: ");
            pin = scanner.nextLine();
            if(!validatePin(pin).equals(PIN_OK)){
                System.out.println(INVALID_PIN);
                pinAttempts++;
            }else{
                String accountPin = account.getPin().substring( 0,4);
                if (accountPin.equals(pin)) {
                    System.out.println("PIN is correct.");
                    pinOk = true;
                    break;
                } else {
                    System.out.println(INVALID_PIN);
                    pinAttempts++;
                }
            }

        }
        if(!pinOk){
            System.out.println("Maximum PIN attempts reached. Exiting...");
            System.exit(1);
        }

    }

    public String validatePin(String pin){
        if (pin.length() == 4 && pin.matches("\\d{4}")) {
            System.out.println("PIN OK" );
        } else {
            System.out.println(INVALID_PIN_LENGTH_OR_FORMAT);
        }
        return PIN_OK;
    }

}
