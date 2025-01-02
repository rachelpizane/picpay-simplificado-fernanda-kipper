package com.picpaysimplicado.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.picpaysimplicado.domain.transaction.Transaction;
import com.picpaysimplicado.domain.user.User;
import com.picpaysimplicado.domain.user.UserType;
import com.picpaysimplicado.dtos.TransactionDTO;
import com.picpaysimplicado.repositories.TransactionRepository;

public class TransactionServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository repository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private NotificationService notificationService;

    @Autowired
    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create a successfully when everything is OK")
    void testCreateTransaction() throws Exception {
        User sender = new User(1L, "Fernanda", "Silva", "123347", "fernanda@exemplo.com", "senha1234", new BigDecimal(60), UserType.COMMON);

        User receiver = new User(2L, "Carla", "Soares", "123348", "carla@exemplo.com", "senha1234", new BigDecimal(20), UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);
        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(true);

        TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);

        Transaction result = this.transactionService.createTransaction(request);

        assertThat(new TransactionDTO(result.getAmount(), sender.getId(), receiver.getId())).isEqualTo(request);

        verify(repository, times(1)).save(any()); // Verifica se o método .save() foi acionado apenas uma vez.

        verify(userService, times(1)).saveUser(argThat(user -> 
            user.getId() == sender.getId() && user.getBalance().equals(new BigDecimal(50)) // Verifica se o método .saveUser foi acionado apenas uma vez e que o argumento utilizado possui o id igual ao sender e que seu balanço é igual a 50.
        ));

        verify(userService, times(1)).saveUser(argThat(user -> 
        user.getId() == receiver.getId() && user.getBalance().equals(new BigDecimal(30)) // Verifica se o método .saveUser foi acionado apenas uma vez e que o argumento utilizado possui o id igual ao receiver e que seu balanço é igual a 30.
        ));
    }

    @Test
    @DisplayName("Should throw Exception when Transaction is now allowed")
    void testCreateTransactionCase2() throws Exception {
        User sender = new User(1L, "Fernanda", "Silva", "123347", "fernanda@exemplo.com", "senha1234", new BigDecimal(60), UserType.COMMON);

        User receiver = new User(2L, "Carla", "Soares", "123348", "carla@exemplo.com", "senha1234", new BigDecimal(20), UserType.COMMON);

        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);
        when(authorizationService.authorizeTransaction(any(), any())).thenReturn(false);

        TransactionDTO request = new TransactionDTO(new BigDecimal(10), 1L, 2L);

        Exception exception = assertThrows(Exception.class, () -> {
            this.transactionService.createTransaction(request); // Verifica se foi lançado um erro da classe "Exception"
        });

        String expectedMessage = "Transação não autorizada";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage); // Verifica se a mensagem lançaca é igual a mensagem esperada.
    }
}
