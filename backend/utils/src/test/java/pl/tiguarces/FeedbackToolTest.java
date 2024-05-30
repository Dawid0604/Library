package pl.tiguarces;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static pl.tiguarces.Constants.MESSAGE_KEY;

class FeedbackToolTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSendResponse() throws IOException {
        // Given
        String message = "xyz";

        int status = 400;
        var httpServletResponse = mock(HttpServletResponse.class);
        var printWriter = mock(PrintWriter.class);

        given(httpServletResponse.getWriter())
                .willReturn(printWriter);

        // When
        FeedbackTool.sendResponse(httpServletResponse, status, message);

        // Then
        verify(httpServletResponse).setStatus(eq(status));
        verify(printWriter).write(eq(objectMapper.writeValueAsString(Map.of(MESSAGE_KEY, message))));
    }
}