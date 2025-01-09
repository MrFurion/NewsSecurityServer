package by.clevertec.controller;

import by.clevertec.constants.SecurityRole;
import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.services.NewsClientService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsClientController {

    private final NewsClientService newsClientService;

    @PreAuthorize(SecurityRole.ROLE_ADMIN_OR_JOURNALIST_OR_SUBSCRIBER)
    @GetMapping("/{id}")
    public ResponseEntity<NewsDtoResponse> findNews(@PathVariable UUID id) {
        NewsDtoResponse newsDtoResponse = newsClientService.getNewsById(id);
        return ResponseEntity.ok(newsDtoResponse);
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<Page<NewsDtoResponse>> findAllNews(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NewsDtoResponse> newsDtoResponses = newsClientService.findAll(pageable);
        return ResponseEntity.ok(newsDtoResponses);
    }

    @PreAuthorize(SecurityRole.ROLE_ADMIN_OR_JOURNALIST_OR_SUBSCRIBER)
    @GetMapping("/search")
    public ResponseEntity<List<NewsDtoResponse>> searchNewsByTitleAndText(@RequestBody String query,
                                                                          @RequestParam(defaultValue = "0") int startIndex,
                                                                          @RequestParam(defaultValue = "5") int maxResults,
                                                                          @RequestParam(defaultValue = "title") String fields,
                                                                          @RequestParam(defaultValue = "sort_title") String sortBy,
                                                                          @RequestParam(defaultValue = "ASC") String sortOrder) {

        List<NewsDtoResponse> newsDtoResponses = newsClientService.fullTextSearchByTitleAndTextField(query, startIndex, maxResults,
                fields, sortBy, sortOrder);
        return ResponseEntity.ok(newsDtoResponses);
    }

    @PermitAll
    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<NewsDtoResponse>> findAllNewsWithComments(@PathVariable UUID id,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "5") int size) {
        Page<NewsDtoResponse> newsDtoResponse = newsClientService.findByIdWithAllComments(id, page, size);
        return ResponseEntity.ok(newsDtoResponse);
    }

    @PreAuthorize(SecurityRole.ROLE_ADMIN_OR_JOURNALIST)
    @PostMapping
    public ResponseEntity<String> createNews(@Validated @RequestBody NewsDtoRequest newsDtoRequest) {
        String result = newsClientService.create(newsDtoRequest);
        URI location = URI.create("/news/");
        return ResponseEntity.created(location).body(result);
    }

    @PreAuthorize(SecurityRole.ROLE_ADMIN_OR_JOURNALIST)
    @PutMapping("/{id}")
    public ResponseEntity<NewsDtoResponse> updateNews(@Validated @RequestBody NewsDtoRequestUpdate newsDtoRequestUpdate,
                                                      @PathVariable UUID id) {

        NewsDtoResponse newsDtoResponse = newsClientService.update(newsDtoRequestUpdate, id);
        return ResponseEntity.ok(newsDtoResponse);
    }

    @PreAuthorize(SecurityRole.ROLE_ADMIN_OR_JOURNALIST)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNews(@PathVariable UUID id) {
        newsClientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
