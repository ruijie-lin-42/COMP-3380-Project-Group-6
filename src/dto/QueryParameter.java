package dto;

public record QueryParameter (
        String name,
        ParameterType type,
        String prompt
){}
