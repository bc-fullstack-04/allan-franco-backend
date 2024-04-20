package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UsersServiceTest {
    @Autowired
    private UsersService usersService;

    @MockBean
    private UsersRepository usersRepository;

    @Test
    @DisplayName("Should return users when valid users is saved")
    public void shouldReturnUsersWhenValidUsersIsSaved() {
        Users users = Users.builder().id(1L).name("teste").email("test").password("teste").build();
        when(usersRepository.save(any(Users.class))).thenReturn(users);

        Users savedUsers = usersService.save(users);

        assertEquals(users, savedUsers);
    }
}
