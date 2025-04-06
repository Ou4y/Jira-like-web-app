package com.jira.jira.controller;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.jira.jira.models.User;
import com.jira.jira.service.UserService;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userService.getAllUsers()).thenReturn(users);
        when(userService.countUsers()).thenReturn(2L);

        String viewName = userController.getAllUsers(model);

        assertEquals("index", viewName);
        verify(model).addAttribute("users", users);
        verify(model).addAttribute("userCount", 2L);
    }

    @Test
    void testShowAddUserForm() {
        String viewName = userController.showAddUserForm(model);
        assertEquals("add-user", viewName);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    @Test
    void testAddUser_ValidUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.doesUsernameExist("testuser")).thenReturn(false);
        when(userService.doesEmailExist("test@example.com")).thenReturn(false);

        String result = userController.addUser(user, bindingResult, model);

        assertEquals("redirect:/users", result);
        verify(userService).addUser(user);
    }

    @Test
    void testAddUser_WithValidationErrors() {
        User user = new User();
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = userController.addUser(user, bindingResult, model);

        assertEquals("add-user", result);
        verify(userService, never()).addUser(any());
    }

    @Test
    void testAddUser_ExistingUsername() {
        User user = new User();
        user.setUsername("existinguser");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.doesUsernameExist("existinguser")).thenReturn(true);

        String result = userController.addUser(user, bindingResult, model);

        assertEquals("add-user", result);
        verify(model).addAttribute(eq("usernameError"), anyString());
        verify(userService, never()).addUser(any());
    }

    @Test
    void testAddUser_ExistingEmail() {
        User user = new User();
        user.setUsername("uniqueuser");
        user.setEmail("existing@example.com");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.doesUsernameExist("uniqueuser")).thenReturn(false);
        when(userService.doesEmailExist("existing@example.com")).thenReturn(true);

        String result = userController.addUser(user, bindingResult, model);

        assertEquals("add-user", result);
        verify(model).addAttribute(eq("emailError"), anyString());
        verify(userService, never()).addUser(any());
    }

    @Test
    void testShowEditUserForm() {
        User user = new User();
        when(userService.getUserById(1L)).thenReturn(user);

        String result = userController.showEditUserForm(1L, model);

        assertEquals("edit-user", result);
        verify(model).addAttribute("user", user);
    }

    @Test
    void testUpdateUser() {
        User user = new User();

        String result = userController.updateUser(1L, user);

        assertEquals("redirect:/users", result);
        verify(userService).updateUser(1L, user);
    }

    @Test
    void testDeleteUser() {
        String result = userController.deleteUser(1L);

        assertEquals("redirect:/users", result);
        verify(userService).deleteUser(1L);
    }
}
