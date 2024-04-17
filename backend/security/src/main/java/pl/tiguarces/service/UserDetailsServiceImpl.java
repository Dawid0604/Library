package pl.tiguarces.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pl.tiguarces.repository.AppUserRepository;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static pl.tiguarces.Patterns.COMMA_PATTERN;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AppUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                                 .orElseThrow(() -> new UsernameNotFoundException("User with username '%s' not found".formatted(username)));

        return new User(username, passwordEncoder.encode(user.getPassword()), mapToAuthorities(user.getRoles()));
    }

    private static List<? extends GrantedAuthority> mapToAuthorities(final String roles) {
        if(isBlank(roles)) {
            throw new IllegalArgumentException("User roles cannot be empty!");
        }

        return Arrays.stream(COMMA_PATTERN.split(roles))
                     .map(SimpleGrantedAuthority::new)
                     .toList();
    }
}
