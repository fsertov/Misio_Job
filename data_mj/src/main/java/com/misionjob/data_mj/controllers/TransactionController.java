package com.misionjob.data_mj.controllers;


import com.misionjob.data_mj.exceptions.AccountNotFoundException;
import com.misionjob.data_mj.exceptions.InsufficientBalanceException;
import com.misionjob.data_mj.models.Account;
import com.misionjob.data_mj.models.Transaction;
import com.misionjob.data_mj.repositories.AccountRepository;
import com.misionjob.data_mj.repositories.TransactionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class TransactionController {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionController(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/transactions")
    Transaction newTransaction(@RequestBody Transaction transaction){
        Account accountOrigin = accountRepository.findById(transaction.getUserIdOrigin()).orElse(null);
        Account accountDestinity= accountRepository.findById(transaction.getUserIdDestiny()).orElse(null);

        if (accountOrigin == null)
            throw new AccountNotFoundException("No se encontro una cuenta con el userId: " + transaction.getUserIdOrigin());

        if (accountDestinity == null)
            throw new AccountNotFoundException("No se encontro una cuenta con el userId: " + transaction.getUserIdDestiny());

        if(accountOrigin.getBalance() < transaction.getValue())
            throw new InsufficientBalanceException("No tiene puntos en su experiencia misión Job");

        accountOrigin.setBalance(accountOrigin.getBalance() - transaction.getValue());
        accountOrigin.setLastChange(new Date());
        accountRepository.save(accountOrigin);

        accountDestinity.setBalance(accountDestinity.getBalance() + transaction.getValue());
        accountDestinity.setLastChange(new Date());
        accountRepository.save(accountDestinity);

        transaction.setDate(new Date());
        return transactionRepository.save(transaction);

    }

    @GetMapping("/transactions/{userId}")
    List<Transaction> userTransaction(@PathVariable String userId){
        Account userAccount = accountRepository.findById(userId).orElse(null);
        if (userAccount == null)
            throw new AccountNotFoundException("No se encontro una cuenta con el userId: " + userId);

        List<Transaction> transactionsOrigin = transactionRepository.findByUserIdOrigin(userId);
        List<Transaction> transactionsDestinity = transactionRepository.findByUserIdDestiny(userId);

        List<Transaction> transactions = Stream.concat(transactionsOrigin.stream(), transactionsDestinity.stream()).collect(Collectors.toList());

        return transactions;
    }


}
