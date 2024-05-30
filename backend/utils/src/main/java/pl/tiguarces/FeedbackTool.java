package pl.tiguarces;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static pl.tiguarces.Constants.MESSAGE_KEY;

public final class FeedbackTool {
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private FeedbackTool() { }

    public static void sendResponse(final HttpServletResponse response, final int status, final String message) throws IOException {
        if(response != null && status > 0 && isNotBlank(message)) {
            response.setStatus(status);
            response.getWriter()
                    .write(jsonMapper.writeValueAsString(Map.of(MESSAGE_KEY, message)));
        }
    }
}
