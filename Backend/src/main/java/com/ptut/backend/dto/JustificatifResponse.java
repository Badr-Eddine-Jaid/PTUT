package com.ptut.backend.dto;

public class JustificatifResponse {

    private Long id;
    private String fileName;
    private String url;
    private String uploadedAt;

    public JustificatifResponse() {
    }

    public JustificatifResponse(Long id, String fileName, String url, String uploadedAt) {
        this.id = id;
        this.fileName = fileName;
        this.url = url;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUrl() {
        return url;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUploadedAt(String uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}