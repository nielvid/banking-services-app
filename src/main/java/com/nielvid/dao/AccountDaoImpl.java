package com.nielvid.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.nielvid.config.DatabaseUtil;
import com.nielvid.entities.Account;
import com.nielvid.entities.User;
import com.nielvid.model.AccountType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountDaoImpl implements AccountDao<Account, Integer> {
    public static final String CREATE_USER = "insert into users (client_id, first_name, last_name, email, phone) values (?, ?, ?, ?, ?) returning id";
    public static final String CREATE_ACCOUNT = "insert into accounts (user_id, account_number, account_name, balance,account_type, status) values (?, ?, ?, ?, ?, ?) returning id";
    public static final String FIND_ONE_ACCOUNT = "select * from accounts where id = ?";
    private static final String FIND_BY_ACCOUNT_NUMBER = "select * from accounts where account_number = ?";
    public static final String FIND_ALL_ACCOUNT = "select * from accounts"  ;
    private static final Logger log = LoggerFactory.getLogger(AccountDaoImpl.class);
    private final String UPDATE_TRANSACTION_PIN = "UPDATE accounts SET pin=? WHERE account_number=?";
    private final String UPDATE_ACCOUNT_BALANCE = "UPDATE accounts SET balance=? where account_number = ?";


    @Override
    public Optional<Account> findOne(Integer id)throws SQLException {
        Connection conn = DatabaseUtil.getConnection();
        PreparedStatement statement = conn.prepareStatement(FIND_ONE_ACCOUNT);
        statement.setLong(1, id);
        ResultSet rs = statement.executeQuery();
        Optional<Account> account = mapAccount(rs);
        rs.close();
        statement.close();
        return account;
    }

    @Override
    public synchronized Account create(User user, AccountType accountType) {
        Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
              Account account = new Account(user, accountType);
//              findOne(account.)

              connection.setAutoCommit(false);

              PreparedStatement uStatement = connection.prepareStatement(CREATE_USER);
              uStatement.setString(1, user.getClientId());
              uStatement.setString(2, user.getFirstName());
              uStatement.setString(3, user.getLastName());
              uStatement.setString(4, user.getEmail());
              uStatement.setString(5, user.getPhone());

              ResultSet userRs = uStatement.executeQuery();
              int userId = 0;
              if (userRs.next()) {
                  userId = userRs.getInt(1);
                  user.setId(userId);
              }

              PreparedStatement aStatement = connection.prepareStatement(CREATE_ACCOUNT);
              aStatement.setString(1, user.getClientId());
              aStatement.setString(2, account.getAccountNumber());
              aStatement.setString(3, user.getFirstName() + " " + user.getLastName());
              aStatement.setBigDecimal(4, BigDecimal.valueOf(0.00));
              aStatement.setString(5, accountType.name());
              aStatement.setString(6, account.getStatus().name());

              ResultSet accountRs = aStatement.executeQuery();
              if (accountRs.next()) {
                  int accountId = accountRs.getInt(1);
                  account.setId(accountId);
              } else {
                  throw new SQLException("Failed to create account");
              }

              connection.commit();
              log.info("Account created with id: {} for user: {}", account.getId(), user.getClientId());

            return account;
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(Account entity, Integer y) {
        // Implementation for updating an account
    }

    @Override
    public void delete(Integer id) {
        // Implementation for deleting an account
    }

    @Override
    public List<Account> findAll() {
       try(Connection conn = DatabaseUtil.getConnection()){
           Statement statement = conn.createStatement();
           ResultSet rs = statement.executeQuery(FIND_ALL_ACCOUNT);
           return mapAccounts(rs);
       }
       catch (SQLException e) {
           throw new RuntimeException(e);
       }
    }
    public Optional<Account> findByAccountNumber(String accountNumber) {
      try(Connection conn = DatabaseUtil.getConnection()){
          PreparedStatement statement = conn.prepareStatement(FIND_BY_ACCOUNT_NUMBER);
          statement.setString(1, accountNumber);
          ResultSet rs = statement.executeQuery();
          Optional<Account> account = mapAccount(rs);
          rs.close();
          statement.close();
          return account;
      }
      catch (SQLException e) {
          throw new RuntimeException(e);
      }
    }


    public void depositFund(String accountNumber, double amount) {
        Optional<Account> account = findByAccountNumber(accountNumber);
        if (account.isEmpty()) {
            System.out.println("Account not found.");
            return;
        }
        Account acct = account.get();
        try(Connection conn = DatabaseUtil.getConnection()){
            PreparedStatement statement = conn.prepareStatement(UPDATE_ACCOUNT_BALANCE);
            statement.setBigDecimal(1, acct.getBalance().add(BigDecimal.valueOf(amount)));
            statement.setString(2, accountNumber);
            int count = statement.executeUpdate();
            if(count == 1){
                log.info("{} Successfully deposited into your for account: {}", amount, account.<Object>map(Account::getAccountNumber).orElse(null));
            }
            statement.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void withdrawFund(String accountNumber, double amount) {
        Optional<Account> account = findByAccountNumber(accountNumber);
        if (account.isEmpty()) {
            System.out.println("Account not found.");
            return;
        }
        Account acct = account.get();
        try(Connection conn = DatabaseUtil.getConnection()){
            PreparedStatement statement = conn.prepareStatement(UPDATE_ACCOUNT_BALANCE);
            statement.setBigDecimal(1, acct.getBalance().subtract(BigDecimal.valueOf(amount)));
            statement.setString(2, accountNumber);
            int count = statement.executeUpdate();
            if(count == 1){
                log.info("Funds withdrawal Successful");
            }
            statement.close();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized int fundTransfer(Account sourceAccount, Account recipientAccount,  double amount) {
        Connection conn = null;
        int  count = 0;
        try{
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement sourceStmt = conn.prepareStatement(UPDATE_ACCOUNT_BALANCE);
            sourceStmt.setBigDecimal(1, sourceAccount.getBalance().subtract(BigDecimal.valueOf(amount)));
            sourceStmt.setString(2, sourceAccount.getAccountNumber());
            int count1 = sourceStmt.executeUpdate();

            PreparedStatement rStmt = conn.prepareStatement(UPDATE_ACCOUNT_BALANCE);
            rStmt.setBigDecimal(1, recipientAccount.getBalance().add(BigDecimal.valueOf(amount)));
            rStmt.setString(2, recipientAccount.getAccountNumber());
            int count2 = rStmt.executeUpdate();

            // If batch operation is desired
           /* PreparedStatement updateStmt = conn.prepareStatement(UPDATE_ACCOUNT_BALANCE);

            // First update
            updateStmt.setBigDecimal(1, sourceAccount.getBalance().subtract(BigDecimal.valueOf(amount)));
            updateStmt.setString(2, sourceAccount.getAccountNumber());
            updateStmt.addBatch();

            // Second update
            updateStmt.setBigDecimal(1, recipientAccount.getBalance().add(BigDecimal.valueOf(amount)));
            updateStmt.setString(2, recipientAccount.getAccountNumber());
            updateStmt.addBatch();

            int[] results = updateStmt.executeBatch();*/

            if(count1 != 1 || count2 != 1){
                log.info("Funds transfer not Successful");
                conn.rollback();
                throw new RuntimeException("Transfer failed");
            }
            sourceStmt.close();
            rStmt.close();
            conn.commit();
            count = 2;
        }
        catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return  count;
    }

    public int updateAccountPIN(String accountNumber, String pin) throws SQLException{
        try{
            Connection connection = DatabaseUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE_TRANSACTION_PIN);
            statement.setString(1, pin);
            statement.setString(2, accountNumber);
            return statement.executeUpdate();
        }
        catch (SQLException e){
            throw  new RuntimeException(e.getMessage());
        }
    }
    public List<Account> mapAccounts(ResultSet rs){
      List<Account> accounts = new ArrayList<>();
      try{
        while (rs.next()){
          Account account = new Account();
          account.setId(rs.getInt("id"));
          account.setUserId(rs.getString("user_id"));
          account.setAccountNumber(rs.getString("account_number"));
          account.setAccountName(rs.getString("account_name"));
          account.setAccountType(AccountType.valueOf(rs.getString("account_type")));
          account.setBalance(rs.getBigDecimal("balance"));
          accounts.add(account);
        }
        return accounts;
      }
      catch (SQLException e){
        e.printStackTrace();
      }
      return accounts;

    }

    public Optional<Account> mapAccount(ResultSet rs){
        try{
            if (rs.next()){
                Account account = new Account();
                account.setId(rs.getInt("id"));
                account.setUserId(rs.getString("user_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setAccountName(rs.getString("account_name"));
                account.setAccountType(AccountType.valueOf(rs.getString("account_type")));
                account.setBalance(rs.getBigDecimal("balance"));
                account.setPin(rs.getString("pin"));
                return Optional.of(account);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
