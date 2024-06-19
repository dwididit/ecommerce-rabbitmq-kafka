package dev.dwidi.ecommercerabbitmqkafka.repository;

import dev.dwidi.ecommercerabbitmqkafka.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIdNotIn(List<Long> purchasedProductIds);
    List<Product> findByIdIn(List<Long> ids);
}
