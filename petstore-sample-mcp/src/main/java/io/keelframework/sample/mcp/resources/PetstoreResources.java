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

package io.keelframework.sample.mcp.resources;

import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

/**
 * MCP Resources — Petstore domain (Keel reference project).
 *
 * Exposes static context to the LLM about the Petstore tools, so it
 * knows when and how to invoke each one without guessing.
 */
@Component
public class PetstoreResources {

    @McpResource(
            uri = "petstore://tools-guide",
            name = "Petstore tools guide",
            description = "Describes when and how to use each available Petstore tool. " +
                    "Consult when the LLM needs to decide which tool to call.",
            mimeType = "text/plain"
    )
    public String toolsGuide() {
        return """
                AVAILABLE TOOLS IN petstore-sample:
 
                1. findPet(petId)
                   - When to use:  the user asks about a specific pet or gives an ID
                   - Parameters:   petId (long) — numeric identifier of the pet
                   - Example:      "What's the status of pet 5?"
                   - Returns:      id, name, category and status of the pet
 
                2. listPets(status)
                   - When to use:  the user wants to browse pets by availability
                   - Parameters:   status (String) — one of: available, pending, sold
                   - Example:      "Show me all available pets"
                   - Returns:      a list of pets matching that status, plus the total count
 
                3. registerPet(name, category, status)
                   - When to use:  the user wants to add a new pet to the store
                   - Parameters:   name (String), category (String), status (String, one of:
                                   available, pending, sold)
                   - Example:      "Register a new dog named Rex, available"
                   - Returns:      the newly registered pet, including its assigned ID
 
                GENERAL RULES:
                - Never invent pet data — always use the tools for real information
                - If a required parameter is missing, ask the user before calling the tool
                - "status" only accepts: available, pending, sold — reject anything else
                  and ask the user to pick one of these three values
                """;
    }

}