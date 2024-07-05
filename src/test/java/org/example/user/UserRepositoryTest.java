package org.example.user;

import org.example.Main;
import org.example.task.Task;
import org.example.user.User;
import org.example.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ContextConfiguration(classes = Main.class)
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void shouldSaveUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("userPass");
        userRepository.save(user);

        List<User> userList = userRepository.findAll();

        assertThat(userList).hasSize(1);
        assertThat(userList).contains(user);


    }

    @Test
    public void shouldFindById() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("userPass");
        userRepository.save(user);

        Long id = user.getId();

        Optional<User> foundUser = userRepository.findById(id);
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(user);

        Optional<User> notFoundUser = userRepository.findById(999L);
        assertThat(notFoundUser).isNotPresent();
    }

    @Test
    public void shouldFindAll() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPassword("user1Pass");

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPassword("user2Pass");

        userRepository.save(user1);
        userRepository.save(user2);

        List<User> userList =userRepository.findAll();
        assertThat(userList).hasSize(2);
        assertThat(userList).contains(user1, user2);
    }

    @Test
    public void shouldDeleteById() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPassword("user1Pass");

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPassword("user2Pass");

        userRepository.save(user1);
        userRepository.save(user2);

        Long id = user1.getId();
        userRepository.deleteById(id);

        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(1);
        assertThat(userList).doesNotContain(user1);
        assertThat(userList).contains(user2);
    }
}
