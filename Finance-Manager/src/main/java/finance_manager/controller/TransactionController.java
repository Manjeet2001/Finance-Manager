package finance_manager.controller;

import finance_manager.dto.TransactionRequest;
import finance_manager.entity.Transaction;
import finance_manager.entity.TransactionType;
import finance_manager.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionRequest request, HttpSession session) {
        return ResponseEntity.ok(transactionService.createTransaction(request, session));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(HttpSession session) {
        return ResponseEntity.ok(transactionService.getUserTransactions(session));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionRequest request, HttpSession session) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, request, session));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id, HttpSession session) {
        transactionService.deleteTransaction(id, session);
        return ResponseEntity.ok("Transaction deleted with id: " + id);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Transaction>> filter(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false)TransactionType type, HttpSession session)
    {
        return ResponseEntity.ok(transactionService.filterTransactions(startDate, endDate, category, type, session));
    }
}
