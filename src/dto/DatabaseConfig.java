package dto;

public record DatabaseConfig(
        String url,
        String username,
        String password
) {}
