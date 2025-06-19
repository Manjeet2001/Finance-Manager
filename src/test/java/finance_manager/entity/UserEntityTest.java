package finance_manager.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testUserCreation() {
        String username = "john.doe@example.com";
        String password = "password123";
        String fullName = "John Doe";
        String phoneNumber = "+1234567890";

        user.setUsername(username);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);

        assertNotNull(user);
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(fullName, user.getFullName());
        assertEquals(phoneNumber, user.getPhoneNumber());
        assertEquals("ROLE_USER", user.getRole()); // Default role
    }

    @Test
    void testDefaultRoleAssignment() {
        User newUser = new User();

        assertEquals("ROLE_USER", newUser.getRole());
    }

    @Test
    void testGetAuthorities_DefaultRole() {
        user.setRole("ROLE_USER");

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testGetAuthorities_WithoutRolePrefix() {
        user.setRole("ADMIN");

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testGetAuthorities_WithRolePrefix() {
        user.setRole("ROLE_ADMIN");

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testGetAuthorities_EmptyRole() {
        user.setRole("");

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertTrue(authorities.isEmpty());
    }

    @Test
    void testGetAuthorities_NullRole() {
        user.setRole(null);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertTrue(authorities.isEmpty());
    }

    @Test
    void testGetAuthorities_WhitespaceRole() {
        user.setRole("   ");

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertTrue(authorities.isEmpty());
    }

    @Test
    void testUserDetailsImplementation() {
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("test@example.com");
        user1.setPassword("password");
        user1.setFullName("Test User");
        user1.setPhoneNumber("+1234567890");

        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("test@example.com");
        user2.setPassword("password");
        user2.setFullName("Test User");
        user2.setPhoneNumber("+1234567890");

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        user.setId(1L);
        user.setUsername("test@example.com");
        user.setFullName("Test User");

        String userString = user.toString();

        assertNotNull(userString);
        assertTrue(userString.contains("test@example.com"));
        assertTrue(userString.contains("Test User"));
    }
}