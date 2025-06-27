package com.stonebridge.tradeflow.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.attribute.AttrGroup;
import com.stonebridge.tradeflow.business.service.AttrGroupService;
import com.stonebridge.tradeflow.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Business库pms_attr_group表的Controller") // 定义 API 组名称
@RestController
@RequestMapping("/api/attrGroup")
public class AttrGroupController {

    AttrGroupService attrGroupService;

    @Autowired
    public AttrGroupController(AttrGroupService attrGroupService) {
        this.attrGroupService = attrGroupService;
    }

    @RequestMapping("list/{currentPage}/{pageSize}/{catId}")
    public Result<Object> getAttrGroupsByCatId(String keyword,@PathVariable String currentPage, @PathVariable String pageSize, @PathVariable(value = "catId") String categoryId) {
        Page<AttrGroup> attrGroupPage = attrGroupService.queryPage(currentPage, pageSize, categoryId,keyword);
        return Result.ok(attrGroupPage);
    }
}
