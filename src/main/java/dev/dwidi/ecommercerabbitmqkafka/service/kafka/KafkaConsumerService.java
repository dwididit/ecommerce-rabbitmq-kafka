package dev.dwidi.ecommercerabbitmqkafka.service.kafka;

import dev.dwidi.ecommercerabbitmqkafka.entity.Product;
import dev.dwidi.ecommercerabbitmqkafka.entity.Transaction;
import dev.dwidi.ecommercerabbitmqkafka.exception.UserNotFoundException;
import dev.dwidi.ecommercerabbitmqkafka.repository.ProductRepository;
import dev.dwidi.ecommercerabbitmqkafka.repository.TransactionRepository;
import dev.dwidi.ecommercerabbitmqkafka.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class KafkaConsumerService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private final ConcurrentMap<Long, List<Product>> recommendationsCache = new ConcurrentHashMap<>();

    public void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
    }

    @KafkaListener(topics = "product-recommendations", groupId = "group_id")
    public void consume(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        List<Product> recommendations = recommendProducts(userId);
        recommendationsCache.put(userId, recommendations);
        System.out.println("Recommendations for user " + userId + ": " + recommendations);
    }

    public List<Product> recommendProducts(Long userId) {
        List<Long> similarUserIds = findSimilarUsers(userId);

        // Get products purchased by similar users
        List<Long> similarUsersProductIds = transactionRepository.findProductIdsByUserIds(similarUserIds);

        // Exclude products already purchased by the user
        List<Long> userPurchasedProductIds = transactionRepository.findProductIdsByUserId(userId);
        List<Long> recommendedProductIds = similarUsersProductIds.stream()
                .filter(productId -> !userPurchasedProductIds.contains(productId))
                .collect(Collectors.toList());

        return productRepository.findByIdIn(recommendedProductIds);
    }

    public List<Long> findSimilarUsers(Long userId) {
        List<Transaction> userTransactions = transactionRepository.findByUserId(userId);
        List<Long> purchasedProductIds = userTransactions.stream()
                .map(Transaction::getProduct)
                .map(Product::getId)
                .collect(Collectors.toList());

        // Find users who purchased at least one of the same products
        return transactionRepository.findSimilarUserIdsByProductIds(purchasedProductIds, userId);
    }

    public List<Product> getRecommendations(Long userId) {
        return recommendationsCache.get(userId);
    }
}