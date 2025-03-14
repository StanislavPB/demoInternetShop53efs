package org.demointernetshop53efs.controller;

import lombok.AllArgsConstructor;
import org.demointernetshop53efs.entity.ConfirmationCode;
import org.demointernetshop53efs.entity.User;
import org.demointernetshop53efs.repository.ConfirmationCodeRepository;
import org.demointernetshop53efs.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

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
    void testRegisterUser() throws Exception {
        String newUserJson = """
                {
                "firstName":"John",
                "lastName":"John",
                "email":"john@gmail.com",
                "hashPassword":"Pass12345!"
                }
                """;

        String requestPath = "/api/public/register";

        mockMvc.perform(post(requestPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void testReturn400ForBadFormatEmail() throws Exception {
        String newUserJson = """
                {
                "firstName":"John",
                "lastName":"John",
                "email":"badFormatEmail",
                "hashPassword":"Pass12345!"
                }
                """;

        String requestPath = "/api/public/register";

        mockMvc.perform(post(requestPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].rejectedValue").value("badFormatEmail"));

    }

    @Test
    void testReturn409ForExistEmail() throws Exception {
        String newUserJson = """
                {
                "firstName":"John",
                "lastName":"John",
                "email":"user1@gmail.com",
                "hashPassword":"Pass12345!"
                }
                """;

        String requestPath = "/api/public/register";
        String errorMessage = "Пользователь с email: user1@gmail.com уже зарегистрирован";

        mockMvc.perform(post(requestPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(errorMessage));


    }

    @Test
    void testConfirmRegistration() throws Exception {

        String requestPath = "/api/public/confirm";
        String requestParamName = "code";
        String requestParamValue = "someConfirmationCode";
        String expectedValue = "user1@gmail.com";

        mockMvc.perform(get(requestPath)
                .param(requestParamName,requestParamValue))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(expectedValue));
    }





}