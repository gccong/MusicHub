package com.softuni.musichub.admin.category.services;

import com.softuni.musichub.admin.category.entities.Category;
import com.softuni.musichub.admin.category.models.bindingModels.AddCategory;
import com.softuni.musichub.admin.category.models.bindingModels.EditCategory;
import com.softuni.musichub.admin.category.models.views.CategoryView;
import com.softuni.musichub.admin.category.repositories.CategoryRepository;
import com.softuni.musichub.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
@Transactional
public class CategoryManipulationServiceImpl implements CategoryManipulationService {

    private final CategoryRepository categoryRepository;

    private final MapperUtil mapperUtil;

    @Autowired
    public CategoryManipulationServiceImpl(CategoryRepository categoryRepository,
                                           MapperUtil mapperUtil) {
        this.categoryRepository = categoryRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public CategoryView addCategory(AddCategory addCategory) {
        Category category = this.mapperUtil.getModelMapper()
                .map(addCategory, Category.class);
        Category savedCategory = this.categoryRepository.save(category);
        return this.mapperUtil.getModelMapper()
                .map(savedCategory, CategoryView.class);
    }

    @Override
    public boolean deleteById(Long categoryId) {
        boolean isCategoryExists = this.categoryRepository.existsById(categoryId);
        if (!isCategoryExists) {
            return false;
        }

        this.categoryRepository.deleteById(categoryId);
        return true;
    }

    @Override
    public CategoryView edit(EditCategory editCategory, Long id) {
        boolean isCategoryExists = this.categoryRepository.existsById(id);
        if (!isCategoryExists) {
            return null;
        }

        Category category = this.mapperUtil.getModelMapper()
                .map(editCategory, Category.class);
        category.setId(id);
        Category editedCategory = this.categoryRepository.save(category);
        return this.mapperUtil.getModelMapper()
                .map(editedCategory, CategoryView.class);
    }
}
