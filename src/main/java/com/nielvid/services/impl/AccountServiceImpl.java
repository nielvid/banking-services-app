package com.nielvid.services.impl;  

import com.nielvid.dao.AccountDaoImpl;
import com.nielvid.services.AccountService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.nielvid.entities.Account;
import com.nielvid.entities.User;
import com.nielvid.model.AccountType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountServiceImpl implements AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final String PIN_OK = "OK";
    private final String INVALID_PIN = "Invalid PIN. Please try again.";
    private final String INVALID_ACCOUNT_NUMBER = "Invalid account number. Please try again.";
    private final String INSUFFICIENT_FUNDS = "Insufficient funds. Please try again.";
    private final String INVALID_PIN_LENGTH_OR_FORMAT = "Invalid PIN. Must be exactly 4 digits.";


    private final UserServiceImpl userService;
    public AccountServiceImpl(){
       this.userService = new UserServiceImpl();
    }
    private final AccountDaoImpl accountDaoImpl = new AccountDaoImpl();


    public void openingAccount() {
    try{
        Scanner scanner  = new Scanner(System.in);
        User user = userService.createUser();
        displayAccountType();
        System.out.println("Enter the type of account you want to open: ");
        String accountType = scanner.next();
        Account account = openAccount(user, accountType);
        setAccountPIN(account.getAccountNumber());
        System.out.println("Fund your account with a minimum of $100");
        System.out.println("Enter the amount you want to deposit: ");
        double amount = scanner.nextDouble();
        initialDeposit(account, amount);
        doSomethingElse();
    } catch (RuntimeException e) {
        throw new RuntimeException(e);
    }
    }


    private void displayAccountType(){
        List<AccountType> accountTypes = Arrays.asList(AccountType.values());
        System.out.println("Account Types " +  accountTypes );
    }
    public void setAccountPIN(String accountNumber){
        try{
            Scanner scanner  = new Scanner(System.in);
            System.out.println("Set Transaction PIN for your account: ");
            System.out.println("Enter 4-digits PIN: ");
            String pin = scanner.nextLine();
            if (pin.length() == 4 && pin.matches("\\d{4}")) {
                System.out.println("PIN OK" );
            } else {
                System.out.println(INVALID_PIN_LENGTH_OR_FORMAT);
            }
            int count = accountDaoImpl.updateAccountPIN(accountNumber, maskPin(pin));
            if(count > 0){
                System.out.println("PIN set successfully");
            }else{
                System.out.println("PIN not set");
            }
        }
        catch (Exception e){
            throw  new RuntimeException(e.getMessage());
        }
    }

    public void deposit() {
        Scanner scanner  = new Scanner(System.in);
        System.out.println("Enter your account number: ");
        long accountNumber = scanner.nextLong();
       if(validateAccountOwnership(accountNumber)){
           System.exit(1);
       }
        System.out.println("Enter the amount you want to deposit: ");
        double amount = scanner.nextDouble();
        makeDeposit(accountNumber, amount);
        doSomethingElse();
    }


    public void withdraw() {
        Scanner scanner  = new Scanner(System.in);
        System.out.println("Enter your account number: ");
        long accountNumber = scanner.nextLong();
        if(validateAccountOwnership(accountNumber)){
            System.exit(1);
        }
        System.out.println("Enter the amount you want to withdraw: ");
        double amount = scanner.nextDouble();
        withdrawFund(accountNumber, amount);
        doSomethingElse();
    }

    public void checkBalance() {
        Scanner scanner  = new Scanner(System.in);
        System.out.println("Enter your account number: ");
        long accountNumber = scanner.nextLong();
        BigDecimal acctBalance =  viewBalance(accountNumber);
        log.info("Your account balance is  : {}", acctBalance);
        doSomethingElse();
    }

    public void transfer() {
        Scanner scanner  = new Scanner(System.in);
        System.out.println("Enter your account number: ");
        long accountNumber = scanner.nextLong();
        if(validateAccountOwnership(accountNumber)){
            System.exit(1);
        }
        System.out.println("Enter the amount you want to transfer: ");
        double amount = scanner.nextDouble();
        transferFund(accountNumber, amount);
        doSomethingElse();
    }


    @Override
    public Account openAccount(User user, String accountTypeString) {

        boolean userExist = userService.userExist(user);
        if(userExist){
            System.out.println("Account already exist for this user");
            doSomethingElse();
        }
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

       try{
           Account account = accountDaoImpl.create(user, accountType);
           System.out.println("Account created successfully!");
           System.out.println("Your account name is: \n  " + account.getAccountName());
           System.out.println("Your account number is: \n  " + account.getAccountNumber());
           return account;
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }

    public void initialDeposit(Account account, double amount) {
        while (amount < 100) {
            Scanner scanner  = new Scanner(System.in);
            System.out.println("Invalid amount. Minimum initial deposit cannot be less than $100.");
            System.out.println("Enter the amount you want to deposit: ");
            amount = scanner.nextDouble();
        }
        accountDaoImpl.depositFund(account.getAccountNumber(), amount);
        System.out.printf("%.2f deposited successfully into your account %s%n",amount, account.getAccountNumber());
    }

    @Override
    public void makeDeposit(long accountNumber, double amount) {
        while (amount <= 0) {
            Scanner scanner  = new Scanner(System.in);
            System.out.println("Invalid amount. Please enter a positive amount.");
            System.out.println("Enter the amount you want to deposit: ");
            amount = scanner.nextDouble();
        }
        String acctNumber = String.valueOf(accountNumber);
        accountDaoImpl.depositFund(acctNumber, amount);
        System.out.printf("%.2f deposited successfully into your account %s%n",amount, accountNumber);
    }


    @Override
    public void withdrawFund(long accountNumber, double amount) {
        Scanner scanner  = new Scanner(System.in);
        while (amount <= 0) {

            System.out.println("Invalid amount. Please enter a positive amount.");
            System.out.println("Enter the amount you want to withdraw: ");
            amount = scanner.nextDouble();
        }

        Optional<Account> account = accountDaoImpl.findByAccountNumber(String.valueOf(accountNumber));
        if (account.isEmpty()) {
            System.out.println(INVALID_ACCOUNT_NUMBER);
            return;
        }
        Account acct = account.get();
        confirmTransactionWithPIN(acct);

        if( acct.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0){
            System.out.println(INSUFFICIENT_FUNDS);
            return;
        }

        if( acct.getBalance().compareTo(BigDecimal.valueOf(amount)) == 0){
            System.out.println("You can't withdraw all your money. Please leave some for future needs");
            return;
        }
        accountDaoImpl.withdrawFund(String.valueOf(accountNumber), amount);
    }


    @Override
    public BigDecimal viewBalance(long accountNumber) {
        Optional<Account> account = accountDaoImpl.findByAccountNumber(String.valueOf(accountNumber));
        if (account.isEmpty()) {
            System.out.println(INVALID_ACCOUNT_NUMBER);
            System.exit(1);
        }
        Account acct = account.get();
        confirmTransactionWithPIN(acct);

        return acct.getBalance();
    }


    @Override
    public void transferFund(long accountNumber, double amount) {
        Scanner scanner  = new Scanner(System.in);
        while (amount <= 0) {
            System.out.println("Invalid amount. Please enter a positive amount.");
            System.out.println("Enter the amount you want to withdraw: ");
            amount = scanner.nextDouble();
        }
        BigDecimal bigDecimalAmount = BigDecimal.valueOf(amount);
        Optional<Account> account = accountDaoImpl.findByAccountNumber(String.valueOf(accountNumber));
        if (account.isEmpty()) {
            System.out.println(INVALID_ACCOUNT_NUMBER);
            System.exit(1);
        }
        Account sAcct = account.get();

        if( sAcct.getBalance().compareTo(bigDecimalAmount) < 0){
            System.out.println(INSUFFICIENT_FUNDS);
            return;
        }

        if( sAcct.getBalance().compareTo(bigDecimalAmount) == 0){
            System.out.println("You can't transfer all your money. Please leave some for future needs");
            return;
        }
        System.out.println("Enter the beneficiary account number: ");
        long beneficiaryAccountNumber = scanner.nextLong();
        Optional<Account> recipientAccount = accountDaoImpl.findByAccountNumber(String.valueOf(beneficiaryAccountNumber));

        if (recipientAccount.isEmpty()) {
            System.out.println("Invalid recipient account number");
            System.exit(1);
        }
        Account rAcct = recipientAccount.get();

        confirmTransactionWithPIN(sAcct);
        int count = accountDaoImpl.fundTransfer(sAcct, rAcct, amount);
        if(count == 2){
            System.out.printf("%.2f transferred to account %d.%n", amount, beneficiaryAccountNumber);
        }else{
            log.info("something went wrong");
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
        System.out.println("What would you like to do?");
        System.out.println("Enter the number that matches what you would like to do: ");
        System.out.print("1. Open a new account ");
        System.out.print("     *********************************  ");
        System.out.print("2. Deposit fund ");
        System.out.println(" ");
        System.out.println(" ");
        System.out.print("3. Withdraw fund ");
        System.out.print("          *********************************  ");
        System.out.print("4. Transfer fund ");
        System.out.println(" ");
        System.out.println(" ");
        System.out.print("5. View account balance ");
        System.out.print("   *********************************  ");
        System.out.print("6. View transaction history ");
        System.out.println(" ");
        System.out.println(" ");
        System.out.println("7. Exit ");


        List<Integer> possibleChoices = Arrays.asList(1,2,3,4,5,6,7);

        Scanner scanner  = new Scanner(System.in);
        int choice = scanner.nextInt();

        while(!possibleChoices.contains(choice)){
            System.out.println("Invalid choice. Please try again.");
            System.out.println("Enter the number that matches what you would like to do: ");
            choice = scanner.nextInt();
        }
        System.out.println("You selected option: " + choice);
        if(choice == 1){
            openingAccount();
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
            System.out.println("Logging out >>>>>");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("*****************************************");
            System.out.println("Thank you for using Niel Digital Bank!");
            System.out.println("*****************************************");
            System.exit(0);
        }
    }

    private void doSomethingElse(){
        Scanner scanner  = new Scanner(System.in);
        System.out.println("Would you like to do something else? (y/n)");
        System.out.println("Press y for yes and n for no (y/n)");
        String answer = scanner.next();
        if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
            promptUserForIntendedAction();
        } else {

            System.out.println("Wait while we log you out >>>>>");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("*****************************************");
            System.out.println("Thank you for using Niel Digital Bank!");
            System.out.println("*****************************************");
            System.exit(0);
        }
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

    private boolean validateAccountOwnership(long accountNumber) {
        Scanner scanner  = new Scanner(System.in);
        System.out.println("Enter your PIN: ");
        String pin = scanner.nextLine();
        if(!validatePin(pin).equals(PIN_OK)) {
                System.out.println(INVALID_PIN);
        }
        Optional<Account> account = accountDaoImpl.findByAccountNumber(String.valueOf(accountNumber));
        if (account.isEmpty()) {
            System.out.println(INVALID_ACCOUNT_NUMBER);
            System.exit(1);
        }
        Account acct = account.get();
        String accountPin = acct.getPin().substring( 0,4);

        if (!accountPin.equals(pin)) {
            System.out.println(INVALID_PIN);
            System.exit(1);
        }

        return false;
    }

    public void confirmTransactionWithPIN(Account account){
        Scanner scanner  = new Scanner(System.in);
        int pinAttempts = 0;
        String pin = null;
        boolean pinOk = false;
        int MAX_PIN_ATTEMPTS = 3;
        while (pinAttempts < MAX_PIN_ATTEMPTS){
            System.out.println("Confirm transaction with our 4-digit PIN: ");
            pin = scanner.nextLine();
            if(!validatePin(pin).equals(PIN_OK)){
                System.out.println(INVALID_PIN);
                pinAttempts++;
            }else{
                String accountPin = account.getPin().substring( 0,4);
                if (accountPin.equals(pin)) {
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

    public String maskPin(String pin) {
        String masked = "****";
        return pin + masked;
    }

}
