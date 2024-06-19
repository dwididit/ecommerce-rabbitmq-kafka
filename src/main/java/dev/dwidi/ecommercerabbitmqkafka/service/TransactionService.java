package dev.dwidi.ecommercerabbitmqkafka.service;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.transaction.TransactionRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.transaction.TransactionResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.entity.Product;
import dev.dwidi.ecommercerabbitmqkafka.entity.Transaction;
import dev.dwidi.ecommercerabbitmqkafka.entity.User;
import dev.dwidi.ecommercerabbitmqkafka.enums.TransactionStatus;
import dev.dwidi.ecommercerabbitmqkafka.exception.TransactionNotFoundException;
import dev.dwidi.ecommercerabbitmqkafka.repository.ProductRepository;
import dev.dwidi.ecommercerabbitmqkafka.repository.TransactionRepository;
import dev.dwidi.ecommercerabbitmqkafka.repository.UserRepository;
import dev.dwidi.ecommercerabbitmqkafka.service.rabbitmq.RabbitMQSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private Supplier<String> uuidGenerator;

    public PublicResponseDTO<TransactionResponseDTO> createTransaction(Long userId, TransactionRequestDTO transactionRequestDTO) {
        String requestId = uuidGenerator.get();

        // Retrieve user and product
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TransactionNotFoundException("User not found"));
        Product product = productRepository.findById(transactionRequestDTO.getProductId())
                .orElseThrow(() -> new TransactionNotFoundException("Product not found"));

        // Calculate total amount
        Double totalAmount = product.getProductPrice() * transactionRequestDTO.getProductQuantity();

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setProduct(product);
        transaction.setProductQuantity(transactionRequestDTO.getProductQuantity());
        transaction.setStatus(TransactionStatus.CREATED);
        transaction.setTotalAmount(totalAmount);
        transaction.setOperation("CREATE");

        // Log transaction details before saving
        logger.info("Creating transaction: {}", transaction);

        // Save the transaction to the repository
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Log transaction details after saving
        logger.info("Transaction saved: {}", savedTransaction);

        // Send the transaction entity to RabbitMQ
        rabbitMQSender.send(savedTransaction);
        logger.info("Transaction sent to RabbitMQ: {}", savedTransaction);

        // Build the response
        TransactionResponseDTO transactionResponseDTO = getTransactionResponseDTO(savedTransaction);

        return new PublicResponseDTO<>(requestId, 201, "Transaction created successfully", transactionResponseDTO);
    }

    public PublicResponseDTO<TransactionResponseDTO> processTransaction(Long transactionId) {
        String requestId = uuidGenerator.get();

        Transaction transactionToProcess = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        if (transactionToProcess.getStatus() == TransactionStatus.CREATED) {
            transactionToProcess.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transactionToProcess);
            rabbitMQSender.send(transactionToProcess);
        }

        TransactionResponseDTO transactionResponseDTO = getTransactionResponseDTO(transactionToProcess);

        return new PublicResponseDTO<>(requestId, 200, "Transaction processed successfully", transactionResponseDTO);
    }


    public PublicResponseDTO<TransactionResponseDTO> deleteTransaction(Long transactionId) {
        String requestId = uuidGenerator.get();

        Transaction transactionToDelete = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        transactionRepository.delete(transactionToDelete);

        // Create a new Transaction object to send the delete operation to RabbitMQ
        Transaction transactionToSend = new Transaction();
        transactionToSend.setId(transactionId);
        transactionToSend.setOperation("DELETE");
        rabbitMQSender.send(transactionToSend);

        return new PublicResponseDTO<>(requestId, 200, "Transaction deleted successfully", null);
    }

    public PublicResponseDTO<TransactionResponseDTO> getTransactionById(Long transactionId) {
        String requestId = uuidGenerator.get();

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        TransactionResponseDTO transactionResponseDTO = getTransactionResponseDTO(transaction);

        return new PublicResponseDTO<>(requestId, 200, "Transaction retrieved successfully", transactionResponseDTO);
    }

    public PublicResponseDTO<List<TransactionResponseDTO>> getAllTransactions(Integer page, Integer size) {
        String requestId = uuidGenerator.get();
        List<Transaction> transactions;
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Transaction> transactionPage = transactionRepository.findAll(pageable);
            transactions = transactionPage.getContent();
        } else {
            transactions = transactionRepository.findAll();
        }

        List<TransactionResponseDTO> transactionResponseDTOs = transactions.stream()
                .map(this::getTransactionResponseDTO)
                .collect(Collectors.toList());

        PublicResponseDTO<List<TransactionResponseDTO>> responseDTO = new PublicResponseDTO<>();
        responseDTO.setCode(200);
        responseDTO.setMessage("Transactions retrieved successfully");
        responseDTO.setData(transactionResponseDTOs);
        responseDTO.setRequestId(requestId);

        return responseDTO;
    }

    public PublicResponseDTO<List<TransactionResponseDTO>> getTransactionsByUserId(Long userId) {
        String requestId = uuidGenerator.get();

        // Check if the user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TransactionNotFoundException("User not found with ID " + userId));

        // Retrieve transactions for the user
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException("No transactions found for user ID " + userId);
        }

        List<TransactionResponseDTO> transactionResponseDTOs = transactions.stream()
                .map(this::getTransactionResponseDTO)
                .collect(Collectors.toList());

        return new PublicResponseDTO<>(requestId, 200, "Transactions retrieved successfully", transactionResponseDTOs);
    }



    // Helper method to build the TransactionResponseDTO
    private TransactionResponseDTO getTransactionResponseDTO(Transaction transaction) {
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        transactionResponseDTO.setId(transaction.getId());
        transactionResponseDTO.setUserId(transaction.getUser().getId());
        transactionResponseDTO.setProductId(transaction.getProduct().getId());
        transactionResponseDTO.setProductQuantity(transaction.getProductQuantity());
        transactionResponseDTO.setTotalAmount(transaction.getTotalAmount());
        transactionResponseDTO.setStatus(transaction.getStatus());
        transactionResponseDTO.setCreatedAt(LocalDateTime.parse(transaction.getCreatedAt().toString()));
        transactionResponseDTO.setUpdatedAt(LocalDateTime.parse(transaction.getUpdatedAt().toString()));
        return transactionResponseDTO;
    }

}
