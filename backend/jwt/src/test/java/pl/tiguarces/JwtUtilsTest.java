package pl.tiguarces;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.jupiter.api.Assertions.*;
import static pl.tiguarces.AppUtils.getCurrentDate;
import static pl.tiguarces.JwtUtils.ACCESS_TOKEN;
import static pl.tiguarces.JwtUtils.REFRESH_TOKEN;

class JwtUtilsTest {
    private static final String JWT_SECRET = "xyz";
    private static final Integer EXPIRATION_DATE = 720_000;
    private static final Integer REFRESH_TOKEN_EXPIRATION_DATE = 1_720_000;

    @Test
    void shouldCreateComponentProperly() throws Exception {
        // Given
        Field expirationDateField = JwtUtils.class.getDeclaredField("expirationDate");
        Field refreshExpirationDateField = JwtUtils.class.getDeclaredField("refreshTokenExpirationDate");
        Field singingKeyField = JwtUtils.class.getDeclaredField("signingKey");

        singingKeyField.setAccessible(true);
        expirationDateField.setAccessible(true);
        refreshExpirationDateField.setAccessible(true);

        byte[] expectedHashKey = MessageDigest.getInstance("SHA-512")
                                              .digest(JWT_SECRET.getBytes(UTF_8));

        SecretKeySpec expectedKey = new SecretKeySpec(expectedHashKey, "HmacSHA512");

        // When
        JwtUtils jwtUtils = new JwtUtils(JWT_SECRET, EXPIRATION_DATE, REFRESH_TOKEN_EXPIRATION_DATE);

        // Then
        assertEquals(EXPIRATION_DATE, expirationDateField.get(jwtUtils));
        assertEquals(REFRESH_TOKEN_EXPIRATION_DATE, refreshExpirationDateField.get(jwtUtils));
        assertEquals(Keys.secretKeyFor(HS512).getAlgorithm(), ((SecretKey) singingKeyField.get(jwtUtils)).getAlgorithm());
        assertArrayEquals(expectedKey.getEncoded(), ((SecretKey) singingKeyField.get(jwtUtils)).getEncoded());
    }

    @Test
    void shouldCreateComponentProperlyWithDefaultSingingKey() throws Exception {
        // Given
        Field expirationDateField = JwtUtils.class.getDeclaredField("expirationDate");
        Field refreshExpirationDateField = JwtUtils.class.getDeclaredField("refreshTokenExpirationDate");
        Field singingKeyField = JwtUtils.class.getDeclaredField("signingKey");

        singingKeyField.setAccessible(true);
        expirationDateField.setAccessible(true);
        refreshExpirationDateField.setAccessible(true);

        byte[] expectedHashKey = MessageDigest.getInstance("SHA-512")
                                              .digest(JWT_SECRET.getBytes(UTF_8));

        SecretKeySpec expectedKey = new SecretKeySpec(expectedHashKey, "HmacSHA512");

        // When
        JwtUtils jwtUtils = new JwtUtils(null, EXPIRATION_DATE, REFRESH_TOKEN_EXPIRATION_DATE);

        // Then
        assertEquals(EXPIRATION_DATE, expirationDateField.get(jwtUtils));
        assertEquals(REFRESH_TOKEN_EXPIRATION_DATE, refreshExpirationDateField.get(jwtUtils));
        assertEquals(Keys.secretKeyFor(HS512).getAlgorithm(), ((SecretKey) singingKeyField.get(jwtUtils)).getAlgorithm());
        assertFalse(Arrays.equals(expectedKey.getEncoded(), ((SecretKey) singingKeyField.get(jwtUtils)).getEncoded()));
    }

    @Test
    void shouldGenerateTokens() throws NoSuchAlgorithmException {
        // Given
        String username = "xyz";
        JwtUtils jwtUtils = new JwtUtils(JWT_SECRET, EXPIRATION_DATE, REFRESH_TOKEN_EXPIRATION_DATE);

        // When
        var result = jwtUtils.generateTokens(username);

        // Then
        assertTrue(() -> {
            var accessToken = result.get(ACCESS_TOKEN);

            if(isBlank(accessToken)) {
                return false;
            }

            var expirationDate = LocalDateTime.ofInstant(jwtUtils.getClaimFromToken(accessToken, Claims::getExpiration).toInstant(), systemDefault());
            var currentDate = getCurrentDate().plusSeconds(EXPIRATION_DATE / 1000).truncatedTo(MINUTES);

            return expirationDate.truncatedTo(MINUTES).equals(currentDate);
        });

        assertTrue(() -> {
            var refreshToken = result.get(REFRESH_TOKEN);

            if(isBlank(refreshToken)) {
                return false;
            }

            var expirationDate = LocalDateTime.ofInstant(jwtUtils.getClaimFromToken(refreshToken, Claims::getExpiration).toInstant(), systemDefault());
            var currentDate = getCurrentDate().plusSeconds(REFRESH_TOKEN_EXPIRATION_DATE / 1000).truncatedTo(MINUTES);

            return expirationDate.truncatedTo(MINUTES).equals(currentDate);
        });

        assertEquals(username, jwtUtils.getClaimFromToken(result.get(ACCESS_TOKEN), Claims::getSubject));
    }

    @Test
    void shouldRefreshTokens() throws NoSuchAlgorithmException {
        // Given
        JwtUtils jwtUtils = new JwtUtils(JWT_SECRET, EXPIRATION_DATE, REFRESH_TOKEN_EXPIRATION_DATE);

        String username = "xyz";
        String refreshToken = jwtUtils.generateTokens(username)
                                      .get(REFRESH_TOKEN);

        // When
        var result = jwtUtils.refreshTokens(refreshToken);

        // Then
        assertTrue(result.containsKey(ACCESS_TOKEN) && isNotBlank(result.get(ACCESS_TOKEN)));
        assertTrue(result.containsKey(REFRESH_TOKEN) && isNotBlank(result.get(REFRESH_TOKEN)));
    }

    @Test
    void shouldNotRefreshTokensWhenAccessTokenTypeWasPassed() throws NoSuchAlgorithmException {
        // Given
        JwtUtils jwtUtils = new JwtUtils(JWT_SECRET, EXPIRATION_DATE, REFRESH_TOKEN_EXPIRATION_DATE);

        String username = "xyz";
        String refreshToken = jwtUtils.generateTokens(username)
                                      .get(ACCESS_TOKEN);

        // When
        var result = jwtUtils.refreshTokens(refreshToken);

        // Then
        assertNull(result);
    }
}