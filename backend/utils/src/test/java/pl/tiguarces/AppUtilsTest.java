package pl.tiguarces;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppUtilsTest {

    @Test
    void shouldGetCurrentDate() {
        // Given
        // When
        // Then
        assertNotNull(AppUtils.getCurrentDate());
    }

    @ParameterizedTest
    @MethodSource("shouldFormatDateDataProvider")
    void shouldFormatDate(final LocalDateTime incomingDate, final String expectedDate) {
        // Given
        // When
        // Then
        assertEquals(expectedDate, AppUtils.formatDate(incomingDate));
    }

    private static Stream<Arguments> shouldFormatDateDataProvider() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2024, 1, 2, 13, 50), "02-01-2024 13:50"),
                Arguments.of(LocalDateTime.of(2024, 11, 12, 9, 0), "12-11-2024 09:00"),
                Arguments.of(null, null)
        );
    }
}