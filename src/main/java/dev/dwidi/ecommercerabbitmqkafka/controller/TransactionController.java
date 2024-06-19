package dev.dwidi.ecommercerabbitmqkafka.controller;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.transaction.TransactionRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.transaction.TransactionResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PreAuthorize("@customSecurityExpression.isAdminOrSelf(#userId)")
    @PostMapping("/create")
    public PublicResponseDTO<TransactionResponseDTO> createTransaction(
            @RequestParam Long userId,
            @RequestBody TransactionRequestDTO transactionRequestDTO) {
        return transactionService.createTransaction(userId, transactionRequestDTO);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/process")
    public PublicResponseDTO<TransactionResponseDTO> processTransaction(Long transactionId) {
        return transactionService.processTransaction(transactionId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete")
    public PublicResponseDTO<TransactionResponseDTO> deleteTransaction(@RequestParam Long transactionId) {
        return transactionService.deleteTransaction(transactionId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public PublicResponseDTO<List<TransactionResponseDTO>> getAllTransactions(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size)  {
        return transactionService.getAllTransactions(page, size);
    }

    @PreAuthorize("@customSecurityExpression.isAdminOrSelf(#userId)")
    @GetMapping("/byUser")
    public PublicResponseDTO<List<TransactionResponseDTO>> getTransactionByUserId(@RequestParam Long userId) {
        return transactionService.getTransactionsByUserId(userId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    PublicResponseDTO<TransactionResponseDTO> getTransactionById(@RequestParam Long transactionId) {
        return transactionService.getTransactionById(transactionId);
    }
}
