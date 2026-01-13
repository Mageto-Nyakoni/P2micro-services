package com.revature.smartAppointment.ServiceTest;

import com.revature.smartAppointment.Model.Privilege;
import com.revature.smartAppointment.Repository.PrivilegeRepository;
import com.revature.smartAppointment.Service.PrivilegeService;
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
public class PrivilegeServiceTest {

    @Mock
    private PrivilegeRepository privilegeRepository;

    @InjectMocks
    private PrivilegeService privilegeService;

    @Test
    void testSavePrivilege() {
        Privilege privilege = new Privilege(null, "ADMIN");
        Privilege saved = new Privilege(1, "ADMIN");

        when(privilegeRepository.save(privilege)).thenReturn(saved);

        Privilege result = privilegeService.save(privilege);

        assertEquals(saved, result);
    }

    @Test
    void testFindByIdExists() {
        Privilege privilege = new Privilege(1, "ADMIN");
        when(privilegeRepository.findById(1)).thenReturn(Optional.of(privilege));

        Optional<Privilege> result = privilegeService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(privilege, result.get());
    }

    @Test
    void testFindByIdNotExists() {
        when(privilegeRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Privilege> result = privilegeService.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindAll() {
        Privilege p1 = new Privilege(1, "ADMIN");
        Privilege p2 = new Privilege(2, "USER");
        List<Privilege> list = Arrays.asList(p1, p2);

        when(privilegeRepository.findAll()).thenReturn(list);

        List<Privilege> result = privilegeService.findAll();

        assertEquals(2, result.size());
        assertEquals(list, result);
    }

    @Test
    void testDeleteByIdExists() {
        Privilege privilege = new Privilege(1, "ADMIN");
        when(privilegeRepository.findById(1)).thenReturn(Optional.of(privilege));

        Optional<Privilege> result = privilegeService.deleteById(1);

        assertTrue(result.isPresent());
        verify(privilegeRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteByIdNotExists() {
        when(privilegeRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Privilege> result = privilegeService.deleteById(1);

        assertFalse(result.isPresent());
        verify(privilegeRepository, never()).deleteById(anyInt());
    }

    @Test
    void testUpdateByIdExists() {
        Privilege oldP = new Privilege(1, "USER");
        Privilege newP = new Privilege(null, "ADMIN");

        when(privilegeRepository.findById(1)).thenReturn(Optional.of(oldP));
        when(privilegeRepository.save(oldP)).thenReturn(oldP);

        Privilege result = privilegeService.updateById(1, newP);

        assertEquals("ADMIN", result.getRoleName());
    }

    @Test
    void testUpdateByIdNotExists() {
        Privilege newP = new Privilege(null, "ADMIN");
        when(privilegeRepository.findById(1)).thenReturn(Optional.empty());

        Privilege result = privilegeService.updateById(1, newP);

        assertNull(result);
    }
}