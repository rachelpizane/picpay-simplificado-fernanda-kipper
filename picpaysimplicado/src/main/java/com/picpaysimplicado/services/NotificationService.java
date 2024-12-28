package com.picpaysimplicado.services;

import com.picpaysimplicado.domain.user.User;
import com.picpaysimplicado.dtos.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

    @Autowired
    private RestTemplate restTemplate;

    public void sendNotification(User user, String message) throws Exception {
        String email = user.getEmail();
        NotificationDTO notificationRequest = new NotificationDTO(email, message);

        try {
            restTemplate.postForEntity("https://util.devi.tools/api/v1/notify", notificationRequest, String.class);

            System.out.println("Notificação enviada com sucesso");

        } catch (HttpClientErrorException error) {
            System.out.println(error.getMessage());
        }
    }
}
