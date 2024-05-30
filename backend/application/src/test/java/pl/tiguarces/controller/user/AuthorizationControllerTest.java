package pl.tiguarces.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.tiguarces.service.AppUserService;
import pl.tiguarces.user.dto.UserPayload;

import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.tiguarces.JwtUtils.ACCESS_TOKEN;
import static pl.tiguarces.JwtUtils.REFRESH_TOKEN;

@SuppressWarnings("unused")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "LIBRARY_DATABASE=localhost:52000",
        "LIBRARY_USERNAME=suse",
        "LIBRARY_PASSWORD=suse"
})
class AuthorizationControllerTest {
    @MockBean private AppUserService appUserService;
    @Autowired private MockMvc mockMvc;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldLogin() throws Exception {
        // Given
        var request = new UserPayload("x", "y");

        given(appUserService.generateTokens(eq(request)))
                .willReturn(new ResponseEntity<>(Map.of(ACCESS_TOKEN, "xyz",
                                                        REFRESH_TOKEN, "xyz"), OK));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.post(getFullEndpoint("login"))
                                              .contentType(APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").isNotEmpty(),
                              jsonPath("$." + ACCESS_TOKEN).exists(),
                              jsonPath("$." + REFRESH_TOKEN).exists());
    }

    @Test
    void shouldRegister() throws Exception {
        // Given
        var request = new UserPayload("x", "y");

        given(appUserService.register(eq(request)))
                .willReturn(new ResponseEntity<>(OK));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.post(getFullEndpoint("register"))
                                              .contentType(APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").doesNotExist());
    }

    @Test
    void shouldNotRegister() throws Exception {
        // Given
        var request = new UserPayload("x", "y");

        given(appUserService.register(eq(request)))
                .willReturn(new ResponseEntity<>("Message", BAD_REQUEST));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.post(getFullEndpoint("register"))
                                              .contentType(APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpectAll(status().isBadRequest(),
                              jsonPath("$").exists());
    }

    @Test
    void shouldDelete() throws Exception {
        // Given
        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.delete(getFullEndpoint("delete")))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").doesNotExist());

        verify(appUserService).deleteAccount();
    }

    @Test
    void shouldRefreshTokens() throws Exception {
        // Given
        String token = "xyz";
        String newToken = "abcd";
        var tokenPayload = Map.of(REFRESH_TOKEN, token);

        given(appUserService.refreshToken(eq(token)))
                .willReturn(Map.of(REFRESH_TOKEN, newToken));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.post(getFullEndpoint("refresh"))
                                              .contentType(APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(tokenPayload)))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").exists(),
                              jsonPath("$." + REFRESH_TOKEN).isNotEmpty(),
                              jsonPath("$." + REFRESH_TOKEN).value(newToken));
    }

    private static String getFullEndpoint(final String endpoint) {
        return "/api/auth/" + endpoint;
    }
}