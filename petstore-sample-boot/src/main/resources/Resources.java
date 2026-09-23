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
 * MCP Resources — context and data exposed to the LLM.
 *
 * DEVELOPER GUIDE:
 * ─────────────────────────────────────────────────────────────────
 * A Resource is information the LLM can query without executing
 * actions against the backend.
 *
 * WHEN TO USE RESOURCES:
 *  - Data catalogs that rarely change (product lines, types, statuses)
 *  - Business rules the LLM must respect
 *  - Usage guides for the available tools
 *  - Domain context the LLM doesn't already know
 *
 * RESOURCE TYPES:
 *
 *  1. STATIC — hardcoded content, never changes
 *     public String myResource() {
 *         return "fixed content";
 *     }
 *
 *  2. DYNAMIC — real-time content from the backend
 *     public String myResource() {
 *         McpRestClient client = restClientFactory.getClient("service");
 *         return client.get("/catalog/", String.class).body();
 *     }
 *
 *
 * @McpResource ANNOTATION (Spring AI standard):
 * ─────────────────────────────────────────────────────────────────
 *  uri         → unique identifier for the resource
 *                format: "mcp-petstore-sample://descriptive-name"
 *  name        → human-readable name for the LLM
 *  description → CRITICAL — the LLM decides whether to query this
 *                resource based on this description. Be specific:
 *                "Query when the user asks about X"
 *  mimeType    → "text/plain" or "application/json"
 *
 * NOTE: This class is OPTIONAL.
 * Remove it if you don't need to expose context to the LLM.
 * ─────────────────────────────────────────────────────────────────
 */
@Component
public class Resources {

    // ================================================
    // EXAMPLES — remove when you implement your own prompts
    // ================================================

    /**
     * Ejemplo 1 — Guía de tools disponibles para que el LLM sepa cuándo invocar cada una
     * Resource estático.
     * RECOMENDADO — mantener actualizado cuando añadas nuevas tools.
     *
     * @McpResource(
     *       uri = "mcpsca://tools-guide",
     *       name = "Guía de Tools disponibles",
     *       description = "Describe cuándo y cómo usar cada tool disponible. " +
     *               "Consultar cuando el LLM necesite decidir qué tool invocar.",
     *       mimeType = "text/plain"
     * )
     * public String toolsGuide() {
     *   return """
     *           TOOLS DISPONIBLES EN mcpsca:
     *
     *           // TODO: documenta aquí cada tool que implementes en HelloWorldItemMapper.java
     *           // Formato recomendado:
     *
     *           1. nombre_tool(parametros)
     *              - Cuándo usar: situación donde el LLM debe invocarla
     *              - Parámetros:  descripción de cada parámetro
     *              - Ejemplo:     frase del usuario que activa esta tool
     *              - Devuelve:    formato y contenido de la respuesta
     *
     *           REGLAS GENERALES:
     *           - Nunca inventar datos — usar siempre las tools para información real
     *           - Si faltan parámetros obligatorios, solicitarlos al usuario antes de invocar
     *           - Responde siempre en español
     *           """;
    * } */

    /**
     * Ejemplo 2  — atálogo obtenido del backend en tiempo real
     * Resource dinámico (comentado).
     *
     * Para usarlo:
     *  1. Inyecta McpRestClientFactory en el constructor
     *  2. Configura el servicio en application-dev.yml
     *  3. Descomenta el método
     *
     * @McpResource(
     *         uri = "mcp-petstore-sample://catalogo",
     *         name = "Catálogo dinámico",
     *         description = "Datos actualizados del backend en tiempo real. " +
     *                 "Consultar cuando el usuario pregunte por datos que cambian frecuentemente.",
     *         mimeType = "application/json"
     * )
     * public String catalogoDinamico() {
     *     McpRestClient client = restClientFactory.getClient("nombre-servicio");
     *     return client.get("/catalogo/", String.class).body();
     * }
     */

}