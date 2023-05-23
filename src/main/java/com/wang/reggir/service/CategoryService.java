package com.wang.reggir.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.reggir.pojo.Category;

public interface CategoryService extends IService<Category> {
    public void removeComplex(Long id);
}
