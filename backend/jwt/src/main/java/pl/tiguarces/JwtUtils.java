package pl.tiguarces;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static java.lang.System.currentTimeMillis;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
public class JwtUtils {
    private final Integer expirationDate;
    private final Integer refreshTokenExpirationDate;
    private final SecretKey signingKey;

    public static final String ACCESS_TOKEN_TYPE = "access";
    public static final String REFRESH_TOKEN_TYPE = "refresh";
    public static final String TOKEN_TYPE_FIELD = "tokenType";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";

    public JwtUtils(@Value("${jwt.secret}") final String apiSecret,
                    @Value("${jwt.expirationDate}") final Integer expirationDate,
                    @Value("${jwt.refreshTokenExpirationDate}") Integer refreshTokenExpirationDate) throws NoSuchAlgorithmException {

        this.expirationDate = expirationDate;
        this.refreshTokenExpirationDate = refreshTokenExpirationDate;

        if(apiSecret != null) {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            signingKey = new SecretKeySpec(md.digest(apiSecret.getBytes(UTF_8)),"HmacSHA512");

        } else {
            log.warn("Property 'api.secret' is not present, using default settings");
            signingKey = Keys.secretKeyFor(HS512);
        }
    }

    public Map<String, String> generateTokens(final String username) {
        String accessToken = generateToken(new HashMap<>(), username, ACCESS_TOKEN_TYPE);
        String refreshToken = generateToken(new HashMap<>(), username, REFRESH_TOKEN_TYPE);

        return Map.of(ACCESS_TOKEN, accessToken, REFRESH_TOKEN, refreshToken);
    }

    public Map<String, String> refreshTokens(final String refreshToken) throws NoSuchElementException {
        String email = getUsernameFromToken(requireNonNull(refreshToken));

        if(isNotBlank(refreshToken) && isNotBlank(email)) {
            String tokenType = (String) getClaimFromToken(refreshToken, (claims) -> claims.get(TOKEN_TYPE_FIELD));

            if(tokenType.equals(REFRESH_TOKEN_TYPE)) {
                String newAccessToken = generateToken(getAllClaimsFromToken(refreshToken), email, ACCESS_TOKEN_TYPE);
                String newRefreshToken = generateToken(getAllClaimsFromToken(refreshToken), email, REFRESH_TOKEN_TYPE);

                return Map.of(ACCESS_TOKEN, newAccessToken, REFRESH_TOKEN, newRefreshToken);
            }
        } return null;
    }

    public String getUsernameFromToken(final String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaimsFromToken(token));
    }

    public Claims getAllClaimsFromToken(final String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(signingKey)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    public String generateToken(final Map<String, Object> claims, final String subject, final String type) {
        claims.put("tokenType", type);

        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(subject)
                   .setIssuedAt(new Date(currentTimeMillis()))
                   .setExpiration(new Date(currentTimeMillis() + (type.equals(ACCESS_TOKEN_TYPE) ? expirationDate : refreshTokenExpirationDate)))
                   .signWith(signingKey).compact();
    }
}
