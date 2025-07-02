package com.nielvid;

import com.nielvid.services.impl.AccountServiceImpl;

public class BankApp {

    public static void main(String[] args) {
        System.out.println("***********************************");
        System.out.println("Welcome to the Niel Digital Bank!");
        System.out.println("***********************************");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        AccountServiceImpl accountServiceImpl = new AccountServiceImpl();
        accountServiceImpl.promptUserForIntendedAction();
    }
}
