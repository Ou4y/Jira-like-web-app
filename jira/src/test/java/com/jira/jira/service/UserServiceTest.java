package com.jira.jira.service;

import com.jira.jira.models.User;
import com.jira.jira.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User sampleUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");
        sampleUser.setEmail("test@example.com");
        sampleUser.setPassword("password");
    }

    // 1. getUserByUsername
    @Test
    public void getUserByUsername_success() {
        when(userRepository.findByUsername("testuser")).thenReturn(sampleUser);
        Optional<User> result = userService.getUserByUsername("testuser");
        assertTrue(result.isPresent());
    }

    @Test
    public void getUserByUsername_fail() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);
        Optional<User> result = userService.getUserByUsername("unknown");
        assertFalse(result.isPresent());
    }

    // 2. getAllUsers
    @Test
    public void getAllUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));
        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
    }

    @Test
    public void getAllUsers_fail() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<User> users = userService.getAllUsers();
        assertTrue(users.isEmpty());
    }

    // 3. getUserById
    @Test
    public void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        User result = userService.getUserById(1L);
        assertNotNull(result);
    }

    @Test
    public void getUserById_fail() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        User result = userService.getUserById(2L);
        assertNull(result);
    }

    // 4. addUser
    @Test
    public void addUser_success() {
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        User saved = userService.addUser(sampleUser);
        assertNotNull(saved);
    }

    @Test
    public void addUser_fail() {
        when(userRepository.save(null)).thenThrow(new IllegalArgumentException());
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(null));
    }

    // 5. updateUser
    @Test
    public void updateUser_success() {
        User newDetails = new User();
        newDetails.setUsername("newuser");
        newDetails.setEmail("new@example.com");
        newDetails.setPassword("newpass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User result = userService.updateUser(1L, newDetails);
        assertNotNull(result);
    }

    @Test
    public void updateUser_fail() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        User result = userService.updateUser(2L, sampleUser);
        assertNull(result);
    }

    // 6. deleteUser
    @Test
    public void deleteUser_success() {
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteUser_fail() {
        doThrow(new RuntimeException("User not found")).when(userRepository).deleteById(99L);
        assertThrows(RuntimeException.class, () -> userService.deleteUser(99L));
    }

    // 7. countUsers
    @Test
    public void countUsers_success() {
        when(userRepository.count()).thenReturn(10L);
        assertEquals(10L, userService.countUsers());
    }

    @Test
    public void countUsers_fail() {
        when(userRepository.count()).thenReturn(0L);
        assertEquals(0L, userService.countUsers());
    }

    // 8. doesUsernameExist
    @Test
    public void doesUsernameExist_success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        assertTrue(userService.doesUsernameExist("testuser"));
    }

    @Test
    public void doesUsernameExist_fail() {
        when(userRepository.existsByUsername("unknown")).thenReturn(false);
        assertFalse(userService.doesUsernameExist("unknown"));
    }

    // 9. doesEmailExist
    @Test
    public void doesEmailExist_success() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertTrue(userService.doesEmailExist("test@example.com"));
    }

    @Test
    public void doesEmailExist_fail() {
        when(userRepository.existsByEmail("noemail@example.com")).thenReturn(false);
        assertFalse(userService.doesEmailExist("noemail@example.com"));
    }

    // 10. doesUsernameExistExceptCurrent
    @Test
    public void doesUsernameExistExceptCurrent_success() {
        when(userRepository.existsByUsernameAndIdNot("testuser", 2L)).thenReturn(true);
        assertTrue(userService.doesUsernameExistExceptCurrent("testuser", 2L));
    }

    @Test
    public void doesUsernameExistExceptCurrent_fail() {
        when(userRepository.existsByUsernameAndIdNot("testuser", 1L)).thenReturn(false);
        assertFalse(userService.doesUsernameExistExceptCurrent("testuser", 1L));
    }

    // 11. doesEmailExistExceptCurrent
    @Test
    public void doesEmailExistExceptCurrent_success() {
        when(userRepository.existsByEmailAndIdNot("test@example.com", 2L)).thenReturn(true);
        assertTrue(userService.doesEmailExistExceptCurrent("test@example.com", 2L));
    }

    @Test
    public void doesEmailExistExceptCurrent_fail() {
        when(userRepository.existsByEmailAndIdNot("test@example.com", 1L)).thenReturn(false);
        assertFalse(userService.doesEmailExistExceptCurrent("test@example.com", 1L));
    }

    // 12. findByUsername
    @Test
    public void findByUsername_success() {
        when(userRepository.findByUsername("testuser")).thenReturn(sampleUser);
        assertNotNull(userService.findByUsername("testuser"));
    }

    @Test
    public void findByUsername_fail() {
        when(userRepository.findByUsername("nouser")).thenReturn(null);
        assertNull(userService.findByUsername("nouser"));
    }

    // 13. findByEmail
    @Test
    public void findByEmail_success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(sampleUser);
        assertNotNull(userService.findByEmail("test@example.com"));
    }

    @Test
    public void findByEmail_fail() {
        when(userRepository.findByEmail("no@example.com")).thenReturn(null);
        assertNull(userService.findByEmail("no@example.com"));
    }

    // 14. save
    @Test
    public void save_success() {
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        userService.save(sampleUser);
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    public void save_fail() {
        doThrow(new RuntimeException("Save error")).when(userRepository).save(null);
        assertThrows(RuntimeException.class, () -> userService.save(null));
    }

    // 15. authenticate
    @Test
    public void authenticate_success() {
        when(userRepository.findByUsername("testuser")).thenReturn(sampleUser);
        User result = userService.authenticate("testuser", "password");
        assertNotNull(result);
    }

    @Test
    public void authenticate_fail() {
        when(userRepository.findByUsername("testuser")).thenReturn(sampleUser);
        User result = userService.authenticate("testuser", "wrong");
        assertNull(result);
    }

    // 16. authenticateByEmail
    @Test
    public void authenticateByEmail_success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(sampleUser);
        User result = userService.authenticateByEmail("test@example.com", "password");
        assertNotNull(result);
    }

    @Test
    public void authenticateByEmail_fail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(sampleUser);
        User result = userService.authenticateByEmail("test@example.com", "wrong");
        assertNull(result);
    }
}