package com.january.demo.exception;

import com.january.demo.dto.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
    /**
     * Xu ly loi request thieu query parameter bat buoc.
     *
     * @param exception ngoai le chua ten parameter bi thieu
     * @return response loi 400 voi thong bao parameter bi thieu
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<Object>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Thieu tham so bat buoc: " + exception.getParameterName()
        );
    }

    /**
     * Xu ly loi request parameter hoac path variable sai kieu du lieu.
     *
     * @param exception ngoai le chua thong tin tham so sai kieu
     * @return response loi 400 voi thong bao tham so khong hop le
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Tham so khong hop le: " + exception.getName()
        );
    }

    /**
     * Xu ly loi HTTP method khong duoc endpoint ho tro.
     *
     * @param exception ngoai le chua method khong duoc ho tro
     * @return response loi 405
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<Object>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.METHOD_NOT_ALLOWED, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method khong duoc ho tro");
    }

    /**
     * Xu ly loi Content-Type khong duoc endpoint ho tro.
     *
     * @param exception ngoai le chua media type khong duoc ho tro
     * @return response loi 415
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<BaseResponse<Object>> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content-Type khong duoc ho tro");
    }

    /**
     * Xu ly loi tham so Java khong hop le.
     *
     * @param exception ngoai le tham so khong hop le
     * @return response loi 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

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
    /**
     * Xu ly cac loi token khong hop le, het han hoac da bi thu hoi.
     *
     * @param exception ngoai le token
     * @return response loi 401 voi thong bao tu exception
     */
    @ExceptionHandler({
            InvalidTokenException.class,
            TokenExpiredException.class,
            TokenRevokedException.class
    })
    public ResponseEntity<BaseResponse<Object>> handleTokenException(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

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
    /**
     * Xu ly loi tai khoan bi vo hieu hoa hoac khong duoc phep truy cap.
     *
     * @param exception ngoai le trang thai tai khoan
     * @return response loi 403 voi thong bao tu exception
     */
    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccountDisabledException(
            AccountDisabledException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.FORBIDDEN, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.FORBIDDEN, exception.getMessage());
    }

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
    /**
     * Xu ly loi tai nguyen dang duoc tham chieu nen khong the xoa/cap nhat.
     *
     * @param exception ngoai le tai nguyen dang duoc su dung
     * @return response loi 409 voi thong bao tu exception
     */
    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<BaseResponse<Object>> handleResourceInUseException(
            ResourceInUseException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.CONFLICT, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    /**
     * Xu ly loi rang buoc du lieu tu database nhu unique key hoac foreign key.
     *
     * @param exception ngoai le rang buoc du lieu
     * @return response loi 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.CONFLICT, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.CONFLICT, "Du lieu da ton tai hoac dang duoc tham chieu");
    }

    /**
     * Xu ly loi client gui qua nhieu request trong thoi gian ngan.
     *
     * @param exception ngoai le vuot gioi han request
     * @return response loi 429 voi thong bao tu exception
     */
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<BaseResponse<Object>> handleTooManyRequestsException(
            TooManyRequestsException exception,
            HttpServletRequest request
    ) {
        logClientError(HttpStatus.TOO_MANY_REQUESTS, exception.getMessage(), request);
        return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, exception.getMessage());
    }

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
