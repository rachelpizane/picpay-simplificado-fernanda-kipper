package com.picpaysimplicado.services;

import com.picpaysimplicado.domain.user.User;
import com.picpaysimplicado.dtos.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

    @Autowired
    private RestTemplate restTemplate;

    public void sendNotification(User user, String message) {
        String email = user.getEmail();
        NotificationDTO notificationRequest = new NotificationDTO(email, message);

        try {
            restTemplate.postForEntity("https://util.devi.tools/api/v1/notify", notificationRequest, String.class);

            System.out.println("[SUCCESS] Notificação enviada com sucesso");

        } catch (HttpServerErrorException error) {
            System.out.println("[ERRO] Serviço de notificação fora do ar");
        }
    }
}
