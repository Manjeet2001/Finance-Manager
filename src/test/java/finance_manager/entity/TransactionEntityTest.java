package finance_manager.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionEntityTest {

    private Transaction transaction;
    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();

        user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");

        category = new Category();
        category.setId(1L);
        category.setName("Salary");
        category.setType(TransactionType.INCOME);
    }

    @Test
    void testTransactionCreation() {
        Double amount = 1000.0;
        LocalDate date = LocalDate.of(2025, 6, 18);
        String description = "Monthly salary";

        transaction.setAmount(amount);
        transaction.setDate(date);
        transaction.setDescription(description);
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setType(TransactionType.INCOME);

        assertNotNull(transaction);
        assertEquals(amount, transaction.getAmount());
        assertEquals(date, transaction.getDate());
        assertEquals(description, transaction.getDescription());
        assertEquals(user, transaction.getUser());
        assertEquals(category, transaction.getCategory());
        assertEquals(TransactionType.INCOME, transaction.getType());
    }

    @Test
    void testTransactionWithAllArgsConstructor() {
        Long id = 1L;
        Double amount = 500.0;
        LocalDate date = LocalDate.of(2025, 6, 19);
        String description = "Groceries";

        Transaction newTransaction = new Transaction(id, amount, date, description, category, user, TransactionType.EXPENSE);

        assertEquals(id, newTransaction.getId());
        assertEquals(amount, newTransaction.getAmount());
        assertEquals(date, newTransaction.getDate());
        assertEquals(description, newTransaction.getDescription());
        assertEquals(category, newTransaction.getCategory());
        assertEquals(user, newTransaction.getUser());
        assertEquals(TransactionType.EXPENSE, newTransaction.getType());
    }

    @Test
    void testTransactionWithNoArgsConstructor() {
        Transaction newTransaction = new Transaction();

        assertNotNull(newTransaction);
        assertNull(newTransaction.getId());
        assertNull(newTransaction.getAmount());
        assertNull(newTransaction.getDate());
        assertNull(newTransaction.getDescription());
        assertNull(newTransaction.getCategory());
        assertNull(newTransaction.getUser());
        assertNull(newTransaction.getType());
    }

    @Test
    void testTransactionEqualsAndHashCode() {
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setAmount(1000.0);
        transaction1.setDate(LocalDate.of(2025, 6, 18));
        transaction1.setDescription("Test");
        transaction1.setType(TransactionType.INCOME);

        Transaction transaction2 = new Transaction();
        transaction2.setId(1L);
        transaction2.setAmount(1000.0);
        transaction2.setDate(LocalDate.of(2025, 6, 18));
        transaction2.setDescription("Test");
        transaction2.setType(TransactionType.INCOME);

        assertEquals(transaction1, transaction2);
        assertEquals(transaction1.hashCode(), transaction2.hashCode());
    }

    @Test
    void testTransactionToString() {
        transaction.setId(1L);
        transaction.setAmount(1000.0);
        transaction.setDescription("Test Transaction");

        String transactionString = transaction.toString();

        assertNotNull(transactionString);
        assertTrue(transactionString.contains("1000.0"));
        assertTrue(transactionString.contains("Test Transaction"));
    }

    @Test
    void testTransactionUserRelationship() {
        transaction.setUser(user);

        assertEquals(user, transaction.getUser());
        assertEquals(1L, transaction.getUser().getId());
    }

    @Test
    void testTransactionCategoryRelationship() {
        transaction.setCategory(category);

        assertEquals(category, transaction.getCategory());
        assertEquals("Salary", transaction.getCategory().getName());
        assertEquals(TransactionType.INCOME, transaction.getCategory().getType());
    }

}