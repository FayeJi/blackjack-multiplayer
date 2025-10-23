package com.example.blackjack.controller;

import com.example.blackjack.dto.UserStatsDto;
import com.example.blackjack.model.User;
import com.example.blackjack.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserRepository mockUserRepository;
    @InjectMocks
    private UserController uut;

    @Test
    void getCurrentUserTest() {
        User mockUser = new User();
        mockUser.setId(123L);
        String expectedUserName = "user1";
        mockUser.setUsername(expectedUserName);

        when(mockUserRepository.findByUsername(eq(expectedUserName))).thenReturn(Optional.of(mockUser));

        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn(expectedUserName);

        ResponseEntity<UserStatsDto> actualResponse = uut.getCurrentUser(mockAuthentication);

        assertEquals(expectedUserName, actualResponse.getBody().getUsername());
    }
}
