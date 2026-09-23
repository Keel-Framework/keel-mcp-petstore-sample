/*
 * Copyright 2026 Keel Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.keelframework.sample.mcp.tools;

import io.keelframework.mcp.adapters.rest.client.McpRestClient;
import io.keelframework.mcp.adapters.rest.client.McpRestClientFactory;
import io.keelframework.mcp.adapters.rest.exception.RestClientException;
import io.keelframework.mcp.adapters.rest.model.RestResponse;
import io.keelframework.mcp.common.exceptions.McpToolException;
import io.keelframework.sample.model.dto.PetstoreDTO;
import io.keelframework.sample.model.errors.PetstoreErrorsMsg;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import io.keelframework.mcp.observability.logging.handler.McpAuthLoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

/**
 * MCP Tools — Petstore domain (Keel reference project).
 *
 * These tools call the public Swagger Petstore demo API
 * (https://petstore3.swagger.io/api/v3) as the backend, so this
 * sample works out of the box with no service of your own to stand
 * up. Point "petstore" in keel.adapters.rest-client.services to a
 * different base-url once you replace this with a real backend.
 *
 * Each method demonstrates the standard Keel tool pattern:
 *   1. Typed DTO as the return type (never a generic Map/Object)
 *   2. Technical logging via McpAuthLoggingHandler (structured metrics:
 *      duration, HTTP status) AND diagnostic logging via SLF4J
 *      (descriptive messages, see PetstoreErrorsMsg)
 *   3. Error handling via McpToolException (Spring AI reports it
 *      back to the LLM as an error result, with no need for
 *      ToolExecutionException)
 */
@Component
public class PetstoreTools {

    private static final Logger log = LoggerFactory.getLogger(PetstoreTools.class);

    private static final String SERVICE_NAME = "petstore";
    private static final List<String> VALID_STATUSES = List.of("available", "pending", "sold");

    private McpRestClient petClient;
    private final McpRestClientFactory restClientFactory;
    private final McpAuthLoggingHandler mcplog;

    public PetstoreTools(McpRestClientFactory restClientFactory, McpAuthLoggingHandler mcplog) {
        this.restClientFactory = restClientFactory;
        this.mcplog = mcplog;
    }

    @PostConstruct
    void init(){
        this.petClient = restClientFactory.getClient(SERVICE_NAME);
    }

    @Tool(description = """
        Find a pet by its identifier.
        Use when the user asks about a specific pet or mentions an ID.
        Returns the pet's name, category and status.
        """)
    public PetstoreDTO.PetResponse findPet(
            @ToolParam(description = "Numeric ID of the pet. Example: 1, 2, 3")
            long petId) {

        long start = System.nanoTime();
        String path = "/pet/" + petId;
        try {
            RestResponse<PetstoreDTO.PetResponse> response = petClient.get(
                    "/pet/{petId}", PetstoreDTO.PetResponse.class, petId);

            long durationMs = (System.nanoTime() - start) / 1_000_000;
            mcplog.logTool(SERVICE_NAME, path, response.httpStatus(), durationMs);
            return response.body();
        }
        catch (RestClientException e) {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            mcplog.logTool(SERVICE_NAME, path, 500, durationMs);
            log.warn(PetstoreErrorsMsg.PET_NOT_FOUND_MSG, petId, path, e.getMessage());
            throw new McpToolException(SERVICE_NAME, path, 500,
                    "Could not retrieve pet " + petId, e);
        }
    }

    @Tool(description = """
        List pets filtered by status.
        Use when the user wants to see available, pending or sold pets.
        Returns the list with name, category, status and the total number of results.
        """)
    public PetstoreDTO.PetListResponse listPets(
            @ToolParam(description = "Status to filter by. Valid values: available, pending, sold.")
            String status) {

        if (!VALID_STATUSES.contains(status)) {
            log.warn(PetstoreErrorsMsg.PET_INVALID_STATUS_MSG, status);
            throw new McpToolException(SERVICE_NAME, "/pet/findByStatus", 400,
                    "Invalid status '" + status + "'. Valid values: available, pending, sold.");
        }

        long start = System.nanoTime();
        String path = "/pet/findByStatus";
        try {
            RestResponse<List<PetstoreDTO.PetResponse>> response =  petClient.getListWithQueryParams(
                    path, Map.of("status", status), PetstoreDTO.PetResponse[].class);

            long durationMs = (System.nanoTime() - start) / 1_000_000;
            mcplog.logTool(SERVICE_NAME, path, response.httpStatus(), durationMs);

            List<PetstoreDTO.PetResponse> pets = response.body();
            return new PetstoreDTO.PetListResponse(pets, pets.size());
        }
        catch (RestClientException e) {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            mcplog.logTool(SERVICE_NAME, path, 500, durationMs);
            log.warn(PetstoreErrorsMsg.PET_LIST_FAILED_MSG, status, path, e.getMessage());
            throw new McpToolException(SERVICE_NAME, path, 500,
                    "Could not retrieve the pet list for status '" + status + "'", e);
        }
    }

    @Tool(description = """
        Register a new pet in the system.
        Use when the user wants to add a new pet.
        Requires name, category and status — ask the user if any are missing.
        Returns the registered pet with its assigned ID.
        """)
    public PetstoreDTO.PetResponse registerPet(
            @ToolParam(description = "Pet's name. Required.")
            String name,
            @ToolParam(description = "Pet's category. Example: Dog, Cat, Bird.")
            String category,
            @ToolParam(description = "Pet's status. Valid values: available, pending, sold.")
            String status) {

        if (!VALID_STATUSES.contains(status)) {
            log.warn(PetstoreErrorsMsg.PET_INVALID_STATUS_MSG, status);
            throw new McpToolException(SERVICE_NAME, "/pet", 400,
                    "Invalid status '" + status + "'. Valid values: available, pending, sold.");
        }

        long start = System.nanoTime();
        String path = "/pet";
        // Mirrors the exact payload shape confirmed working against the
        // public Swagger Petstore demo (verified via curl): explicit id,
        // non-empty photoUrls/tags placeholders — this backend appears
        // to reject empty arrays or a missing top-level id with a
        // generic 500 rather than a clear validation error.
        PetstoreDTO.PetRequest request = new PetstoreDTO.PetRequest(
                System.currentTimeMillis(),
                name,
                new PetstoreDTO.Category(1L, category),
                List.of("https://example.test/pets/placeholder.jpg"),
                List.of(new PetstoreDTO.Tag(0L, "string")),
                status);

        try {
            RestResponse<PetstoreDTO.PetResponse> response =
                    petClient.post(path, request, PetstoreDTO.PetResponse.class);

            long durationMs = (System.nanoTime() - start) / 1_000_000;
            mcplog.logTool(SERVICE_NAME, path, response.httpStatus(), durationMs);
            return response.body();
        }
        catch (RestClientException e) {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            mcplog.logTool(SERVICE_NAME, path, 500, durationMs);
            log.warn(PetstoreErrorsMsg.PET_CREATION_FAILED_MSG, name, path, e.getMessage(), e.getMessage());
            throw new McpToolException(SERVICE_NAME, path, 500,
                    "Could not register pet '" + name + "'", e);
        }
    }


}
