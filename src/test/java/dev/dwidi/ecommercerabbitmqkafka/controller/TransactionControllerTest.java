package dev.dwidi.ecommercerabbitmqkafka.controller;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.transaction.TransactionRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.transaction.TransactionResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.service.TransactionService;
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

class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    @Mock
    private Supplier<String> uuidSupplier;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(transactionController)
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createTransaction_success() throws Exception {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        PublicResponseDTO<TransactionResponseDTO> responseDTO = new PublicResponseDTO<>("requestId", 201, "Transaction created successfully", transactionResponseDTO);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(transactionService.createTransaction(anyLong(), any(TransactionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/transaction/create")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\": 1, \"productQuantity\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Transaction created successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createTransaction_userNotFound() throws Exception {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        String requestId = "a311a58e-59c5-4485-a2f3-ff1a6aee74cd";
        PublicResponseDTO<TransactionResponseDTO> responseDTO = new PublicResponseDTO<>(requestId, 404, "User not found", null);

        when(uuidSupplier.get()).thenReturn(requestId);
        when(transactionService.createTransaction(anyLong(), any(TransactionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/transaction/create")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\": 1, \"productQuantity\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void processTransaction_success() throws Exception {
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        PublicResponseDTO<TransactionResponseDTO> responseDTO = new PublicResponseDTO<>("requestId", 200, "Transaction processed successfully", transactionResponseDTO);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(transactionService.processTransaction(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(put("/api/transaction/process")
                        .param("transactionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Transaction processed successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void processTransaction_transactionNotFound() throws Exception {
        String requestId = "a311a58e-59c5-4485-a2f3-ff1a6aee74cd";
        PublicResponseDTO<TransactionResponseDTO> responseDTO = new PublicResponseDTO<>(requestId, 404, "Transaction not found", null);

        when(uuidSupplier.get()).thenReturn(requestId);
        when(transactionService.processTransaction(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(put("/api/transaction/process")
                        .param("transactionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Transaction not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteTransaction_success() throws Exception {
        PublicResponseDTO<TransactionResponseDTO> responseDTO = new PublicResponseDTO<>("requestId", 200, "Transaction deleted successfully", null);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(transactionService.deleteTransaction(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(delete("/api/transaction/delete")
                        .param("transactionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Transaction deleted successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteTransaction_transactionNotFound() throws Exception {
        String requestId = "a311a58e-59c5-4485-a2f3-ff1a6aee74cd";
        PublicResponseDTO<TransactionResponseDTO> responseDTO = new PublicResponseDTO<>(requestId, 404, "Transaction not found", null);

        when(uuidSupplier.get()).thenReturn(requestId);
        when(transactionService.deleteTransaction(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(delete("/api/transaction/delete")
                        .param("transactionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Transaction not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllTransactions_success() throws Exception {
        List<TransactionResponseDTO> transactionResponseDTOList = List.of(
                new TransactionResponseDTO(), new TransactionResponseDTO()
        );
        PublicResponseDTO<List<TransactionResponseDTO>> responseDTO = new PublicResponseDTO<>("requestId", 200, "Transactions retrieved successfully", transactionResponseDTOList);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(transactionService.getAllTransactions(any(), any())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/transaction/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Transactions retrieved successfully"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getTransactionById_success() throws Exception {
        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        PublicResponseDTO<TransactionResponseDTO> responseDTO = new PublicResponseDTO<>("requestId", 200, "Transaction retrieved successfully", transactionResponseDTO);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(transactionService.getTransactionById(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/transaction")
                        .param("transactionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Transaction retrieved successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getTransactionById_transactionNotFound() throws Exception {
        String requestId = "a311a58e-59c5-4485-a2f3-ff1a6aee74cd";
        PublicResponseDTO<TransactionResponseDTO> responseDTO = new PublicResponseDTO<>(requestId, 404, "Transaction not found", null);

        when(uuidSupplier.get()).thenReturn(requestId);
        when(transactionService.getTransactionById(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/transaction")
                        .param("transactionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Transaction not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getTransactionByUserId_success() throws Exception {
        List<TransactionResponseDTO> transactionResponseDTOList = List.of(
                new TransactionResponseDTO(), new TransactionResponseDTO()
        );
        PublicResponseDTO<List<TransactionResponseDTO>> responseDTO = new PublicResponseDTO<>("requestId", 200, "Transactions retrieved successfully", transactionResponseDTOList);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(transactionService.getTransactionsByUserId(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/transaction/byUser")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Transactions retrieved successfully"))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getTransactionByUserId_userNotFound() throws Exception {
        String requestId = "a311a58e-59c5-4485-a2f3-ff1a6aee74cd";
        PublicResponseDTO<List<TransactionResponseDTO>> responseDTO = new PublicResponseDTO<>(requestId, 404, "User not found", null);

        when(uuidSupplier.get()).thenReturn(requestId);
        when(transactionService.getTransactionsByUserId(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/transaction/byUser")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
