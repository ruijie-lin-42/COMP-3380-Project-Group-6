package dto;

import java.util.List;

public record QueryDefinition(
        int id,
        String name,
        String description,
        List<QueryParameter> parameters,
        String sql
){}