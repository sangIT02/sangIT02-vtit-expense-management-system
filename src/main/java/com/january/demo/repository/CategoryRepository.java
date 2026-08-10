package com.january.demo.repository;

import com.january.demo.entity.Category;
import com.january.demo.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUser_Id(Long userId);

    List<Category> findByUser_IdAndType(Long userId, CategoryType type);

    List<Category> findByUser_IdAndParentIsNull(Long userId);

    List<Category> findByParent_Id(Long parentId);

    Optional<Category> findByIdAndUser_Id(Long id, Long userId);

    boolean existsByUser_IdAndNameAndType(Long userId, String name, CategoryType type);
}
