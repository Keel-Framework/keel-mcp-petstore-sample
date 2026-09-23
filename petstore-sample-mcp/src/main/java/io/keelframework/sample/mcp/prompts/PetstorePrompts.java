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
package io.keelframework.sample.mcp.prompts;


import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MCP Prompts — Petstore domain (Keel reference project).
 *
 * These prompts guide the LLM through the two most common Petstore
 * flows: looking up a single pet, and registering a new one with all
 * required fields validated up front.
 */

@Component
public class PetstorePrompts {

    @McpPrompt(
            name = "petstore-find-pet",
            description = "Template for looking up a pet by its ID. " +
                    "Use when the user wants full details about one specific pet."
    )
    public McpSchema.GetPromptResult findPetPrompt(
            @McpArg(description = "Numeric ID of the pet. Example: 1, 2, 3")
            String petId) {
        return new McpSchema.GetPromptResult(
                "Look up a pet by ID",
                List.of(
                        new McpSchema.PromptMessage(
                                McpSchema.Role.USER,
                                new McpSchema.TextContent(
                                        "Give me the full details of the pet with ID " + petId + ".\n" +
                                                "Include its name, category and current status."))
                ));
    }

    @McpPrompt(
            name = "petstore-register-pet",
            description = "Template for registering a new pet, guiding the model to " +
                    "collect name, category and status before calling the registerPet tool. " +
                    "Use when the user wants to add a pet but hasn't given all the required fields yet."
    )
    public McpSchema.GetPromptResult registerPetPrompt() {

        return new McpSchema.GetPromptResult(
                "Register a new pet, step by step",
                List.of(
                        new McpSchema.PromptMessage(
                                McpSchema.Role.USER,
                                new McpSchema.TextContent(
                                        "I want to register a new pet.")),
                        new McpSchema.PromptMessage(
                                McpSchema.Role.ASSISTANT,
                                new McpSchema.TextContent(
                                        "Sure! I need three things before I can register the pet:\n" +
                                                "1. Name\n" +
                                                "2. Category (e.g. Dog, Cat, Bird)\n" +
                                                "3. Status (available, pending, or sold)\n\n" +
                                                "Please provide all three, and I'll confirm the details " +
                                                "back to you before registering."))
                ));
    }
}
