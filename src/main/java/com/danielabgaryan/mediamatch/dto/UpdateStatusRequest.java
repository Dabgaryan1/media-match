package com.danielabgaryan.mediamatch.dto;

import com.danielabgaryan.mediamatch.model.Status;

public class UpdateStatusRequest {
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
