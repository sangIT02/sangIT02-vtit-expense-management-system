package com.january.demo.exception;

import com.january.demo.dto.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý lỗi validate dữ liệu request body khi các annotation như
     * {@code @Valid}, {@code @NotBlank}, {@code @Email}, {@code @Size}
     * không thỏa mãn điều kiện.
     *
     * @param exception ngoại lệ chứa danh sách lỗi validate của từng field
     * @return response lỗi 400 kèm danh sách field và thông báo lỗi tương ứng
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        logClientError(HttpStatus.BAD_REQUEST, "Request body validation failed: " + errors, request);

        BaseResponse<Map<String, String>> response = BaseResponse.<Map<String, String>>builder()
                .success(false)
                .message("Dữ liệu không hợp lệ")
                .data(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Xử lý lỗi validate tham số trên {@code @PathVariable} hoặc
     * {@code @RequestParam} khi không thỏa mãn các ràng buộc đã khai báo.
     *
     * @param exception ngoại lệ chứa danh sách lỗi ràng buộc của các tham số
     * @return response lỗi 400 kèm danh sách tham số và thông báo lỗi tương ứng
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        Map<String, String> errors = exception.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        logClientError(HttpStatus.BAD_REQUEST, "Request parameter validation failed: " + errors, request);

        BaseResponse<Map<String, String>> response = BaseResponse.<Map<String, String>>builder()
                .success(false)
                .message("Tham số không hợp lệ")
                .data(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Xử lý lỗi request body không đọc được, thường xảy ra khi JSON sai format,
     * enum không hợp lệ hoặc client không gửi body bắt buộc.
     *
     * @param exception ngoại lệ phát sinh khi Spring không parse được request body
     * @return response lỗi 400 với thông báo body request không hợp lệ
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Body request không hợp lệ hoặc sai định dạng JSON");
    }

    /**
     * Xử lý các lỗi nghiệp vụ thuộc nhóm bad request, ví dụ mật khẩu mới trùng
     * mật khẩu cũ hoặc dữ liệu đầu vào không phù hợp với quy tắc nghiệp vụ.
     *
     * @param exception ngoại lệ nghiệp vụ thuộc nhóm bad request
     * @return response lỗi 400 với thông báo từ exception
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadRequestException(
            BadRequestException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * Xử lý lỗi xung đột dữ liệu, ví dụ username hoặc email đã tồn tại trong hệ thống.
     *
     * @param exception ngoại lệ thể hiện xung đột dữ liệu
     * @return response lỗi 409 với thông báo từ exception
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<BaseResponse<Object>> handleConflictException(
            ConflictException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.CONFLICT, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    /**
     * Xử lý lỗi xác thực, ví dụ sai username/password, refresh token không hợp lệ,
     * hết hạn hoặc đã bị thu hồi.
     *
     * @param exception ngoại lệ xác thực
     * @return response lỗi 401 với thông báo từ exception
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BaseResponse<Object>> handleUnauthorizedException(
            UnauthorizedException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    /**
     * Xử lý lỗi bị từ chối truy cập, bao gồm lỗi nghiệp vụ như tài khoản bị khóa,
     * chưa kích hoạt, chưa xác thực email và lỗi không đủ quyền từ Spring Security.
     *
     * @param exception ngoại lệ thể hiện hành động bị từ chối truy cập
     * @return response lỗi 403 với thông báo từ exception
     */
    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    public ResponseEntity<BaseResponse<Object>> handleForbiddenException(
            Exception exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.FORBIDDEN, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    /**
     * Xử lý lỗi không tìm thấy tài nguyên, ví dụ User, Role hoặc RefreshToken
     * không tồn tại.
     *
     * @param exception ngoại lệ thể hiện tài nguyên không tồn tại
     * @return response lỗi 404 với thông báo từ exception
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleNotFoundException(
            NotFoundException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.NOT_FOUND, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * Xử lý các lỗi không mong muốn chưa được bắt bởi các handler cụ thể phía trên.
     *
     * @param exception ngoại lệ không xác định phát sinh trong quá trình xử lý request
     * @return response lỗi 500 với thông báo lỗi chung
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleException(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("{} {} -> {} {}", request.getMethod(), request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage(), exception);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi không mong muốn");
    }

    private void logClientError(HttpStatus status, String message, HttpServletRequest request) {
        log.warn("{} {} -> {} {}", request.getMethod(), request.getRequestURI(), status.value(), message);
    }

    private ResponseEntity<BaseResponse<Object>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(BaseResponse.error(message));
    }
}
