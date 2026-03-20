package com.onesolutions.dsd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    private String error;
    private String errorCode;
    private String message;
    private Long timestamp;
    private String path;

}

