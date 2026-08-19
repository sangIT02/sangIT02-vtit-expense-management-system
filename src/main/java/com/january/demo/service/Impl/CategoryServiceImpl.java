package com.january.demo.service.Impl;

import com.january.demo.dto.request.CategoryRequest;
import com.january.demo.dto.request.CategoryUpdateRequest;
import com.january.demo.dto.response.CategoryResponse;
import com.january.demo.dto.response.CategoryTreeNode;
import com.january.demo.entity.Category;
import com.january.demo.entity.User;
import com.january.demo.enums.CategoryType;
import com.january.demo.exception.BadRequestException;
import com.january.demo.exception.ConflictException;
import com.january.demo.exception.NotFoundException;
import com.january.demo.exception.ResourceInUseException;
import com.january.demo.repository.CategoryRepository;
import com.january.demo.repository.TransactionRepository;
import com.january.demo.repository.UserRepository;
import com.january.demo.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.january.demo.utils.SecurityUtils.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Long userId = getCurrentUserId();

        if (categoryRepository.existsByUser_IdAndNameAndType(userId, request.name(), request.type())) {
            throw new ConflictException("Danh muc da ton tai");
        }

        Category parent = resolveParent(userId, request.parentId(), request.type());

        Category category = Category.builder()
                .name(request.name())
                .type(request.type())
                .parent(parent)
                .user(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Khong tim thay nguoi dung")))
                .build();
        category = categoryRepository.save(category);
        return toResponse(category);
    }

    @Override
    public List<CategoryResponse> getAll(CategoryType type, Long parentId) {
        Long userId = getCurrentUserId();
        List<Category> categories;

        if (type != null) {
            categories = categoryRepository.findByUser_IdAndType(userId, type);
        } else if (parentId != null) {
            categories = categoryRepository.findByParent_Id(parentId);
        } else {
            categories = categoryRepository.findByUser_Id(userId);
        }

        return categories.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CategoryTreeNode> getTree() {
        Long userId = getCurrentUserId();
        List<Category> roots = categoryRepository.findByUser_IdAndParentIsNull(userId);
        return roots.stream()
                .map(this::toTreeNode)
                .toList();
    }

    @Override
    public CategoryResponse getById(Long id) {
        Category category = findOwned(id);
        return toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryUpdateRequest request) {
        Long userId = getCurrentUserId();
        Category category = findOwned(id);

        Category parent = resolveParent(userId, request.parentId(), category.getType());
        if (parent != null && parent.getId().equals(category.getId())) {
            throw new BadRequestException("Danh muc khong the la cha cua chinh no");
        }

        category.setName(request.name());
        category.setParent(parent);
        category = categoryRepository.save(category);
        return toResponse(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = findOwned(id);

        if (transactionRepository.findByCategory_Id(id).stream().findAny().isPresent()) {
            throw new ResourceInUseException("Danh muc dang duoc su dung trong giao dich");
        }
        if (categoryRepository.findByParent_Id(id).stream().findAny().isPresent()) {
            throw new ResourceInUseException("Danh muc dang co danh muc con");
        }

        categoryRepository.delete(category);
    }

    private Category resolveParent(Long userId, Long parentId, CategoryType type) {
        if (parentId == null) {
            return null;
        }
        Category parent = categoryRepository.findByIdAndUser_Id(parentId, userId)
                .orElseThrow(() -> new NotFoundException("Khong tim thay danh muc cha"));
        if (type != null && parent.getType() != type) {
            throw new BadRequestException("Danh muc cha phai cung loai voi danh muc con");
        }
        return parent;
    }

    private Category findOwned(Long id) {
        return categoryRepository.findByIdAndUser_Id(id, getCurrentUserId())
                .orElseThrow(() -> new NotFoundException("Khong tim thay danh muc"));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    private CategoryTreeNode toTreeNode(Category category) {
        List<CategoryTreeNode> children = categoryRepository.findByParent_Id(category.getId()).stream()
                .map(this::toTreeNode)
                .toList();
        return new CategoryTreeNode(category.getId(), category.getName(), category.getType(), children);
    }
}