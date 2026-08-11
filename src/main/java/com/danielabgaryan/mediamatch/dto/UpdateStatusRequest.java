package com.danielabgaryan.mediamatch.dto;

import com.danielabgaryan.mediamatch.model.Status;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private Status status;

    public UpdateStatusRequest(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
