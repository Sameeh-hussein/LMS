package com.LibraryManagementSystem.LMS.Services;

import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.dto.AddRoleDto;
import com.LibraryManagementSystem.LMS.dto.ReturnRoleDto;
import com.LibraryManagementSystem.LMS.exceptions.RoleAlreadyExistsException;
import com.LibraryManagementSystem.LMS.exceptions.RoleNotFoundException;
import com.LibraryManagementSystem.LMS.mappers.impl.RoleRequestMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.RoleReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.RoleRepository;
import com.LibraryManagementSystem.LMS.services.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleRequestMapper roleRequestMapper;

    @Mock
    private RoleReturnMapper roleReturnMapper;

    @InjectMocks
    private RoleServiceImpl underTest;

    @Test
    public void testThatAddNewRoleSaveRoleWhenSuccess() {
        AddRoleDto addRoleDto = AddRoleDto.builder().name("ROLE_MEMBER").build();
        Role role = Role.builder().name("ROLE_MEMBER").build();

        when(roleRepository.existsByName(addRoleDto.getName())).thenReturn(false);
        when(roleRequestMapper.mapFrom(addRoleDto)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);

        underTest.addNewRole(addRoleDto);

        verify(roleRepository, times(1)).save(role);
        verify(roleRequestMapper, times(1)).mapFrom(addRoleDto);
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    public void testThatAddNewRoleThrowsRoleAlreadyExistExceptionWhenRoleExist() {
        AddRoleDto addRoleDto = AddRoleDto.builder().name("ROLE_MEMBER").build();

        when(roleRepository.existsByName(addRoleDto.getName())).thenReturn(true);

        RoleAlreadyExistsException roleAlreadyExistsException = assertThrows(RoleAlreadyExistsException.class, () -> {
            underTest.addNewRole(addRoleDto);
        });

        assertNotNull(roleAlreadyExistsException);

        verify(roleRepository, times(1)).existsByName(addRoleDto.getName());
        verify(roleRequestMapper, never()).mapFrom(any(AddRoleDto.class));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testThatFindAllRolesReturnsAllRolesWhenSuccess() {
        Role role1 = Role.builder().name("ROLE_MEMBER").build();
        Role role2 = Role.builder().name("ROLE_ADMIN").build();

        ReturnRoleDto returnRoleDto1 = ReturnRoleDto.builder().name("ROLE_MEMBER").build();
        ReturnRoleDto returnRoleDto2 = ReturnRoleDto.builder().name("ROLE_ADMIN").build();

        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));
        when(roleReturnMapper.mapTo(role1)).thenReturn(returnRoleDto1);
        when(roleReturnMapper.mapTo(role2)).thenReturn(returnRoleDto2);

        List<ReturnRoleDto> roles = underTest.findAll();

        assertEquals(2, roles.size());
        assertEquals("ROLE_MEMBER", roles.get(0).getName());
        assertEquals("ROLE_ADMIN", roles.get(1).getName());

        verify(roleRepository, times(1)).findAll();
        verify(roleReturnMapper, times(1)).mapTo(role1);
        verify(roleReturnMapper, times(1)).mapTo(role2);
    }

    @Test
    public void testThatFindByIdReturnsRoleWhenRoleExists() {
        Role role = Role.builder().id(1L).name("ROLE_MEMBER").build();

        ReturnRoleDto returnRoleDto = ReturnRoleDto.builder().name("ROLE_MEMBER").build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleReturnMapper.mapTo(role)).thenReturn(returnRoleDto);

        ReturnRoleDto result = underTest.findRoleById(1L);

        assertNotNull(result);
        assertEquals("ROLE_MEMBER", result.getName());

        verify(roleRepository, times(1)).findById(1L);
        verify(roleReturnMapper, times(1)).mapTo(role);
    }
    @Test
    public void testThatFindByIdThrowRoleNotFoundExceptionWhenRoleNotExists() {
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> underTest.findRoleById(999L));

        verify(roleRepository, times(1)).findById(999L);
        
        verify(roleReturnMapper, times(0)).mapTo(any(Role.class));
    }

}
