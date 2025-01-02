package com.picpaysimplicado.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.picpaysimplicado.domain.user.User;
import com.picpaysimplicado.dtos.AuthorizationTransactionDTO;

@Service
public class AuthorizationService {
    @Autowired
    private RestTemplate restTemplate;

    @SuppressWarnings("null")
    public boolean authorizeTransaction(User sender, BigDecimal amount){
        try {
            ResponseEntity<AuthorizationTransactionDTO> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", AuthorizationTransactionDTO.class);

            System.out.println("[SUCCESS] Transação autorizada com sucesso");
            return authorizationResponse.getBody().data().authorization();
            
        } catch (HttpClientErrorException error) {
            System.out.println("[ERRO] Transação não autorizada");
            return false;
        } 
    }
}
