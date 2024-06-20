package dev.dwidi.ecommercerabbitmqkafka.controller;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.product.ProductRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.product.ProductResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @Mock
    private Supplier<String> uuidSupplier;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createProduct_success() throws Exception {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        PublicResponseDTO<ProductResponseDTO> responseDTO = new PublicResponseDTO<>("requestId", 201, "Product created successfully", productResponseDTO);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Product1\", \"price\": 100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Product created successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createProduct_productNotFound() throws Exception {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        String requestId = "a311a58e-59c5-4485-a2f3-ff1a6aee74cd";
        PublicResponseDTO<ProductResponseDTO> responseDTO = new PublicResponseDTO<>(requestId, 404, "Product not found", null);

        when(uuidSupplier.get()).thenReturn(requestId);
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Product1\", \"price\": 100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Product not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateProduct_success() throws Exception {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        PublicResponseDTO<ProductResponseDTO> responseDTO = new PublicResponseDTO<>("requestId", 200, "Product updated successfully", productResponseDTO);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(productService.updateProduct(anyLong(), any(ProductRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/product/update")
                        .param("productId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"UpdatedProduct\", \"price\": 150}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Product updated successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateProduct_productNotFound() throws Exception {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        String requestId = "a311a58e-59c5-4485-a2f3-ff1a6aee74cd";
        PublicResponseDTO<ProductResponseDTO> responseDTO = new PublicResponseDTO<>(requestId, 404, "Product not found", null);

        when(uuidSupplier.get()).thenReturn(requestId);
        when(productService.updateProduct(anyLong(), any(ProductRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/product/update")
                        .param("productId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"UpdatedProduct\", \"price\": 150}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Product not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteProduct_success() throws Exception {
        PublicResponseDTO<ProductResponseDTO> responseDTO = new PublicResponseDTO<>("requestId", 200, "Product deleted successfully", null);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(productService.deleteProduct(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(delete("/api/product/delete")
                        .param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteProduct_productNotFound() throws Exception {
        String requestId = "a311a58e-59c5-4485-a2f3-ff1a6aee74cd";
        PublicResponseDTO<ProductResponseDTO> responseDTO = new PublicResponseDTO<>(requestId, 404, "Product not found", null);

        when(uuidSupplier.get()).thenReturn(requestId);
        when(productService.deleteProduct(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(delete("/api/product/delete")
                        .param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Product not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllProducts_success() throws Exception {
        List<ProductResponseDTO> productResponseDTOList = List.of(
                new ProductResponseDTO(), new ProductResponseDTO()
        );
        PublicResponseDTO<List<ProductResponseDTO>> responseDTO = new PublicResponseDTO<>("requestId", 200, "Products retrieved successfully", productResponseDTOList);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(productService.getAllProducts(any(), any())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Products retrieved successfully"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getProductById_success() throws Exception {
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        PublicResponseDTO<ProductResponseDTO> responseDTO = new PublicResponseDTO<>("requestId", 200, "Product retrieved successfully", productResponseDTO);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(productService.getProductById(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/product")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Product retrieved successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getProductById_productNotFound() throws Exception {
        String requestId = "a311a58e-59c5-4485-a2f3-ff1a6aee74cd";
        PublicResponseDTO<ProductResponseDTO> responseDTO = new PublicResponseDTO<>(requestId, 404, "Product not found", null);

        when(uuidSupplier.get()).thenReturn(requestId);
        when(productService.getProductById(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/product")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Product not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
