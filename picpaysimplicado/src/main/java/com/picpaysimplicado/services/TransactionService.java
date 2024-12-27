package com.picpaysimplicado.services;

import com.picpaysimplicado.domain.transaction.Transaction;
import com.picpaysimplicado.domain.user.User;
import com.picpaysimplicado.dtos.AuthorizationTransactionDTO;
import com.picpaysimplicado.dtos.TransactionDTO;
import com.picpaysimplicado.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class TransactionService {
    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    public void createTransaction(TransactionDTO transaction) throws Exception {
        User sender = userService.findUserById(transaction.senderId());
        User receiver = userService.findUserById(transaction.receiverId());

        userService.validaTeTransaction(sender, transaction.value());

        if(!this.authorizeTransaction(sender, transaction.value())){
            throw new Exception("Transação não autorizada");
        }

        Transaction newTransaction = new Transaction();

        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setAmount(transaction.value());
        newTransaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        this.repository.save(newTransaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);
    }

    private boolean authorizeTransaction(User sender, BigDecimal amount){
        ResponseEntity<AuthorizationTransactionDTO> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", AuthorizationTransactionDTO.class);

        if(authorizationResponse.getStatusCode() == HttpStatus.OK){
                return authorizationResponse.getBody().data().authorization();
        }

        return false;
    }

}
