package pl.tiguarces.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.tiguarces.service.AppUserService;
import pl.tiguarces.user.dto.UserPayload;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.tiguarces.Constants.MESSAGE_KEY;

@SuppressWarnings("unused")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class ControllerExceptionHandlerTest {
    @MockBean private AppUserService appUserService;
    @Autowired private MockMvc mockMvc;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldLogin() throws Exception {
        // Given
        String exceptionResponse = "Bad credentials";
        var request = new UserPayload("x", "y");

        given(appUserService.generateTokens(eq(request)))
                .willThrow(new BadCredentialsException(exceptionResponse));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                                              .contentType(APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                              jsonPath("$").isNotEmpty(),
                              jsonPath("$." + MESSAGE_KEY).exists(),
                              jsonPath("$." + MESSAGE_KEY).value(exceptionResponse));
    }

}