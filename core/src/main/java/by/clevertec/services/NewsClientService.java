package by.clevertec.services;

import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface NewsClientService {
    NewsDtoResponse getNewsById(UUID id);

    Page<NewsDtoResponse> findAll(Pageable pageable);

    List<NewsDtoResponse> fullTextSearchByTitleAndTextField(String searchElement, int page, int pageSize,
                                                            String searchableFields, String sortField, String sortOrder);

    Page<NewsDtoResponse> findByIdWithAllComments(UUID uuid, int page, int size);

    String create(NewsDtoRequest newsDtoRequest);

    NewsDtoResponse update(NewsDtoRequestUpdate newsDtoRequestUpdate, UUID uuid);

    void delete(UUID uuid);
}
