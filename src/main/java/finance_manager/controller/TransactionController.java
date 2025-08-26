package finance_manager.controller;

import finance_manager.dto.TransactionRequest;
import finance_manager.entity.Transaction;
import finance_manager.entity.TransactionType;
import finance_manager.security.SecurityUtils;
import finance_manager.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionRequest request) {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return ResponseEntity.status(401).body("Unauthorized - Please login first");
        }

        Transaction transaction = transactionService.createTransaction(request, username);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        String username = SecurityUtils.getCurrentUsername();
        List<Transaction> transactions = transactionService.getUserTransactions(username);
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionRequest request, HttpSession session) {
        String username = SecurityUtils.getCurrentUsername();
        Transaction updatedTransaction = transactionService.updateTransaction(id, request, username);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        String username = SecurityUtils.getCurrentUsername();
        transactionService.deleteTransaction(id, username);
        return ResponseEntity.ok("Transaction deleted with id: " + id);
    }

    @GetMapping("/check")
    public ResponseEntity<String> check(HttpSession Session) {
        return ResponseEntity.ok("Transaction is Up and Running");
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Transaction>> filter(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false)TransactionType type)
    {
        String username = SecurityUtils.getCurrentUsername();
        List<Transaction> filtered = transactionService.filterTransactions(startDate, endDate, category, type, username);
        return ResponseEntity.ok(filtered);
    }
}
