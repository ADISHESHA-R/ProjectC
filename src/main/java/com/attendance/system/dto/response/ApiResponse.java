package com.attendance.system.dto.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    /** Legacy / gateway clients that expect PascalCase {@code Success} alongside {@code success}. */
    @JsonGetter("Success")
    public boolean getSuccessPascalCase() {
        return success;
    }

    /** Some SPAs treat {@code status} as a second success flag (same value as {@code success}). */
    @JsonGetter("status")
    public boolean getStatusAsSuccessFlag() {
        return success;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
