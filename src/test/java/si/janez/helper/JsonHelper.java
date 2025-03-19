package si.janez.helper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JsonHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T loadJsonFromResource(String resourcePath, Class<T> valueType) throws IOException {
        try (InputStream inputStream = JsonHelper.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            return objectMapper.readValue(inputStream, valueType);
        }
    }
}
