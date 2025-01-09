package by.clevertec.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.client.HttpClientErrorException;

public class ExceptionHandlerUtil {
    public static RuntimeException handleHttpClientError(HttpClientErrorException ex) {
        String responseBody = ex.getResponseBodyAsString();
        int statusCode = ex.getStatusCode().value();

        return switch (statusCode) {
            case 403 -> new AccessDeniedException(responseBody);
            case 404 -> new NewsNotFoundException();
            default -> new DataIntegrityViolationException(responseBody);
        };
    }
}
