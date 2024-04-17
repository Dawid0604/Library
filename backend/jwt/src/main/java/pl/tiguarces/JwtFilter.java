package pl.tiguarces;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static pl.tiguarces.FeedbackTool.sendResponse;
import static pl.tiguarces.JwtUtils.ACCESS_TOKEN_TYPE;
import static pl.tiguarces.JwtUtils.TOKEN_TYPE_FIELD;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {

        try {
            String authorizationHeader;

            if(!request.getServletPath().contains("/api/auth/refresh") && (authorizationHeader = request.getHeader(AUTHORIZATION)) != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);

                if(!jwtUtils.getClaimFromToken(token, claims -> claims.get(TOKEN_TYPE_FIELD)).equals(ACCESS_TOKEN_TYPE)) {
                    sendResponse(response, SC_BAD_REQUEST, "Using the wrong token type. Use an Access token instead");
                }

            } filterChain.doFilter(request, response);

        } catch (SignatureException exception) {
            sendResponse(response, SC_FORBIDDEN, "Token has been changed by external service");

        } catch (MalformedJwtException exception) {
            sendResponse(response, SC_BAD_REQUEST, "Token is invalid");

        } catch (ExpiredJwtException exception) {
            sendResponse(response, SC_FORBIDDEN, "Token expired");
        }
    }
}
