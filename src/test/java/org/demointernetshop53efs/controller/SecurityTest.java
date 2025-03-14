package org.demointernetshop53efs.controller;

import org.demointernetshop53efs.entity.ConfirmationCode;
import org.demointernetshop53efs.entity.User;
import org.demointernetshop53efs.repository.ConfirmationCodeRepository;
import org.demointernetshop53efs.repository.UserRepository;
import org.demointernetshop53efs.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp(){
        User testUser = new User();
        testUser.setFirstName("user1");
        testUser.setLastName("user1");
        testUser.setEmail("user1@gmail.com");
        testUser.setHashPassword("Pass12345!");
        testUser.setRole(User.Role.USER);
        testUser.setState(User.State.NOT_CONFIRMED);
        User savedUser = userRepository.save(testUser);

        ConfirmationCode code = new ConfirmationCode();
        code.setCode("someConfirmationCode");
        code.setUser(savedUser);
        code.setExpiredDataTime(LocalDateTime.now().plusDays(1));
        confirmationCodeRepository.save(code);
    }

    @AfterEach
    void drop(){
        confirmationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testWhenNoAuthenticationThenReturn403Users() throws Exception {
        String requestPath = "/api/users";
        mockMvc.perform(get(requestPath)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    public void testWhenNoAuthenticationThenReturn403Admins() throws Exception {
        String requestPath = "/api/admins/users";
        mockMvc.perform(get(requestPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }


    @Test
    @WithMockUser(username = "user",roles = {"USER"})
    public void testWhenNoAuthorizeRoleThenReturn403Admins() throws Exception {
        String requestPath = "/api/admins/users";
        mockMvc.perform(get(requestPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "admin",roles = {"ADMIN"})
    public void testWhenReturn200ForAdminRequest() throws Exception {
        String requestPath = "/api/admins/users";
        mockMvc.perform(get(requestPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }




}
