package com.january.demo.service;

import com.january.demo.dto.request.CategoryRequest;
import com.january.demo.dto.request.CategoryUpdateRequest;
import com.january.demo.dto.response.CategoryResponse;
import com.january.demo.dto.response.CategoryTreeNode;
import com.january.demo.enums.CategoryType;

import java.util.List;

public interface ICategoryService {

    CategoryResponse create(CategoryRequest request);

    List<CategoryResponse> getAll(CategoryType type, Long parentId);

    List<CategoryTreeNode> getTree();

    CategoryResponse getById(Long id);

    CategoryResponse update(Long id, CategoryUpdateRequest request);

    void delete(Long id);
}