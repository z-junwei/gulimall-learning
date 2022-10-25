package com.atguigu.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1 查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        // 2 组装成父子的树形结构
        List<CategoryEntity> level1Menus = entities
                .stream()
                .filter(categoryEntity -> categoryEntity.getParentCid().longValue() == 0)
                .map((menu)->{
                    menu.setChildren(getChildrenList(menu, entities));
                    return menu;})
                .sorted((menu1,menu2)->{
                    return (menu1.getSort() == null?0:menu1.getSort()) - (menu2.getSort() == null?0:menu2.getSort());
                }).collect(Collectors.toList());
        return level1Menus;
    }

    public List<CategoryEntity> getChildrenList(CategoryEntity root, List<CategoryEntity> all){
        List<CategoryEntity> children = all
                .stream()
                .filter(categoryEntity -> {
                    //注意此处应该用longValue()来比较，否则会出bug，因为parentCid和catId是long类型
                    return categoryEntity.getParentCid().longValue() == root.getCatId().longValue();})
                .map(categoryEntity -> {
                    // 1 找到子菜单
                    categoryEntity.setChildren(getChildrenList(categoryEntity, all));
                    return categoryEntity;})
                .sorted((menu1,menu2)->{
                    return (menu1.getSort() == null?0:menu1.getSort()) - (menu2.getSort() == null?0:menu2.getSort());
                }).collect(Collectors.toList());
        return children;
    }

}