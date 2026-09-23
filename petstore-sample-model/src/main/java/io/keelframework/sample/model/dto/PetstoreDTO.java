package io.keelframework.sample.model.dto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Petstore domain DTOs — used by the sample MCP tools.
 * These match the schema of the public Swagger Petstore demo API
 * (https://petstore3.swagger.io/api/v3), used as the backend for
 * this reference project so it works out of the box, with no need
 * to stand up your own service. Replace them with your own business
 * DTOs, or delete them if you don't need them.
 */


public class PetstoreDTO {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Category(
            Long id,
            String name
    ) {}

    // The public Petstore demo has years of inconsistent data: most
    // "tags" entries are objects like {"id":0,"name":"..."}, but some
    // legacy entries are plain strings instead. Accept both shapes so
    // one bad historical entry doesn't break the whole list fetch.
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Tag(
            Long id,
            String name
    ) {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static Tag fromString(String name) {
            return new Tag(null, name);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PetResponse(
            Long id,
            String name,
            Category category,
            List<String> photoUrls,
            List<Tag> tags,
            String status
    ) {}

    public record PetListResponse(
            List<PetResponse> pets,
            int total
    ) {}

    public record PetRequest(
            Long id,
            String name,
            Category category,
            List<String> photoUrls,
            List<Tag> tags,
            String status
    ) {}

}