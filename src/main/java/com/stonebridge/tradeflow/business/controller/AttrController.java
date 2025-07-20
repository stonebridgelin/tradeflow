package com.stonebridge.tradeflow.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.BasePageDTO;
import com.stonebridge.tradeflow.business.entity.attribute.Attr;
import com.stonebridge.tradeflow.business.entity.attribute.AttrAttrgroupRelation;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrAttrgroupRelationDto;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrDTO;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrRespVo;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrVo;
import com.stonebridge.tradeflow.business.service.AttrAttrgroupRelationService;
import com.stonebridge.tradeflow.business.service.AttrService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attr")
public class AttrController {
    private final AttrService attrService;

    private final AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    public AttrController(AttrService attrService, AttrAttrgroupRelationService attrAttrgroupRelationService) {
        this.attrService = attrService;
        this.attrAttrgroupRelationService = attrAttrgroupRelationService;
    }

    /**
     * 根据attrId获取pms_attr表对应的所有信息，以及关联的pms_attr_attrgroup_relation表里面的信息
     *
     * @param attrId ：pms_attr.attrId
     * @return :封装的pms_attr对应的所有信息
     */
    @RequestMapping("/attrInfo/{attrId}")
    public Result<Object> info(@PathVariable("attrId") String attrId) {
        AttrRespVo respVo = attrService.getAttrInfo(attrId);
        return Result.ok(respVo);
    }


    /**
     * 列表
     */
    /**
     * 以列表的形式展示符合条件的pms_attr表对应的所有信息，以及关联的pms_attr_attrgroup_relation表里面的信息
     *
     * @param attrDTO ：查询条件 ：关键字，分页信息等
     * @return ：符合条件的pms_attr表对应的所有信息，以及关联的pms_attr_attrgroup_relation表里面的信息封装的分页列表
     */
    @PostMapping("list")
    public Result<Object> list(@RequestBody AttrDTO attrDTO) {
        Page<AttrRespVo> attrGroupPage = attrService.queryPage(attrDTO);
        return Result.ok(attrGroupPage);
    }

    /**
     * 保存信息到pms_attr表，以及关联表pms_attr_attrgroup_relation
     *
     * @param attr ：接收参数的Attr+attrGroupId
     * @return ：保存成功的状态
     */
    @PostMapping("save")
    public Result<Object> save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);
        return Result.ok();
    }

    /**
     * 更新信息到pms_attr表，以及关联表pms_attr_attrgroup_relation
     *
     * @param attr 接收参数的Attr+attrGroupId
     * @return 更新成功的状态
     */
    @RequestMapping("/update")
    public Result<Object> update(@RequestBody AttrVo attr) {
        attrService.updateAttr(attr);
        return Result.ok();
    }


    /**
     * 根据attrId删除pms_attr表，以及关联表pms_attr_attrgroup_relation
     *
     * @param attrId attr.attrId
     * @return 删除成功
     */
    @DeleteMapping("/delete")
    public Result<Object> delete(@PathVariable("attrId") String attrId) {
        attrService.deleteAttrById(attrId);
        return Result.ok();
    }

    /**
     * 根据attrGroupId获取已经绑定的Attr基础属性(Attr.type='base')
     *
     * @param attrGroupId :分组id
     * @return ：
     */
    @GetMapping("getAttrByAttrGoupId/{attrGroupId}")
    public Result<Object> getAttrByAttrGoupId(@PathVariable("attrGroupId") String attrGroupId) {
        List<Attr> attrList = attrService.getAttrByAttrGoupId(attrGroupId);
        return Result.ok(attrList);
    }

    /**
     * 获取本分类下没有关联其他分组关联的属性
     *
     * @param attrGroupId 属性分组的id
     * @param basePageDTO 所在分类信息
     * @return :查询结果
     */
    @PostMapping("/getNoAttrRelation/{attrGroupId}")
    public Result<Object> attrNoRelation(@PathVariable("attrGroupId") String attrGroupId, @RequestBody BasePageDTO basePageDTO) {
        Page<Attr> attrPage = attrService.getNoRelationAttr(attrGroupId, basePageDTO);
        return Result.ok(attrPage);
    }

    /**
     * 属性和属性分组保存关联关系
     *
     * @param list: 参数集合
     * @return :处理结果
     */
    @PostMapping("/relation")
    public Result<Object> addRelation(@RequestBody List<AttrAttrgroupRelationDto> list) {
        List<AttrAttrgroupRelation> attrgroupRelationEntityList = list.stream().map((item) -> {
            AttrAttrgroupRelation relationEntity = new AttrAttrgroupRelation();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationService.saveBatch(attrgroupRelationEntityList);
        return Result.ok();
    }

    /**
     * 查询查询条件查询属性表（pms_attr）的数据
     * mallproduct/attr/base/list/225
     *
     * @param params    ：参数
     * @param catelogId ：分类的id（pms_category.id）
     * @param attrType  : 当为sale的时候查询<销售属性>attr_type=0，当为base的时候查询<所有属性>
     * @return :结果集
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    public Result<Object> baseList(@RequestParam Map<String, Object> params,
                           @PathVariable("catelogId") String catelogId,
                           @PathVariable("attrType") String attrType) {
        Page<AttrRespVo> page = attrService.queryBaseAttrPage(params, catelogId, attrType);
        return Result.ok(page);
    }
}
