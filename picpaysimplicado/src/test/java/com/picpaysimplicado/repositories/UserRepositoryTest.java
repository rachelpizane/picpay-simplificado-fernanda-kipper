package com.picpaysimplicado.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import com.picpaysimplicado.domain.user.User;
import com.picpaysimplicado.domain.user.UserType;
import com.picpaysimplicado.dtos.UserDTO;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;
    
    @Test
    @DisplayName("Should get User successfully from DB")
    void testFindUserByDocumentSuccess() {
        String document = "12345667";
        UserDTO data = new UserDTO("Fernanda", "Silva", document, "fernanda@exemplo.com", "senha1234", new BigDecimal(60), UserType.COMMON);

        User persistedUser = this.createUser(data);

        Optional<User> result = this.userRepository.findUserByDocument(document);

        assertThat(result.isPresent()).isTrue(); // Afirma que o resultado é verdadeiro, caso ao contrário, o teste falha
        assertThat(result.get()).isEqualTo(persistedUser); // Afirma que o resultado é igual ao usuário persistido, caso ao contrário, o teste falha
    }

    @Test
    @DisplayName("Should not get User from DB when user not exists")
    void testFindUserByDocumentEmpty() {
        String document = "12345667";
 
        Optional<User> result = this.userRepository.findUserByDocument(document);

        assertThat(result.isEmpty()).isTrue(); // Afirma que o optional está vazio, caso ao contrário, o teste falha.
    }

    private User createUser(UserDTO data) {
        User newUser = new User(data);
        this.entityManager.persist(newUser);

        return newUser;
    }
}
