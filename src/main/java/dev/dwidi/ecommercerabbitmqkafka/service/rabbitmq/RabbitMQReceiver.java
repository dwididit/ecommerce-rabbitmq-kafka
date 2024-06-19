package dev.dwidi.ecommercerabbitmqkafka.service;

import dev.dwidi.ecommercerabbitmqkafka.config.rabbitmq.RabbitMQConfig;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.entity.User;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQReceiver {

    @Autowired
    private UserService userService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(User user) {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername(user.getUsername());
        userRequestDTO.setPassword(user.getPassword());
        userRequestDTO.setEmail(user.getEmail());
        userRequestDTO.setAddress(user.getAddress());
        userRequestDTO.setPhone(user.getPhone());

        switch (user.getOperation()) {
            case "CREATE":
                userService.createUser(userRequestDTO);
                break;
            case "UPDATE":
                userService.updateUser(user.getId(), userRequestDTO);
                break;
            case "DELETE":
                userService.deleteUser(user.getId());
                break;
        }
    }
}