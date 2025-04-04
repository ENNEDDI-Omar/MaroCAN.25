package com.projects.server.dto.response;

import lombok.*;

@Data
@Builder
public class AuthResponse
{
    private String message;
    private String accessToken;
    private String refreshToken;
    private boolean success;
}
