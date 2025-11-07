package com.hackathon.melon.domain.project.entity;

/**
 * 서비스 타입 enum
 */
public enum ServiceType {
    FE("Frontend"),
    BE("Backend");

    private final String description;

    ServiceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}