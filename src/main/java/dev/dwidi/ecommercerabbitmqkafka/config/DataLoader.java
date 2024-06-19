package dev.dwidi.ecommercerabbitmqkafka.config;

import com.github.javafaker.Faker;
import dev.dwidi.ecommercerabbitmqkafka.entity.Product;
import dev.dwidi.ecommercerabbitmqkafka.entity.Transaction;
import dev.dwidi.ecommercerabbitmqkafka.entity.User;
import dev.dwidi.ecommercerabbitmqkafka.enums.TransactionStatus;
import dev.dwidi.ecommercerabbitmqkafka.enums.UserRole;
import dev.dwidi.ecommercerabbitmqkafka.repository.ProductRepository;
import dev.dwidi.ecommercerabbitmqkafka.repository.TransactionRepository;
import dev.dwidi.ecommercerabbitmqkafka.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Check if the database already contains data
        if (userRepository.count() > 0 || productRepository.count() > 0 || transactionRepository.count() > 0) {
            System.out.println("Data already exists in the database. Skipping data loading.");
            return;
        }

        Faker faker = new Faker(Locale.forLanguageTag("id-ID"));
        Random random = new Random();

        List<User> users = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Transaction> transactions = new ArrayList<>();

        // Create admin user
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("admin"));
        adminUser.setAddress(faker.address().fullAddress());
        adminUser.setPhone(faker.phoneNumber().phoneNumber());
        adminUser.setUserRole(UserRole.ROLE_ADMIN);
        users.add(adminUser);

        // Create 99 additional users
        for (int i = 0; i < 99; i++) {
            User user = new User();
            user.setUsername(faker.name().username());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(passwordEncoder.encode(faker.internet().password()));
            user.setAddress(faker.address().fullAddress());
            user.setPhone(faker.phoneNumber().phoneNumber());
            user.setUserRole(UserRole.ROLE_USER);
            users.add(user);
        }
        userRepository.saveAll(users);

        // Create 100 Products
        for (int i = 0; i < 100; i++) {
            Product product = new Product();
            product.setProductName(faker.commerce().productName());
            product.setProductDescription(faker.lorem().sentence());
            product.setProductCategory(faker.commerce().department());
            product.setProductPrice((double) (100000 + (random.nextInt(9) * 50000)));
            products.add(product);
        }
        productRepository.saveAll(products);

        // Create 250 CREATED Transactions and 1750 SUCCESS Transactions
        for (int i = 0; i < 2000; i++) {
            Transaction transaction = new Transaction();
            transaction.setUser(users.get(random.nextInt(users.size())));
            transaction.setProduct(products.get(random.nextInt(products.size())));
            transaction.setProductQuantity(random.nextInt(10) + 1); // random quantity between 1 and 10
            if (i < 250) {
                transaction.setStatus(TransactionStatus.CREATED);
            } else {
                transaction.setStatus(TransactionStatus.SUCCESS);
            }
            transaction.setTotalAmount(transaction.getProduct().getProductPrice() * transaction.getProductQuantity());
            transactions.add(transaction);
        }

        // Print the number of transactions created
        System.out.println("Number of transactions created: " + transactions.size());

        transactionRepository.saveAll(transactions);

        // Verify that transactions are saved
        long transactionCount = transactionRepository.count();
        System.out.println("Number of transactions saved: " + transactionCount);
    }
}
