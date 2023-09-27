package com.topchoir.directory.resource;

import com.auth0.json.auth.TokenHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topchoir.directory.domain.Equipment;
import com.topchoir.directory.domain.Event;
import com.topchoir.directory.domain.Member;
import com.topchoir.directory.dto.LoginInput;
import com.topchoir.directory.repository.MemberRepository;
import com.topchoir.directory.service.AuthService;
import com.topchoir.directory.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthControllerTests {

    private static final Logger LOG = Logger.getLogger(MemberControllerTests.class.toString());
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthController authController;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenLoginSuccessfulThenStatusIsOk() throws Exception {

        LoginInput input = new LoginInput("test@gmail.com", "TesT123!!");

        when(authService.login(anyString(), anyString()))
                .thenReturn(new TokenHolder( "accessTokenTest239723", "idToken",  "refreshToken",  "tokenType", 123456,  "scope", new Date()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .content(objectMapper.writeValueAsString(input)).contentType("application/json;charset=UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessTokenTest239723"));
    }
}