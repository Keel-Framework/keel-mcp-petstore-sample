# Keel MCP Petstore Sample

Un MCP Server de referencia construido con el [Keel Framework](https://keelframework.io), que demuestra una integración completa y real: autenticación JWT, un adaptador REST, logging estructurado, y Tools / Prompts / Resources de MCP — todo funcionando de fábrica contra **servicios públicos, sin necesidad de configuración**, para que puedas clonar, ejecutar y explorar sin registrarte en nada.

## Qué demuestra este proyecto

- ✅ **3 Tools MCP** que invocan una API REST externa real (`findPet`, `listPets`, `registerPet`)
- ✅ **2 Prompts MCP** que guían al LLM en flujos habituales (consulta por ID, recolección guiada de datos)
- ✅ **1 Resource MCP** que documenta las tools disponibles para el LLM
- ✅ **Autenticación JWT** (Streamable HTTP), validada contra un IdP OIDC público de demo
- ✅ **Logging JSON estructurado** (técnico, funcional, seguridad) vía Keel Observability
- ✅ Deserialización JSON resiliente frente a un backend **públicamente editable** con años de datos inconsistentes

Sin Keycloak, sin API keys, sin registro — todo funciona contra servicios públicos de demostración.

## Requisitos previos

- Java 25
- Maven 3.8+
- Conexión a internet (ambos backends usados son servicios públicos alojados)

## Backends utilizados (públicos, sin configuración necesaria)

| Propósito | Servicio | URL |
|---|---|---|
| Datos REST | API de demo [Swagger Petstore](https://petstore3.swagger.io) | `https://petstore3.swagger.io/api/v3` |
| JWT / OIDC | Demo pública de [Duende IdentityServer](https://demo.duendesoftware.com) | `https://demo.duendesoftware.com` |

⚠️ Ambos son servicios de demo **públicos y compartidos**. No los uses para nada más allá de probar este proyecto — los datos pueden reiniciarse, y los datos de prueba de otras personas (con formas inconsistentes) son justamente el motivo por el que los DTOs de este proyecto están escritos de forma defensiva (ver [Manejo de datos públicos inconsistentes](#manejo-de-datos-públicos-inconsistentes) más abajo).

## Ejecución en local

```bash
git clone https://github.com/Keel-Framework/keel-mcp-petstore-sample.git
cd keel-mcp-petstore-sample/petstore-sample-boot
mvn clean install
mvn spring-boot:run
```

El servidor arranca en `http://localhost:8080`, con el endpoint MCP en `POST /mcp`.

### Obtener un JWT para autenticar las peticiones

Este proyecto valida los JWT contra el IdP público de demo de Duende, usando el grant `client_credentials` (máquina-a-máquina, sin necesidad de login de usuario):

```bash
curl -X POST "https://demo.duendesoftware.com/connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=m2m&client_secret=secret&grant_type=client_credentials&scope=api"
```

Copia el `access_token` de la respuesta y úsalo como token `Bearer` en el header `Authorization` de cada petición a `/mcp`.

### Conectar con MCP Inspector (recomendado)

```bash
npx @modelcontextprotocol/inspector@latest
```

En la interfaz de Inspector:
1. **Transport Type**: `Streamable HTTP`
2. **URL**: `http://localhost:8080/mcp`
3. **Authorization header**: `Bearer <tu-token>`
4. Clic en **Connect**

Deberías ver 3 tools, 2 prompts y 1 resource listados.

![MCP Inspector connected to the Petstore sample](img/mcp-inspector-screenshot.png)

Tools - listPets::

![MCP Inspector connected to the Petstore sample](img/mcp-tool-01-screenshot.png)

Tools - findPet::
![MCP Inspector connected to the Petstore sample](img/mcp-tool-02-screenshot.png)

Tools - registerPet::
![MCP Inspector connected to the Petstore sample](img/mcp-tool-03-screenshot.png)


## Tools

| Tool | Descripción | Llamada al backend |
|---|---|---|
| `findPet(petId)` | Busca una mascota por su ID numérico | `GET /pet/{petId}` |
| `listPets(status)` | Lista mascotas filtrando por estado (`available`, `pending`, `sold`) | `GET /pet/findByStatus?status=...` |
| `registerPet(name, category, status)` | Registra una nueva mascota | `POST /pet` |

## Prompts

| Prompt | Propósito |
|---|---|
| `petstore-find-pet` | Plantilla para buscar una mascota por ID |
| `petstore-register-pet` | Guía al modelo para recolectar `name`, `category` y `status` antes de invocar `registerPet`, mediante un ejemplo de dos mensajes (USER + ASSISTANT) |

## Resources

| Resource | URI | Propósito |
|---|---|---|
| Guía de tools | `petstore://tools-guide` | Guía en texto plano describiendo cuándo y cómo usar cada tool |

## Manejo de datos públicos inconsistentes

`petstore3.swagger.io` es una demo **públicamente editable**, usada por incontables developers a lo largo de los años. Algunas entradas existentes tienen formas de datos que no coinciden exactamente con el esquema OpenAPI (por ejemplo, una entrada de `tags` que es un string simple en vez de `{"id": ..., "name": ...}`). Los DTOs de este proyecto manejan esto correctamente:

```java
@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
public static Tag fromString(String name) {
    return new Tag(null, name);
}
```

Si estás construyendo contra tu propio backend con datos limpios y controlados, probablemente no necesites este tipo de deserialización defensiva — se incluye aquí específicamente porque este proyecto apunta a un sandbox público y compartido.

## Estructura del proyecto

```
petstore-sample/
├── petstore-sample-model/     # DTOs (PetstoreDTO), constantes de mensajes de error
├── petstore-sample-mcp/       # Tools, Prompts, Resources
└── petstore-sample-boot/      # Aplicación Spring Boot, configuración
```

## Configuración

La configuración local vive en `petstore-sample-boot/src/main/resources/application-local.yml`:

```yaml
keel:
  adapters:
    rest-client:
      services:
        petstore:
          base-url: https://petstore3.swagger.io/api/v3
          log-requests: true
  mcp:
    transport:
      auth:
        enabled: true
        jwt:
          issuer: https://demo.duendesoftware.com
          jwks-uri: https://demo.duendesoftware.com/.well-known/openid-configuration/jwks
```

## Construido con Keel Framework

Este proyecto de ejemplo funciona sobre [Keel Framework](https://keelframework.io) — una arquitectura para construir MCP Servers listos para producción sobre Spring AI, con autenticación JWT, gestión de sesión, observabilidad y adaptadores REST ya resueltos por ti.

- 📖 [Documentación de Keel Framework](https://keelframework.io/es/docs/introduction.html)
- 🚀 [Guía rápida](https://keelframework.io/es/docs/quickstart.html)
- 💻 [Código fuente de Keel Framework](https://github.com/Keel-Framework/keel-mcp-java)

## Licencia

Apache License 2.0 — ver [LICENSE](LICENSE).
