package pl.tiguarces.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.tiguarces.JwtUtils;
import pl.tiguarces.service.UserDetailsServiceImpl;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticateFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl appUserService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader;
        if ((authorizationHeader = request.getHeader(AUTHORIZATION)) != null && authorizationHeader.startsWith("Bearer ")) {
            String username = jwtUtils.getUsernameFromToken(authorizationHeader.substring(7));

            if (username != null && (SecurityContextHolder.getContext()).getAuthentication() == null) {
                UserDetails userDetails = appUserService.loadUserByUsername(username);
                var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                                   .buildDetails(request));

                (SecurityContextHolder.getContext()).setAuthentication(authenticationToken);
            }

        } filterChain.doFilter(request, response);
    }
}
