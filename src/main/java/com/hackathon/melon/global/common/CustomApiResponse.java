package com.hackathon.melon.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonPropertyOrder({"isSuccess", "message", "result", "errorCode"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomApiResponse<T> {
    private final boolean isSuccess;
    private final String message;
    private final T result;
    private final String errorCode;

    public static <T> CustomApiResponse<T> onSuccess(T result) {
        return new CustomApiResponse<>(true, "", result, null);
    }

    public static <T> CustomApiResponse<T> onFailure(String message, String errorCode) {
        return new CustomApiResponse<>(false, message, null, errorCode);
    }
}