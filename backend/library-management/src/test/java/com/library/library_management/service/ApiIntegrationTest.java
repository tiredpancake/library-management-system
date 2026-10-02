package com.library.library_management.controller;

import com.library.library_management.entity.AppUser;
import com.library.library_management.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;



    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String username;
    private String password;

    @BeforeEach
    void setUp() {

        username = "api-test-" + UUID.randomUUID();

        password = "test123";

        AppUser user = new AppUser();

        user.setUsername(username);

        user.setPassword(passwordEncoder.encode(password));

        appUserRepository.save(user);
    }

    @Test
    void unauthenticatedRequestShouldReturn401() throws Exception {

        mockMvc.perform(get("/api/books")).andExpect(status().isUnauthorized());
    }

    @Test
    void invalidMemberNationalCodeShouldReturn400() throws Exception {

        String body = """
                {
                  "fullName": "API Test Member",
                  "nationalCode": "123",
                  "birthDate": "2000-01-01",
                  "membershipType": "INDIVIDUAL",
                  "phone": "09120000000",
                  "address": "Test Address",
                  "postalCode": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/members").with(httpBasic(username, password)).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.field").value("nationalCode"));
    }

    @Test
    void invalidPhoneShouldReturn400() throws Exception {

        String body = """
                {
                  "fullName": "API Test Member",
                  "nationalCode": "1234567890",
                  "birthDate": "2000-01-01",
                  "membershipType": "INDIVIDUAL",
                  "phone": "123",
                  "address": "Test Address",
                  "postalCode": "1234567890"
                }
                """;

        mockMvc.perform(post("/api/members").with(httpBasic(username, password)).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400)).andExpect(jsonPath("$.field").value("phone"));
    }

    @Test
    void blankBookIsbnShouldReturn400()
            throws Exception {

        String body =
                """
                {
                  "isbn": "",
                  "title": "API Test Book",
                  "author": "Test Author",
                  "category": "Test",
                  "publisher": "Test Publisher",
                  "publishYear": 2026,
                  "totalCopies": 2,
                  "price": 100000
                }
                """;

        mockMvc.perform(
                        post("/api/books")
                                .with(
                                        httpBasic(
                                                username,
                                                password
                                        )
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(body)
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.field")
                                .value("isbn")
                );
    }

    @Test
    void validMemberCreationShouldReturn200() throws Exception {

        String nationalCode = randomTenDigits();

        String body = """
                {
                  "fullName": "API Valid Member",
                  "nationalCode": "%s",
                  "birthDate": "2000-01-01",
                  "membershipType": "INDIVIDUAL",
                  "phone": "09120000000",
                  "address": "Test Address",
                  "postalCode": "1234567890"
                }
                """.formatted(nationalCode);

        mockMvc.perform(post("/api/members").with(httpBasic(username, password)).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.membershipNumber").exists()).andExpect(jsonPath("$.nationalCode").value(nationalCode)).andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void duplicateNationalCodeShouldReturn409() throws Exception {

        String nationalCode = randomTenDigits();

        String firstBody = """
                {
                  "fullName": "First Member",
                  "nationalCode": "%s",
                  "birthDate": "2000-01-01",
                  "membershipType": "INDIVIDUAL",
                  "phone": "09120000000",
                  "address": "Test Address",
                  "postalCode": "1234567890"
                }
                """.formatted(nationalCode);

        String secondBody = """
                {
                  "fullName": "Second Member",
                  "nationalCode": "%s",
                  "birthDate": "1999-01-01",
                  "membershipType": "INDIVIDUAL",
                  "phone": "09121111111",
                  "address": "Second Address",
                  "postalCode": "2222222222"
                }
                """.formatted(nationalCode);

        mockMvc.perform(post("/api/members").with(httpBasic(username, password)).contentType(MediaType.APPLICATION_JSON).content(firstBody)).andExpect(status().isOk());

        mockMvc.perform(post("/api/members").with(httpBasic(username, password)).contentType(MediaType.APPLICATION_JSON).content(secondBody)).andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409)).andExpect(jsonPath("$.field").value("nationalCode"));
    }

    @Test
    void nonexistentBookShouldReturn404() throws Exception {

        mockMvc.perform(get("/api/books/{id}", 999999999L).with(httpBasic(username, password))).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404)).andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void invalidFinePaymentShouldReturn400() throws Exception {

        String body = """
                {
                  "membershipNumber": "",
                  "amount": 0
                }
                """;

        mockMvc.perform(put("/api/fines/pay").with(httpBasic(username, password)).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    private String randomTenDigits() {

        String digits = UUID.randomUUID().toString().replaceAll("\\D", "");

        while (digits.length() < 10) {

            digits += UUID.randomUUID().toString().replaceAll("\\D", "");
        }

        return digits.substring(0, 10);
    }
}