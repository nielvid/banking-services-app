package services.impl;

import model.Account;
import model.AccountType;
import model.User;
import services.AccountService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AccountServiceImpl implements AccountService {

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
                System.out.println("Fund your account with a minimum of $100");
                System.out.println("Enter the amount you want to deposit: ");
                double amount = scanner.nextDouble();
                initialDeposit(account, amount);
                System.out.println("Would you like to do something else? (y/n)");
                String answer = scanner.next();
                if (answer.equalsIgnoreCase("y")) {
                    promptUserForIntendedAction();
                } else {
                    System.out.println("Thank you for using Niel Digital Bank!");
                    System.exit(0);
                }
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
    public void makeDeposit(Account account, double amount) {
        while (amount <= 0) {
            System.out.println("Invalid amount. Please enter a positive amount.");
            System.out.println("Enter the amount you want to deposit: ");
            amount = scanner.nextDouble();
        }
        BigDecimal accountBalance = account.getBalance().add(BigDecimal.valueOf(amount));
        account.setBalance(accountBalance);
        System.out.printf("%.2f deposited successfully into your account %s%n",amount, account.getAccountNumber());
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
        if(choice == 1){
            accountOpeningOperation();
        }
        if(choice == 2){
            System.exit(0);
        }
        if(choice == 3){
            System.exit(0);
        }
        if(choice == 4){
            System.exit(0);
        }
        if(choice == 5){
            System.exit(0);
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

}
