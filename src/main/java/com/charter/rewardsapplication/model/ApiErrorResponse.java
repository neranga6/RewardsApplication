package com.charter.rewardsapplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record ApiErrorResponse(

        @JsonProperty("timestamp")
        LocalDateTime timestamp,

        @JsonProperty("status")
        int status,

        @JsonProperty("error")
        String error,

        @JsonProperty("message")
        String message,

        @JsonProperty("path")
        String path
) {}