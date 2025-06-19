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
    public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionRequest request, HttpSession session) {
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("Unauthorized - Please login first");
        }

        Transaction transaction = transactionService.createTransaction(request, session);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    public ResponseEntity<?> getAllTransactions(HttpSession session) {
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("Unauthorized - Please login first");
        }

        List<Transaction> transactions = transactionService.getUserTransactions(session);
        return ResponseEntity.ok(transactions);
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

    @GetMapping("/check")
    public ResponseEntity<String> check(HttpSession Session) {
        return ResponseEntity.ok("Transaction is Up and Running");
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
