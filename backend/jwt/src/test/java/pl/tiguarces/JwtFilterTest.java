package pl.tiguarces;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static pl.tiguarces.JwtUtils.ACCESS_TOKEN_TYPE;
import static pl.tiguarces.JwtUtils.REFRESH_TOKEN_TYPE;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;
    @Mock private JwtUtils jwtUtils;
    @InjectMocks private JwtFilter jwtFilter;

    @Test
    void shouldDoFilterInternal() throws ServletException, IOException {
        // Given
        String token = "fafjiafjasikf";
        String authorizationHeader = "Bearer " + token;

        given(request.getHeader(eq(AUTHORIZATION)))
                .willReturn(authorizationHeader);

        given(request.getServletPath())
                .willReturn("https://xyz.com/api/xyz");

        given(jwtUtils.getClaimFromToken(eq(token), any()))
                .willReturn(ACCESS_TOKEN_TYPE);

        // When
        // Then
        jwtFilter.doFilterInternal(request, response, filterChain);
    }

    @Test
    void shouldDoFilterInternalWhenEndpointIsRefreshToken() throws ServletException, IOException {
        // Given
        given(request.getServletPath())
                .willReturn("https://xyz.com/api/auth/refresh");

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verifyNoInteractions(jwtUtils);
        verify(request, never()).getHeader(anyString());
    }

    @Test
    void shouldDoNotFilterInternalAndHandleSignatureExceptionAndSendForbiddenStatus() throws ServletException, IOException {
        // Given
        String token = "fafjiafjasikf";
        String authorizationHeader = "Bearer " + token;

        given(request.getHeader(eq(AUTHORIZATION)))
                .willReturn(authorizationHeader);

        given(request.getServletPath())
                .willReturn("https://xyz.com/api/xyz");

        given(jwtUtils.getClaimFromToken(eq(token), any()))
                .willThrow(SignatureException.class);

        given(response.getWriter())
                .willReturn(mock(PrintWriter.class));

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, never()).doFilter(any(), any());
        verify(response).setStatus(SC_FORBIDDEN);
    }

    @Test
    void shouldDoNotFilterInternalAndHandleMalformedJwtExceptionAndSendBadRequestStatus() throws ServletException, IOException {
        // Given
        String token = "fafjiafjasikf";
        String authorizationHeader = "Bearer " + token;

        given(request.getHeader(eq(AUTHORIZATION)))
                .willReturn(authorizationHeader);

        given(request.getServletPath())
                .willReturn("https://xyz.com/api/xyz");

        given(jwtUtils.getClaimFromToken(eq(token), any()))
                .willThrow(MalformedJwtException.class);

        given(response.getWriter())
                .willReturn(mock(PrintWriter.class));

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, never()).doFilter(any(), any());
        verify(response).setStatus(SC_BAD_REQUEST);
    }

    @Test
    void shouldDoNotFilterInternalAndHandleExpiredJwtExceptionAndSendBadRequestStatus() throws ServletException, IOException {
        // Given
        String token = "fafjiafjasikf";
        String authorizationHeader = "Bearer " + token;

        given(request.getHeader(eq(AUTHORIZATION)))
                .willReturn(authorizationHeader);

        given(request.getServletPath())
                .willReturn("https://xyz.com/api/xyz");

        given(jwtUtils.getClaimFromToken(eq(token), any()))
                .willThrow(ExpiredJwtException.class);

        given(response.getWriter())
                .willReturn(mock(PrintWriter.class));

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, never()).doFilter(any(), any());
        verify(response).setStatus(SC_FORBIDDEN);
    }
    
    @Test
    void shouldDoNotFilterInternalWhenRefreshTokenIsUsedAndSendBadRequestStatus() throws ServletException, IOException {
        // Given
        String token = "fafjiafjasikf";
        String authorizationHeader = "Bearer " + token;

        given(request.getHeader(eq(AUTHORIZATION)))
                .willReturn(authorizationHeader);

        given(request.getServletPath())
                .willReturn("https://xyz.com/api/xyz");
        
        given(jwtUtils.getClaimFromToken(eq(token), any()))
                .willReturn(REFRESH_TOKEN_TYPE);

        given(response.getWriter())
                .willReturn(mock(PrintWriter.class));

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(eq(request), eq(response));
        verify(response).setStatus(SC_BAD_REQUEST);
    }
}