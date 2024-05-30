package pl.tiguarces.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import pl.tiguarces.JwtUtils;
import pl.tiguarces.service.UserDetailsServiceImpl;

import java.io.IOException;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(MockitoExtension.class)
class AuthenticateFilterTest {
    @Mock private JwtUtils jwtUtils;
    @Mock private UserDetailsServiceImpl userDetailsService;
    @InjectMocks private AuthenticateFilter authenticateFilter;

    @Test
    void shouldDoInternalWhenAuthorizationHeaderIsPresent() throws ServletException, IOException {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        String token = "syz";
        String authorizationHeaderContent = "Bearer " + token;
        String username = "anyUsername";

        given(request.getHeader(eq(AUTHORIZATION)))
                .willReturn(authorizationHeaderContent);

        given(jwtUtils.getUsernameFromToken(eq(token)))
                .willReturn(username);

        lenient().when(userDetailsService.loadUserByUsername(eq(username)))
                .thenReturn(mock(UserDetails.class));

        // When
        authenticateFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(eq(request), eq(response));
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void shouldDoInternalWhenAuthorizationHeaderIsNotPresent() throws ServletException, IOException {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // When
        authenticateFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(eq(request), eq(response));
        verify(request).getHeader(eq(AUTHORIZATION));
        verify(response, never()).sendError(anyInt(), anyString());
    }

}