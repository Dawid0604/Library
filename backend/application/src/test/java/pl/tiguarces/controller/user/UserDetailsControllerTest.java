package pl.tiguarces.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.tiguarces.book.dto.request.UpdateUserRequest;
import pl.tiguarces.service.AppUserService;
import pl.tiguarces.user.entity.AppUser;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unused")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "LIBRARY_DATABASE=localhost:52000",
        "LIBRARY_USERNAME=suse",
        "LIBRARY_PASSWORD=suse"
})
class UserDetailsControllerTest {
    @MockBean private AppUserService appUserService;
    @Autowired private MockMvc mockMvc;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldGetUserDetails() throws Exception {
        // Given
        var foundUser = AppUser.builder()
                               .userId(1L)
                               .username("anyUsername")
                               .password("adfadfq3r3q")
                               .roles("ROLE_USER")
                               .avatar("xyz.png")
                               .build();

        given(appUserService.getLoggedUserFromDb())
                .willReturn(foundUser);

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("details")))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").exists(),
                              jsonPath("$.username").value(foundUser.getUsername()),
                              jsonPath("$.roles").value(foundUser.getRoles()),
                              jsonPath("$.avatar").value(foundUser.getAvatar()));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        // Given
        var request = new UpdateUserRequest("anyPassword", "anyAvatar");

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.put(getFullEndpoint("update"))
                                              .contentType(APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpectAll(status().isOk(),
                              jsonPath("$").doesNotExist());

        verify(appUserService).update(eq(request));
    }

    @Test
    void shouldGetRoles() throws Exception {
        // Given
        String roles = "ROLE_ADMIN";

        given(appUserService.getLoggedUser())
                .willReturn(new AppUser("anyUsername", "anyPassword", roles));

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get(getFullEndpoint("roles")))
               .andDo(print())
               .andExpectAll(status().isOk(),
                             jsonPath("$").value(roles));
    }

    private static String getFullEndpoint(final String endpoint) {
        return "/api/user/" + endpoint;
    }
}