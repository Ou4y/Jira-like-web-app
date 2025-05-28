package com.jira.jira.controller;

import com.jira.jira.models.User;
import com.jira.jira.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ========== GET ALL USERS TESTS ==========

    @Test
    void getAllUsers_ShouldReturnDashboardWithUsersAndCount_Success() {
        // Arrange
        List<User> users = Arrays.asList(
            createUser(1L, "john", "john@example.com"),
            createUser(2L, "jane", "jane@example.com")
        );
        when(userService.getAllUsers()).thenReturn(users);
        when(userService.countUsers()).thenReturn(2L);

        // Act
        ModelAndView mav = userController.getAllUsers();

        // Assert
        assertEquals("dashboard", mav.getViewName());
        assertEquals(users, mav.getModel().get("users"));
        assertEquals(2L, mav.getModel().get("userCount"));
        verify(userService).getAllUsers();
        verify(userService).countUsers();
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsers() {
        // Arrange
        List<User> emptyUsers = new ArrayList<>();
        when(userService.getAllUsers()).thenReturn(emptyUsers);
        when(userService.countUsers()).thenReturn(0L);

        // Act
        ModelAndView mav = userController.getAllUsers();

        // Assert
        assertEquals("dashboard", mav.getViewName());
        assertEquals(emptyUsers, mav.getModel().get("users"));
        assertEquals(0L, mav.getModel().get("userCount"));
    }

    // ========== SHOW ADD USER FORM TESTS ==========

    @Test
    void showAddUserForm_ShouldReturnAddUserViewWithNewUser_Success() {
        // Act
        ModelAndView mav = userController.showAddUserForm();

        // Assert
        assertEquals("add-user", mav.getViewName());
        assertNotNull(mav.getModel().get("user"));
        assertTrue(mav.getModel().get("user") instanceof User);
    }

    // ========== ADD USER TESTS ==========

    @Test
    void addUser_ShouldSaveUserAndRedirect_Success() {
        // Arrange
        User user = createUser(null, "newuser", "newuser@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.doesUsernameExist("newuser")).thenReturn(false);
        when(userService.doesEmailExist("newuser@example.com")).thenReturn(false);

        // Act
        String result = userController.addUser(user, bindingResult, model);

        // Assert
        assertEquals("redirect:/dashboard", result);
        verify(userService).addUser(user);
        verify(userService).doesUsernameExist("newuser");
        verify(userService).doesEmailExist("newuser@example.com");
    }

    @Test
    void addUser_ShouldReturnAddUserView_WhenValidationErrors() {
        // Arrange
        User user = createUser(null, "", ""); // Invalid user
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String result = userController.addUser(user, bindingResult, model);

        // Assert
        assertEquals("add-user", result);
        verify(userService, never()).addUser(any());
        verify(userService, never()).doesUsernameExist(anyString());
        verify(userService, never()).doesEmailExist(anyString());
    }

    @Test
    void addUser_ShouldReturnAddUserViewWithUsernameError_WhenUsernameExists() {
        // Arrange
        User user = createUser(null, "existinguser", "new@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.doesUsernameExist("existinguser")).thenReturn(true);

        // Act
        String result = userController.addUser(user, bindingResult, model);

        // Assert
        assertEquals("add-user", result);
        verify(model).addAttribute("usernameError", "Username already exists");
        verify(userService, never()).addUser(any());
        verify(userService, never()).doesEmailExist(anyString());
    }

    @Test
    void addUser_ShouldReturnAddUserViewWithEmailError_WhenEmailExists() {
        // Arrange
        User user = createUser(null, "newuser", "existing@example.com");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.doesUsernameExist("newuser")).thenReturn(false);
        when(userService.doesEmailExist("existing@example.com")).thenReturn(true);

        // Act
        String result = userController.addUser(user, bindingResult, model);

        // Assert
        assertEquals("add-user", result);
        verify(model).addAttribute("emailError", "Email already exists");
        verify(userService, never()).addUser(any());
    }

    // ========== SHOW EDIT USER FORM TESTS ==========

    @Test
    void showEditUserForm_ShouldReturnEditUserViewWithUser_Success() {
        // Arrange
        Long userId = 1L;
        User user = createUser(userId, "testuser", "test@example.com");
        when(userService.getUserById(userId)).thenReturn(user);

        // Act
        ModelAndView mav = userController.showEditUserForm(userId);

        // Assert
        assertEquals("edit-user", mav.getViewName());
        assertEquals(user, mav.getModel().get("user"));
        verify(userService).getUserById(userId);
    }

    @Test
    void showEditUserForm_ShouldHandleUserNotFound() {
        // Arrange
        Long userId = 999L;
        when(userService.getUserById(userId)).thenReturn(null);

        // Act
        ModelAndView mav = userController.showEditUserForm(userId);

        // Assert
        assertEquals("edit-user", mav.getViewName());
        assertNull(mav.getModel().get("user"));
        verify(userService).getUserById(userId);
    }

    // ========== UPDATE USER TESTS ==========

    @Test
    void updateUser_ShouldUpdateUserAndRedirect_Success() {
        // Arrange
        Long userId = 1L;
        User user = createUser(userId, "updateduser", "updated@example.com");

        // Act
        String result = userController.updateUser(userId, user);

        // Assert
        assertEquals("redirect:/dashboard", result);
        verify(userService).updateUser(userId, user);
    }

    @Test
    void updateUser_ShouldHandleServiceException_WhenUserNotFound() {
        // Arrange
        Long userId = 999L;
        User user = createUser(userId, "nonexistent", "nonexistent@example.com");
        doThrow(new RuntimeException("User not found")).when(userService).updateUser(userId, user);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userController.updateUser(userId, user);
        });
        verify(userService).updateUser(userId, user);
    }

    // ========== DELETE USER TESTS ==========

    @Test
    void deleteUser_ShouldDeleteUserAndRedirect_Success() {
        // Arrange
        Long userId = 1L;

        // Act
        String result = userController.deleteUser(userId);

        // Assert
        assertEquals("redirect:/dashboard", result);
        verify(userService).deleteUser(userId);
    }

    @Test
    void deleteUser_ShouldStillRedirect_WhenUserNotFound() {
        // Arrange
        Long userId = 999L;
        doNothing().when(userService).deleteUser(userId);

        // Act
        String result = userController.deleteUser(userId);

        // Assert
        assertEquals("redirect:/dashboard", result);
        verify(userService).deleteUser(userId);
    }

    // ========== CHECK USERNAME EXISTS TESTS ==========

    @Test
    void checkUsernameExists_ShouldReturnFalse_WhenUsernameAvailable() {
        // Arrange
        String username = "availableuser";
        Long userId = 1L;
        when(userService.doesUsernameExistExceptCurrent(username, userId)).thenReturn(false);

        // Act
        boolean result = userController.checkUsernameExists(username, userId);

        // Assert
        assertFalse(result);
        verify(userService).doesUsernameExistExceptCurrent(username, userId);
    }

    @Test
    void checkUsernameExists_ShouldReturnTrue_WhenUsernameExists() {
        // Arrange
        String username = "existinguser";
        Long userId = 1L;
        when(userService.doesUsernameExistExceptCurrent(username, userId)).thenReturn(true);

        // Act
        boolean result = userController.checkUsernameExists(username, userId);

        // Assert
        assertTrue(result);
        verify(userService).doesUsernameExistExceptCurrent(username, userId);
    }

    @Test
    void checkUsernameExists_ShouldHandleNullUserId() {
        // Arrange
        String username = "testuser";
        Long userId = null;
        when(userService.doesUsernameExistExceptCurrent(username, userId)).thenReturn(false);

        // Act
        boolean result = userController.checkUsernameExists(username, userId);

        // Assert
        assertFalse(result);
        verify(userService).doesUsernameExistExceptCurrent(username, userId);
    }

    // ========== CHECK EMAIL EXISTS TESTS ==========

    @Test
    void checkEmailExists_ShouldReturnFalse_WhenEmailAvailable() {
        // Arrange
        String email = "available@example.com";
        Long userId = 1L;
        when(userService.doesEmailExistExceptCurrent(email, userId)).thenReturn(false);

        // Act
        boolean result = userController.checkEmailExists(email, userId);

        // Assert
        assertFalse(result);
        verify(userService).doesEmailExistExceptCurrent(email, userId);
    }

    @Test
    void checkEmailExists_ShouldReturnTrue_WhenEmailExists() {
        // Arrange
        String email = "existing@example.com";
        Long userId = 1L;
        when(userService.doesEmailExistExceptCurrent(email, userId)).thenReturn(true);

        // Act
        boolean result = userController.checkEmailExists(email, userId);

        // Assert
        assertTrue(result);
        verify(userService).doesEmailExistExceptCurrent(email, userId);
    }

    @Test
    void checkEmailExists_ShouldHandleNullUserId() {
        // Arrange
        String email = "test@example.com";
        Long userId = null;
        when(userService.doesEmailExistExceptCurrent(email, userId)).thenReturn(false);

        // Act
        boolean result = userController.checkEmailExists(email, userId);

        // Assert
        assertFalse(result);
        verify(userService).doesEmailExistExceptCurrent(email, userId);
    }

    // ========== HELPER METHODS ==========

    private User createUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }
}