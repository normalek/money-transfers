package com.revolut.transfers.api.dto.rs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorRs {
    private int errorCode;
    private String message;
    private String type;
    private String additionalInfo;
}
