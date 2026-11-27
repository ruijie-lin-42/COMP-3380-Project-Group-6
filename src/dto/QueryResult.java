package dto;

import java.util.List;

public record QueryResult(
        List<String> columns,
        List<List<Object>> rows
){}