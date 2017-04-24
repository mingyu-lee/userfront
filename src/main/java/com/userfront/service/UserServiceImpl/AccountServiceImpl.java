package com.userfront.service.UserServiceImpl;

import com.userfront.dao.PrimaryAccountDao;
import com.userfront.dao.SavingsAccountDao;
import com.userfront.domain.*;
import com.userfront.service.AccountService;
import com.userfront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;

/**
 * Created by 이민규 on 2017-04-19.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static int nextAccountNumber = 11223145;

    @Autowired
    private PrimaryAccountDao primaryAccountDao;

    @Autowired
    private SavingsAccountDao savingsAccountDao;

    @Autowired
    private UserService userService;

    @Override
    public PrimaryAccount createPrimaryAccount() {

        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountBalance(new BigDecimal(0.0));
        primaryAccount.setAccountNumber(accountGen());

        primaryAccountDao.save(primaryAccount);

        return primaryAccountDao.findByAccountNumber(primaryAccount.getAccountNumber());
    }

    @Override
    public SavingsAccount createSavingsAccount() {

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountBalance(new BigDecimal(0.0));
        savingsAccount.setAccountNumber(accountGen());

        savingsAccountDao.save(savingsAccount);

        return savingsAccountDao.findByAccountNumber(savingsAccount.getAccountNumber());
    }

    private int accountGen() {
        return ++nextAccountNumber;
    }

    @Override
    public void deposit(String accountType, double amount, Principal principal) {

        User user = userService.findByUsername(principal.getName());

        if ("Primary".equalsIgnoreCase(accountType)) {
            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Deposit to Primary Account", "Account",
                    "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
        } else if ("Savings".equalsIgnoreCase(accountType)) {
            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();

            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Deposit to savings Account",
                    "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
        }

    }

    @Override
    public void withdraw(String accountType, double amount, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        if ("Primary".equalsIgnoreCase(accountType)) {

            PrimaryAccount primaryAccount = user.getPrimaryAccount();
            primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            primaryAccountDao.save(primaryAccount);

            Date date = new Date();

            PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Withdraw from primary account",
                    "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);

        } else if ("Savings".equalsIgnoreCase(accountType)) {

            SavingsAccount savingsAccount = user.getSavingsAccount();
            savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
            savingsAccountDao.save(savingsAccount);

            Date date = new Date();

            SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Withdraw from savings Account",
                    "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);

        }
    }
}
