package com.picpaysimplicado.services;

import com.picpaysimplicado.domain.transaction.Transaction;
import com.picpaysimplicado.domain.user.User;
import com.picpaysimplicado.dtos.AuthorizationTransactionDTO;
import com.picpaysimplicado.dtos.TransactionDTO;
import com.picpaysimplicado.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {
    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NotificationService notificationService;

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        User sender = userService.findUserById(transaction.senderId());
        User receiver = userService.findUserById(transaction.receiverId());

        userService.validaTeTransaction(sender, transaction.value());

        if(!this.authorizeTransaction(sender, transaction.value())){
            throw new Exception("Transação não autorizada");
        }

        Transaction newTransaction = saveTransaction(sender, receiver, transaction.value());
        
        return newTransaction;
    }

    private Transaction saveTransaction(User sender, User receiver, BigDecimal amount) throws Exception{
        Transaction newTransaction = new Transaction();

        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setAmount(amount);
        newTransaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        this.repository.save(newTransaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);
        
        this.notificationService.sendNotification(sender,"Transação realizada com sucesso");
        this.notificationService.sendNotification(receiver,"Transação recebida com sucesso");

        return newTransaction;
    }

    @SuppressWarnings("null")
    private boolean authorizeTransaction(User sender, BigDecimal amount){
        try {
            ResponseEntity<AuthorizationTransactionDTO> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", AuthorizationTransactionDTO.class);

            System.out.println("[SUCESS] Transação autorizada com sucesso");
            return authorizationResponse.getBody().data().authorization();
            
        } catch (HttpClientErrorException error) {
            System.out.println("[ERRO] Transação não autorizada");
            return false;
        } 
    }

}
