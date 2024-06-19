package dev.dwidi.ecommercerabbitmqkafka.service;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.product.ProductRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.product.ProductResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.entity.Product;
import dev.dwidi.ecommercerabbitmqkafka.exception.ProductNotFoundException;
import dev.dwidi.ecommercerabbitmqkafka.exception.UserNotFoundException;
import dev.dwidi.ecommercerabbitmqkafka.repository.ProductRepository;
import dev.dwidi.ecommercerabbitmqkafka.service.rabbitmq.RabbitMQSender;
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
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private Supplier<String> uuidGenerator;

    public PublicResponseDTO<ProductResponseDTO> createProduct(ProductRequestDTO productRequestDTO) {
        String requestId = uuidGenerator.get();

        // Mapping the DTO
        Product product = new Product();
        product.setProductName(productRequestDTO.getProductName());
        product.setProductDescription(productRequestDTO.getProductDescription());
        product.setProductCategory(productRequestDTO.getProductCategory());
        product.setProductPrice(productRequestDTO.getProductPrice());
        product.setOperation("CREATE");

        // Save the product to the repository
        Product savedproduct = productRepository.save(product);

        // Reload the user to get createdAt and updatedAt
        savedproduct = productRepository.findById(savedproduct.getId()).orElseThrow(() -> new ProductNotFoundException("product not found"));

        // Save the user to the repository
        rabbitMQSender.send(product);

        // Reload the user to get the createdAt and updatedAt fields populated
        savedproduct = productRepository.findById(savedproduct.getId()).orElseThrow(() -> new UserNotFoundException("User not found after saving"));

        // Build the response
        ProductResponseDTO productResponseDTO = new ProductResponseDTO(savedproduct);

        return new PublicResponseDTO<>(requestId, 201, "Product created successfully", productResponseDTO);
    }

    public PublicResponseDTO<ProductResponseDTO> updateProduct(Long productId, ProductRequestDTO productRequestDTO) {
        String requestId = uuidGenerator.get();

        Product productToEdit = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // Update only the fields that are not null in the ProductRequestDTO
        if (productRequestDTO.getProductName() != null) {
            productToEdit.setProductName(productRequestDTO.getProductName());
        }
        if (productRequestDTO.getProductDescription() != null) {
            productToEdit.setProductDescription(productRequestDTO.getProductDescription());
        }
        if (productRequestDTO.getProductCategory() != null) {
            productToEdit.setProductCategory(productRequestDTO.getProductCategory());
        }
        if (productRequestDTO.getProductPrice() != null) {
            productToEdit.setProductPrice(productRequestDTO.getProductPrice());
        }
        productToEdit.setOperation("UPDATE");

        productRepository.save(productToEdit);
        rabbitMQSender.send(productToEdit);

        // Build the response
        ProductResponseDTO productResponseDTO = getProductResponseDTO(productToEdit);

        return new PublicResponseDTO<>(requestId, 200, "Product updated successfully", productResponseDTO);
    }

    public PublicResponseDTO<ProductResponseDTO> deleteProduct(Long productId) {
        String requestId = uuidGenerator.get();

        Product productToDelete = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productRepository.delete(productToDelete);

        // Create a new Product object to send the delete operation to RabbitMQ
        Product productToSend = new Product();
        productToSend.setId(productId);
        productToSend.setOperation("DELETE");
        rabbitMQSender.send(productToSend);

        return new PublicResponseDTO<>(requestId, 200, "Product deleted successfully", null);
    }

    public PublicResponseDTO<ProductResponseDTO> getProductById(Long id) {
        String requestId = uuidGenerator.get();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        ProductResponseDTO productResponseDTO = getProductResponseDTO(product);

        return new PublicResponseDTO<>(requestId, 200, "Product retrieved successfully", productResponseDTO);
    }

    public PublicResponseDTO<List<ProductResponseDTO>> getAllProducts(Integer page, Integer size) {
        String requestId = uuidGenerator.get();
        List<Product> products;
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> productPage = productRepository.findAll(pageable);
            products = productPage.getContent();
        } else {
            products = productRepository.findAll();
        }

        List<ProductResponseDTO> productResponseDTOs = products.stream().map(product -> {
            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
            productResponseDTO.setId(product.getId());
            productResponseDTO.setProductName(product.getProductName());
            productResponseDTO.setProductDescription(product.getProductDescription());
            productResponseDTO.setProductCategory(product.getProductCategory());
            productResponseDTO.setProductPrice(product.getProductPrice());
            return productResponseDTO;
        }).collect(Collectors.toList());

        PublicResponseDTO<List<ProductResponseDTO>> responseDTO = new PublicResponseDTO<>();
        responseDTO.setCode(200);
        responseDTO.setMessage("Products retrieved successfully");
        responseDTO.setData(productResponseDTOs);
        responseDTO.setRequestId(requestId);

        return responseDTO;
    }

    // Helper method to build the ProductResponseDTO
    private ProductResponseDTO getProductResponseDTO(Product product) {
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(product.getId());
        productResponseDTO.setProductName(product.getProductName());
        productResponseDTO.setProductDescription(product.getProductDescription());
        productResponseDTO.setProductCategory(product.getProductCategory());
        productResponseDTO.setProductPrice(product.getProductPrice());
        productResponseDTO.setCreatedAt(LocalDateTime.parse(product.getCreatedAt().toString()));
        productResponseDTO.setUpdatedAt(LocalDateTime.parse(product.getUpdatedAt().toString()));
        return productResponseDTO;
    }
}
