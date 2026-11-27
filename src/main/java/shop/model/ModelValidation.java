package shop.model;

/** Small shared checks matching the existing database column limits. */
final class ModelValidation {

    private ModelValidation() {
    }

    static String requiredText(String value, int maxLength, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return optionalText(value, maxLength, field);
    }

    static String optionalText(String value, int maxLength, String field) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must not exceed " + maxLength + " characters");
        }
        return value;
    }
}
