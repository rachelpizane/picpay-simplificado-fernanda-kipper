package com.picpaysimplicado.services;


import com.picpaysimplicado.domain.user.User;
import com.picpaysimplicado.domain.user.UserType;
import com.picpaysimplicado.dtos.UserDTO;
import com.picpaysimplicado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public void validaTeTransaction(User sender, BigDecimal amount) throws Exception {
        if(sender.getUserType() != UserType.COMMON){
            throw new Exception("Usuário do tipo lojista não está autorizado a realizar transação");
        }

        if(sender.getBalance().compareTo(amount) < 0){
            throw new Exception("Saldo insuficiente");
        }
    }

    public User findUserById(Long id) throws Exception {
        return this.repository.findUserById(id).orElseThrow(() -> new Exception("Usuário não encontrado"));
    }

    public void saveUser(User user){
        this.repository.save(user);
    }

    public User createUser(UserDTO data){
        User newUser = new User(data);
        this.saveUser(newUser);

        return newUser;
    }

    public List<User> getAllUsers() {
        return this.repository.findAll();
    }
}
