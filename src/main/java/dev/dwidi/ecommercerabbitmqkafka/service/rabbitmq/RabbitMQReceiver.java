package dev.dwidi.ecommercerabbitmqkafka.service.rabbitmq;

import dev.dwidi.ecommercerabbitmqkafka.config.rabbitmq.RabbitMQConfig;
import dev.dwidi.ecommercerabbitmqkafka.dto.product.ProductRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.transaction.TransactionRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.entity.Product;
import dev.dwidi.ecommercerabbitmqkafka.entity.Transaction;
import dev.dwidi.ecommercerabbitmqkafka.entity.User;
import dev.dwidi.ecommercerabbitmqkafka.service.ProductService;
import dev.dwidi.ecommercerabbitmqkafka.service.TransactionService;
import dev.dwidi.ecommercerabbitmqkafka.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQReceiver {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private TransactionService transactionService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(Object message) {
        if (message instanceof User) {
            handleUserMessage((User) message);
        } else if (message instanceof Product) {
            handleProductMessage((Product) message);
        } else if (message instanceof Transaction) {
            handleTransactionMessage((Transaction) message);
        }
    }

    private void handleUserMessage(User user) {
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

    private void handleProductMessage(Product product) {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setProductName(product.getProductName());
        productRequestDTO.setProductDescription(product.getProductDescription());
        productRequestDTO.setProductCategory(product.getProductCategory());
        productRequestDTO.setProductPrice(product.getProductPrice());

        switch (product.getOperation()) {
            case "CREATE":
                productService.createProduct(productRequestDTO);
                break;
            case "UPDATE":
                productService.updateProduct(product.getId(), productRequestDTO);
                break;
            case "DELETE":
                productService.deleteProduct(product.getId());
                break;
        }
    }

    private void handleTransactionMessage(Transaction transaction) {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setProductId(transaction.getProduct().getId());
        transactionRequestDTO.setProductQuantity(transaction.getProductQuantity());

        Long userId = transaction.getUser().getId();

        switch (transaction.getOperation()) {
            case "CREATE":
                transactionService.createTransaction(userId, transactionRequestDTO);
                break;
            case "DELETE":
                transactionService.deleteTransaction(transaction.getId());
                break;
            case "PROCESS":
                transactionService.processTransaction(transaction.getId());
                break;
        }
    }
}
