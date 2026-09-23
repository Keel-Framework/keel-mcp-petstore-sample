package io.keelframework.sample.model.errors;

/**
 * PetstoreErrorsMsg
 *
 * Domain-specific error message templates for the Petstore sample
 * tools (see PetstoreTools). Used for structured logging with SLF4J's
 * {} placeholders.
 *
 * This is a constant holder — not meant to be instantiated, hence the
 * private constructor.
 */

public final class PetstoreErrorsMsg {

    public static final String HTTP_CLIENT_EXCEPTION_MSG = "Transmiting 4XX error from URL: {}. Cause: {}. Message: {}";
    public static final String HTTP_SERVER_EXCEPTION_MSG = "Transmiting 5XX error from URL: {}. Cause: {}. Message: {}";
    public static final String REST_CLIENT_EXCEPTION_MSG = "Error invoking URL: {}. Cause: {}. Message: {}";
    public static final String URI_SYNTAX_EXCEPTION_MSG = "Invalid URL: {}. Cause: {}. Message: {}";
    public static final String UNHANDLED_EXCEPTION_MSG =
            "An Unhandled Exception has occurred when trying to contact URL: {}. Cause: {}. Message: {}";

    // ==========================================
    // Petstore sample — domain-specific messages
    // ==========================================
    public static final String PET_NOT_FOUND_MSG = "Pet not found for id: {}. URL: {}. Message: {}";
    public static final String PET_INVALID_STATUS_MSG = "Invalid pet status '{}'. Expected one of: available, pending, sold.";
    public static final String PET_CREATION_FAILED_MSG = "Failed to register pet '{}'. URL: {}. Cause: {}. Message: {}";
    public static final String PET_LIST_FAILED_MSG = "Failed to retrieve pet list for status '{}'. URL: {}. Message: {}";

    private PetstoreErrorsMsg(){}

}