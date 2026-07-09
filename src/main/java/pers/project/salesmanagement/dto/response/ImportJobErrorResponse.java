package pers.project.salesmanagement.dto.response;

public record ImportJobErrorResponse(
        Long id,
        int rowNumber,
        String errorMessage,
        String rawData
) {
}
