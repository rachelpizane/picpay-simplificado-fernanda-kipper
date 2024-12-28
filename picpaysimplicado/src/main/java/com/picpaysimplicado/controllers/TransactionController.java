package com.picpaysimplicado.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.picpaysimplicado.domain.transaction.Transaction;
import com.picpaysimplicado.dtos.TransactionDTO;
import com.picpaysimplicado.services.TransactionService;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/transactions")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDTO transaction) throws Exception{
        Transaction newTransaction = this.transactionService.createTransaction(transaction);

        return new ResponseEntity<>(newTransaction, HttpStatus.CREATED);
    }
}
