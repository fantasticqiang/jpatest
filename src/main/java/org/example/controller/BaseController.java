package org.example.controller;

import org.example.common.PageResult;
import org.example.parameter.Filter;
import org.example.parameter.SimpleFilterResolver;
import org.example.repository.EntityBase;
import org.example.service.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

public abstract class BaseController<T extends EntityBase> {

    public abstract BaseService service();

    @RequestMapping(value = "filter", method = RequestMethod.GET)
    public PageResult filter(@Filter SimpleFilterResolver<T> simpleFilterResolver) {
        Page<T> page = service().filter(simpleFilterResolver);
        PageResult<T> result = new PageResult<>();
        result.setItems(page.getContent());
        result.setTotal(page.getTotalElements());
        result.setTotalPage((long) page.getTotalPages());
        return result;
    }

    @PostMapping("/add")
    public PageResult add(@RequestBody T entity) {
        PageResult<T> r = new PageResult<>();
        try {
            service().save(entity);
            r.setCode("100");
            r.setMsg("新增成功");
        } catch (Exception e) {
            r.setCode("101");
            r.setMsg("新增失败");
        }
        return r;
    }

    @PutMapping("/update")
    public PageResult update(@RequestBody T entity) {
        PageResult<T> r = new PageResult<>();
        try {
            service().update(entity);
            r.setCode("100");
            r.setMsg("新增成功");
        } catch (Exception e) {
            r.setCode("101");
            r.setMsg("新增失败");
        }
        return r;
    }
}
