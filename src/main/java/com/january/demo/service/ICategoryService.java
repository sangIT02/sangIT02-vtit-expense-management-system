package com.january.demo.service;

import com.january.demo.dto.request.CategoryRequest;
import com.january.demo.dto.request.CategoryUpdateRequest;
import com.january.demo.dto.response.CategoryResponse;
import com.january.demo.dto.response.CategoryTreeNode;
import com.january.demo.enums.CategoryType;

import java.util.List;

public interface ICategoryService {

    /**
     * Tạo mới một danh mục cho người dùng hiện tại.
     *
     * <p>Tên danh mục phải là duy nhất theo loại trong phạm vi người dùng, và danh mục cha
     * (nếu có) phải cùng loại với danh mục con.</p>
     *
     * @param request thông tin tạo danh mục gồm tên, loại và danh mục cha (tùy chọn)
     * @return thông tin danh mục đã được tạo
     * @throws com.january.demo.exception.ConflictException nếu danh mục đã tồn tại
     */
    CategoryResponse create(CategoryRequest request);

    /**
     * Lấy danh sách danh mục của người dùng hiện tại với điều kiện lọc.
     *
     * <p>Nếu truyền {@code type} sẽ lọc theo loại, ngược lại nếu truyền {@code parentId}
     * sẽ lấy danh mục con trực tiếp của cha đó, còn nếu cả hai đều rỗng sẽ trả về toàn bộ
     * danh mục của người dùng.</p>
     *
     * @param type     loại danh mục cần lọc, có thể là {@code null}
     * @param parentId mã định danh danh mục cha, có thể là {@code null}
     * @return danh sách danh mục thỏa mãn điều kiện lọc
     */
    List<CategoryResponse> getAll(CategoryType type, Long parentId);

    /**
     * Xây dựng cây danh mục từ các danh mục gốc (không có cha) trở xuống.
     *
     * @return cây danh mục của người dùng hiện tại
     */
    List<CategoryTreeNode> getTree();

    /**
     * Lấy chi tiết một danh mục theo id nếu nó thuộc về người dùng hiện tại.
     *
     * @param id mã định danh của danh mục
     * @return thông tin chi tiết danh mục
     * @throws com.january.demo.exception.NotFoundException nếu danh mục không tồn tại
     *         hoặc không thuộc về người dùng hiện tại
     */
    CategoryResponse getById(Long id);

    /**
     * Cập nhật thông tin của một danh mục thuộc về người dùng hiện tại.
     *
     * <p>Danh mục không được phép trở thành cha của chính nó để tránh vòng lặp vô hạn.</p>
     *
     * @param id      mã định danh của danh mục cần cập nhật
     * @param request thông tin mới của danh mục
     * @return thông tin danh mục sau khi cập nhật
     */
    CategoryResponse update(Long id, CategoryUpdateRequest request);

    /**
     * Xóa một danh mục thuộc về người dùng hiện tại.
     *
     * <p>Danh mục đang được dùng trong giao dịch hoặc đang có danh mục con sẽ không được
     * phép xóa và gây ra lỗi xung đột tài nguyên.</p>
     *
     * @param id mã định danh của danh mục cần xóa
     * @throws com.january.demo.exception.ResourceInUseException nếu danh mục đang được sử dụng
     */
    void delete(Long id);
}