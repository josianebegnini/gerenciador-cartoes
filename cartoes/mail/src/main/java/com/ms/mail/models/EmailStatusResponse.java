package com.ms.mail.models;

public class EmailStatusResponse {
    private Long emailId;
    private String emailTo;
    private String status;
    private Long userId;

    public EmailStatusResponse(Long emailId, String emailTo, String status, Long userId) {
        this.emailId = emailId;
        this.emailTo = emailTo;
        this.status = status;
        this.userId = userId;
    }

    public EmailStatusResponse() {
    }

    public Long getEmailId() {
        return emailId;
    }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
