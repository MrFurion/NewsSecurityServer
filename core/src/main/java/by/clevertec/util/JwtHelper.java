package by.clevertec.util;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtHelper {
    public static HttpEntity<Void> getJwtToken() {
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return new HttpEntity<>(headers);
    }
}
