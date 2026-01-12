package com.revature.smartAppointment.ServiceTest;

import com.revature.smartAppointment.Model.Privilege;
import com.revature.smartAppointment.Model.User;
import com.revature.smartAppointment.Repository.PrivilegeRepository;
import com.revature.smartAppointment.Repository.UserRepository;
import com.revature.smartAppointment.Service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PrivilegeRepository privilegeRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testSaveUser() {
        Privilege privilege = new Privilege(1, "ADMIN");
        User user = new User(null, "email@test.com", "pass", "John", "Doe", privilege);
        User savedUser = new User(1, "email@test.com", "pass", "John", "Doe", privilege);

        when(userRepository.save(user)).thenReturn(savedUser);

        User result = userService.save(user);

        assertEquals(savedUser, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testFindByIdFound() {
        User user = new User(1, "email@test.com", "pass", "John", "Doe", null);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testFindByIdNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll() {
        User user1 = new User(1, "a@test.com", "pass", "John", "Doe", null);
        User user2 = new User(2, "b@test.com", "pass", "Jane", "Smith", null);
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertEquals(2, result.size());
        assertEquals(users, result);
    }

    @Test
    void testDeleteByIdExists() {
        User user = new User(1, "email@test.com", "pass", "John", "Doe", null);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> result = userService.deleteById(1);

        assertTrue(result.isPresent());
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteByIdNotExists() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Optional<User> result = userService.deleteById(1);

        assertFalse(result.isPresent());
        verify(userRepository, never()).deleteById(anyInt());
    }

    @Test
    void testUpdateByIdExists() {
        Privilege privilege = new Privilege(1, "ADMIN");
        User existing = new User(1, "old@test.com", "oldpass", "Old", "Name", privilege);
        User updated = new User(null, "new@test.com", "newpass", "New", "Name", privilege);

        when(userRepository.findById(1)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User result = userService.updateById(1, updated);

        assertEquals("new@test.com", result.getEmail());
        assertEquals("newpass", result.getPassword());
        assertEquals("New", result.getFirstName());
        assertEquals("Name", result.getLastName());
        assertEquals(privilege, result.getPrivilege());
    }

    @Test
    void testUpdateByIdNotExists() {
        User updated = new User(null, "new@test.com", "newpass", "New", "Name", null);

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        User result = userService.updateById(1, updated);

        assertNull(result);
    }

    @Test
    void testFindUserByEmailFound() {
        User user = new User(1, "test@test.com", "pass", "John", "Doe", null);
        when(userRepository.findUserByEmail("test@test.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserByEmail("test@test.com");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testFindUserByEmailNotFound() {
        when(userRepository.findUserByEmail("notfound@test.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findUserByEmail("notfound@test.com");

        assertFalse(result.isPresent());
    }
}