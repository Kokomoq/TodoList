package org.example.user;

import org.example.user.User;
import org.example.user.UserRepository;
import org.example.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@example.com");
        user.setPassword("userPass");
    }

    @Test
    public void shouldSaveUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.saveUser(user);

        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        verify(userRepository, times(1)).save(user);
        verify(passwordEncoder, times(1)).encode("userPass");
    }

    @Test
    public void shouldFindByEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        User foundUser = userService.findByEmail(user.getEmail());
        assertThat(foundUser).isEqualTo(user);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void shouldLoadUserByUsername() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    public void shouldNotLoadUserByUsername() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(user.getEmail());
        });
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }
}
