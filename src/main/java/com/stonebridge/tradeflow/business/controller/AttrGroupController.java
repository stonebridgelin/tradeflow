package com.stonebridge.tradeflow.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.attribute.AttrGroup;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrGroupDTO;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrAttrgroupRelationDto;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrGroupVO;
import com.stonebridge.tradeflow.business.service.AttrGroupService;
import com.stonebridge.tradeflow.business.service.AttrService;
import com.stonebridge.tradeflow.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Business库pms_attr_group表的Controller") // 定义 API 组名称
@RestController
@RequestMapping("/api/attrGroup")
public class AttrGroupController {

    private final AttrGroupService attrGroupService;

    private final AttrService attrService;

    @Autowired
    public AttrGroupController(AttrGroupService attrGroupService, AttrService attrService) {
        this.attrGroupService = attrGroupService;
        this.attrService = attrService;
    }

    @PostMapping("list")
    public Result<Object> getAttrGroupsByCatId(@RequestBody AttrGroupDTO attrGroupDTO) {
        Page<AttrGroupVO> attrGroupPage = attrGroupService.queryPage(attrGroupDTO);
        return Result.ok(attrGroupPage);
    }

    @GetMapping("get/{id}")
    public Result<AttrGroupVO> getAttrGroupById(@PathVariable("id") String id) {
        AttrGroupVO attrGroupVO = attrGroupService.selectAttrGroupById(id);
        return Result.ok(attrGroupVO);
    }

    @PutMapping("update")
    public Result<Object> update(@RequestBody AttrGroup attrGroup) {
        attrGroupService.updateAttrGroup(attrGroup);
        return Result.ok();
    }

    @DeleteMapping("delete/{id}")
    public Result<Object> deleteById(@PathVariable("id") String id) {
        attrGroupService.deleteAttrGroup(id);
        return Result.ok();
    }

    @PostMapping("save")
    public Result<Object> save(@RequestBody AttrGroup attrGroup) {
        attrGroupService.saveAttrGroup(attrGroup);
        return Result.ok();
    }

    @GetMapping("getSortRangeByCatId/{catId}")
    public Result<Integer> getSortRangeByCatId(@PathVariable("catId") String catId) {
        return Result.ok(attrGroupService.getSortRangeByCatId(catId));
    }

    @GetMapping("getAttrGroupListByCatId/{catId}")
    public Result<Object> getAttrGroupListByCatId(@PathVariable("catId") String catId) {
        return Result.ok(attrGroupService.getAttrGroupListByCatId(catId));
    }

    @PostMapping("delete/relation")
    public Result<Object> deleteRelation(@RequestBody List<AttrAttrgroupRelationDto> attrAttrgroupRelationDtos) {
        attrService.deleteRelation(attrAttrgroupRelationDtos);
        return Result.ok();
    }
}
