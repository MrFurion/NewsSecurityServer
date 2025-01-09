package by.clevertec.controller;

import by.clevertec.constants.SecurityRole;
import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.request.CommentDtoRequestUpdate;
import by.clevertec.dto.response.CommentsDtoResponse;
import by.clevertec.services.CommentsClientService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentsClientController {

    private final CommentsClientService commentsClientService;

    @PreAuthorize(SecurityRole.ROLE_ADMIN_OR_JOURNALIST_OR_SUBSCRIBER)
    @GetMapping("/{uuid}")
    public ResponseEntity<CommentsDtoResponse> findComment(@PathVariable UUID uuid) {
        CommentsDtoResponse response = commentsClientService.findById(uuid);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize(SecurityRole.ROLE_ADMIN_OR_JOURNALIST_OR_SUBSCRIBER)
    @GetMapping("/search")
    public ResponseEntity<List<CommentsDtoResponse>> searchCommentsByTextAndUsername(@RequestBody String query,
                                                                                     @RequestParam(defaultValue = "0") int startIndex,
                                                                                     @RequestParam(defaultValue = "5") int maxResults,
                                                                                     @RequestParam(defaultValue = "text") String fields,
                                                                                     @RequestParam(defaultValue = "sort_text") String sortBy,
                                                                                     @RequestParam(defaultValue = "ASC") String sortOrder) {
        List<CommentsDtoResponse> commentsDtoResponses =
                commentsClientService.fullTextSearchByTextAndUsernameField(query, startIndex, maxResults, fields, sortBy, sortOrder);
        return ResponseEntity.ok(commentsDtoResponses);
    }

    @PreAuthorize(SecurityRole.ROLE_ADMIN_OR_SUBSCRIBER)
    @PostMapping("/{uuid}")
    public ResponseEntity<String> createComment(@Validated @RequestBody CommentDtoRequest commentDtoRequest,
                                                @PathVariable UUID uuid) {
        String result = commentsClientService.create(uuid, commentDtoRequest);
        URI location = URI.create("/comments/");
        return ResponseEntity.created(location).body(result);
    }

    @PreAuthorize(SecurityRole.ROLE_ADMIN_OR_SUBSCRIBER)
    @PutMapping("/{uuid}")
    public ResponseEntity<CommentsDtoResponse> updateComment(@PathVariable UUID uuid,
                                                             @RequestBody CommentDtoRequestUpdate commentDtoRequestUpdate) {
        CommentsDtoResponse commentsDtoResponse = commentsClientService.update(uuid, commentDtoRequestUpdate);
        return ResponseEntity.ok(commentsDtoResponse);
    }

    @PreAuthorize(SecurityRole.ROLE_ADMIN_OR_SUBSCRIBER)
    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> deleteComment(@PathVariable UUID uuid) {
        commentsClientService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
