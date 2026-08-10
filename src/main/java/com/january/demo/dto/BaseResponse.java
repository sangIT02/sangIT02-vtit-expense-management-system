package com.january.demo.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {

    private String message;
    private T data;
    private boolean success;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Tạo response thành công kèm thông báo và dữ liệu trả về.
     *
     * @param message thông báo của response
     * @param data dữ liệu trả về
     * @param <T> kiểu dữ liệu của response
     * @return {@link BaseResponse} có trạng thái thành công
     */
    public static <T> BaseResponse<T> success(String message, T data) {
        return BaseResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Tạo response thành công chỉ kèm thông báo.
     *
     * @param message thông báo của response
     * @param <T> kiểu dữ liệu của response
     * @return {@link BaseResponse} có trạng thái thành công
     */
    public static <T> BaseResponse<T> success(String message) {
        return BaseResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    /**
     * Tạo response lỗi kèm thông báo.
     *
     * @param message thông báo lỗi
     * @param <T> kiểu dữ liệu của response
     * @return {@link BaseResponse} có trạng thái thất bại
     */
    public static <T> BaseResponse<T> error(String message) {
        return BaseResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
