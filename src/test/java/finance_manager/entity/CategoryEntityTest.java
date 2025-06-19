package finance_manager.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryEntityTest {
    @Mock
    private Category category;
    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        category = new Category();

        user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");
    }

    @Test
    void testCategoryCreation() {
        String name = "Salary";
        TransactionType type = TransactionType.INCOME;

        category.setName(name);
        category.setType(type);
        category.setCustom(true);
        category.setUser(user);

        assertNotNull(category);
        assertEquals(name, category.getName());
        assertEquals(type, category.getType());
        assertTrue(category.isCustom());
        assertEquals(user, category.getUser());
    }

    @Test
    void testCategoryWithAllArgsConstructor() {
        Long id = 1L;
        String name = "Food";
        TransactionType type = TransactionType.EXPENSE;
        boolean isCustom = false;

        Category newCategory = new Category(id, name, type, isCustom, user);

        assertEquals(id, newCategory.getId());
        assertEquals(name, newCategory.getName());
        assertEquals(type, newCategory.getType());
        assertEquals(isCustom, newCategory.isCustom());
        assertEquals(user, newCategory.getUser());
    }

    @Test
    void testCategoryWithNoArgsConstructor() {
        Category newCategory = new Category();

        assertNotNull(newCategory);
        assertNull(newCategory.getId());
        assertNull(newCategory.getName());
        assertNull(newCategory.getType());
        assertFalse(newCategory.isCustom()); // Default value
        assertNull(newCategory.getUser());
    }

    @Test
    void testDefaultCustomFlag() {
        Category newCategory = new Category();

        assertFalse(newCategory.isCustom()); // Default should be false
    }

    @Test
    void testCategoryIncomeType() {
        category.setName("Salary");
        category.setType(TransactionType.INCOME);

        assertEquals(TransactionType.INCOME, category.getType());
        assertEquals("Salary", category.getName());
    }

    @Test
    void testCategoryExpenseType() {
        category.setName("Rent");
        category.setType(TransactionType.EXPENSE);

        assertEquals(TransactionType.EXPENSE, category.getType());
        assertEquals("Rent", category.getName());
    }

    @Test
    void testCategoryUserRelationship() {
        category.setUser(user);

        assertEquals(user, category.getUser());
        assertEquals(1L, category.getUser().getId());
    }

    @Test
    void testCategoryEqualsAndHashCode() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Salary");
        category1.setType(TransactionType.INCOME);
        category1.setCustom(false);

        Category category2 = new Category();
        category2.setId(1L);
        category2.setName("Salary");
        category2.setType(TransactionType.INCOME);
        category2.setCustom(false);

        assertEquals(category1, category2);
        assertEquals(category1.hashCode(), category2.hashCode());
    }

    @Test
    void testCategoryToString() {
        category.setId(1L);
        category.setName("Salary");
        category.setType(TransactionType.INCOME);

        String categoryString = category.toString();

        assertNotNull(categoryString);
        assertTrue(categoryString.contains("Salary"));
        assertTrue(categoryString.contains("INCOME"));
    }

    @Test
    void testDefaultVsCustomCategory() {
        Category defaultCategory = new Category();
        defaultCategory.setName("Salary");
        defaultCategory.setType(TransactionType.INCOME);
        defaultCategory.setCustom(false);

        Category customCategory = new Category();
        customCategory.setName("Freelance");
        customCategory.setType(TransactionType.INCOME);
        customCategory.setCustom(true);
        customCategory.setUser(user);

        assertFalse(defaultCategory.isCustom());
        assertNull(defaultCategory.getUser());

        assertTrue(customCategory.isCustom());
        assertEquals(user, customCategory.getUser());
    }

}