package by.clevertec.services.impl;

import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.request.CommentDtoRequestUpdate;
import by.clevertec.dto.response.CommentsDtoResponse;
import by.clevertec.exception.CommentNotFoundException;
import by.clevertec.exception.NewsNotFoundException;
import by.clevertec.services.CommentsClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static by.clevertec.util.JwtHelper.getJwtToken;

@Service
@RequiredArgsConstructor
public class CommentsClientServiceImpl implements CommentsClientService {

    private final RestTemplate restTemplate;
    @Value("${news.service.url}")
    private String newsServiceUrl;


    @Override
    public CommentsDtoResponse findById(UUID uuid) {
        String url = String.format("%s/comments/%s", newsServiceUrl, uuid);
        HttpEntity<Void> entity = getJwtToken();
        try {
            ResponseEntity<CommentsDtoResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, CommentsDtoResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            String errorMessage = String.format("Error %d: %s", e.getStatusCode().value(), e.getStatusText());

            System.out.println(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

    @Override
    public List<CommentsDtoResponse> fullTextSearchByTextAndUsernameField(String searchElement, int page, int pageSize,
                                                                          String searchableFields, String sortField, String sortOrder) {
        String url = String.format("%s/comments/search?startIndex=%d&maxResults=%d&fields=%s&sortBy=%s&sortOrder=%s",
                newsServiceUrl, page, pageSize, searchableFields, sortField, sortOrder);
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("query", searchElement);

        ResponseEntity<List<CommentsDtoResponse>> response = restTemplate.exchange(url, HttpMethod.POST,
                new HttpEntity<>(requestParams, getJwtToken().getHeaders()), new ParameterizedTypeReference<>() {});
        return response.getBody() != null ? response.getBody() : new ArrayList<>();
    }

    @Override
    public String create(UUID newsUuid, CommentDtoRequest commentDtoRequest) {
        String url = String.format("%s/comments/%s", newsServiceUrl, newsUuid);
        HttpEntity<CommentDtoRequest> entity = new HttpEntity<>(commentDtoRequest, getJwtToken().getHeaders());
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NewsNotFoundException();
        }
    }

    @Override
    public CommentsDtoResponse update(UUID uuid, CommentDtoRequestUpdate commentDtoRequestUpdate) {
        String url = String.format("%s/comments/%s", newsServiceUrl, uuid);
        HttpEntity<CommentDtoRequestUpdate> entity = new HttpEntity<>(commentDtoRequestUpdate, getJwtToken().getHeaders());
        try {
            ResponseEntity<CommentsDtoResponse> response = restTemplate.exchange(url, HttpMethod.PUT, entity, CommentsDtoResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e){
            throw new CommentNotFoundException();
        }
    }

    @Override
    public void delete(UUID uuid) {
        String url = String.format("%s/comments/%s", newsServiceUrl, uuid);
        HttpEntity<Void> entity = new HttpEntity<>(getJwtToken().getHeaders());
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new CommentNotFoundException();
        }
    }
}
