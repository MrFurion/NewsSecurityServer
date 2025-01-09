package by.clevertec.services.impl;

import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.exception.ExceptionHandlerUtil;
import by.clevertec.exception.NewsNotFoundException;
import by.clevertec.services.NewsClientService;
import by.clevertec.util.RestPageImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static by.clevertec.exception.ExceptionHandlerUtil.handleHttpClientError;
import static by.clevertec.util.JwtHelper.getJwtToken;

@Service
@RequiredArgsConstructor
public class NewsClientServiceImpl implements NewsClientService {

    private final RestTemplate restTemplate;
    @Value("${news.service.url}")
    private String newsServiceUrl;

    public NewsDtoResponse getNewsById(UUID id) {
        String url = newsServiceUrl + "/news/" + id;
        HttpEntity<Void> entity = getJwtToken();
        try {
            ResponseEntity<NewsDtoResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, NewsDtoResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
           throw  handleHttpClientError(ex);
        }
    }

    public Page<NewsDtoResponse> findAll(Pageable pageable) {
        checkSelfCall();
        String url = String.format("%s/news?page=%d&size=%d",
                newsServiceUrl, pageable.getPageNumber(), pageable.getPageSize());
        ResponseEntity<RestPageImpl<NewsDtoResponse>> response = restTemplate.exchange(
                url, HttpMethod.GET, getJwtToken(), new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    @Override
    public List<NewsDtoResponse> fullTextSearchByTitleAndTextField(String searchElement, int startIndex, int maxResults, String searchableFields, String sortField, String sortOrder) {
        checkSelfCall();

        String url = String.format("%s/news/search?startIndex=%d&maxResults=%d&fields=%s&sortBy=%s&sortOrder=%s",
                newsServiceUrl, startIndex, maxResults, searchableFields, sortField, sortOrder);
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("query", searchElement);
        ResponseEntity<List<NewsDtoResponse>> response = restTemplate.exchange(
                url, HttpMethod.POST, new HttpEntity<>(requestParams, getJwtToken().getHeaders()), new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody() != null ? response.getBody() : new ArrayList<>();
    }

    @Override
    public Page<NewsDtoResponse> findByIdWithAllComments(UUID uuid, int page, int size) {
        String url = String.format("%s/news/%s/comments?page=%d&size=%d", newsServiceUrl, uuid, page, size);
        ResponseEntity<RestPageImpl<NewsDtoResponse>> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(getJwtToken().getHeaders()),
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody() != null ? response.getBody() : Page.empty();
    }

    @Override
    public String create(NewsDtoRequest newsDtoRequest) {
        String url = String.format("%s/news", newsServiceUrl);
        HttpEntity<NewsDtoRequest> entity = new HttpEntity<>(newsDtoRequest, getJwtToken().getHeaders());
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException.BadRequest e) {
            String errorMessage = e.getResponseBodyAsString();
            return "Error: " + errorMessage;
        }
    }

    @Override
    public NewsDtoResponse update(NewsDtoRequestUpdate newsDtoRequestUpdate, UUID uuid) {
        String url = String.format("%s/news/%s", newsServiceUrl, uuid);
        HttpEntity<NewsDtoRequestUpdate> entity = new HttpEntity<>(newsDtoRequestUpdate, getJwtToken().getHeaders());

        try {
            ResponseEntity<NewsDtoResponse> response = restTemplate.exchange(url, HttpMethod.PUT, entity, NewsDtoResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NewsNotFoundException();
        }
    }

    @Override
    public void delete(UUID uuid) {
        String url = String.format("%s/news/%s", newsServiceUrl, uuid);
        HttpEntity<Void> entity = new HttpEntity<>(getJwtToken().getHeaders());
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NewsNotFoundException();
        }
    }

    private void checkSelfCall() {
        if (newsServiceUrl.contains("localhost:8081")) {
            throw new IllegalStateException("Preventing self-call");
        }
    }
}