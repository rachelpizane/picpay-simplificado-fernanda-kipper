package com.picpaysimplicado.domain.user;

import com.picpaysimplicado.dtos.UserDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name="users")
@Table(name="users")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String document;

    @Column(unique = true)
    private String email;

    private String password;
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private UserType userType;


    public User(UserDTO data) {
        this.firstName = data.firstName();
        this.lastName = data.lastName();
        this.document = data.document();
        this.email = data.email();
        this.password = data.password();
        this.balance = data.balance();
        this.userType = data.userType();
    }

    
}
