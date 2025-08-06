package com.stonebridge.tradeflow.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.BasePageDTO;
import com.stonebridge.tradeflow.business.entity.attribute.Attr;
import com.stonebridge.tradeflow.business.entity.attribute.AttrAttrgroupRelation;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrAttrgroupRelationDto;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrDTO;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrRespVo;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrVo;
import com.stonebridge.tradeflow.business.entity.product.ProductAttrValue;
import com.stonebridge.tradeflow.business.service.AttrAttrgroupRelationService;
import com.stonebridge.tradeflow.business.service.AttrService;
import com.stonebridge.tradeflow.business.service.impl.ProductAttrValueServiceImpl;
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
    private final ProductAttrValueServiceImpl productAttrValueService;

    @Autowired
    public AttrController(AttrService attrService, AttrAttrgroupRelationService attrAttrgroupRelationService, ProductAttrValueServiceImpl productAttrValueService) {
        this.attrService = attrService;
        this.attrAttrgroupRelationService = attrAttrgroupRelationService;
        this.productAttrValueService = productAttrValueService;
    }

    /**
     * 使用页面：src/components/attr/AddAttr.vue -->getAttrInfo-->/api/attr/attrInfo/attrId
     * 根据主键attrId获取pms_attr表唯一对应的属性数据；如果attr_type="1"则查询关联的pms_attr_attrgroup_relation表里面的信息
     *
     * @param attrId ：pms_attr.attrId
     * @return :主键attrId唯一对应的pms_attr的数据
     */
    @RequestMapping("/attrInfo/{attrId}")
    public Result<Object> info(@PathVariable("attrId") String attrId) {
        AttrRespVo respVo = attrService.getAttrInfo(attrId);
        return Result.ok(respVo);
    }

    /**
     * 使用页面：src/components/attr/Attr.vue -->getAttrList-->/api/attr/list
     * 属性页面根据查询条件以分页形式加载符合条件的pms_attr表对应的信息，
     * 以及关联的pms_attr_attrgroup_relation表里面的信息（仅是pms_attr.attr_type='base'）才有pms_attr_attrgroup_relation关联关系
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
     * 使用页面src/components/attr/AddAttr.vue --> saveAttr -->/api/attr/save
     * 新建属性信息，将其保存到pms_attr表；如果是基础属性，则要保存属性与属性分组的关联关系到pms_attr_attrgroup_relation
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
     * 使用页面：src/components/attr/AddAttr.vue -->updateAttr--> /api/attr/update
     * 根据attrId更新信息pms_attr表指定的唯一信息，如果基础属性怎更新关联表pms_attr_attrgroup_relation
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
     * 使用页面：src/components/attr/AddAttr.vue --> deleteAttr --> /api/attr/delete
     * 根据attrId删除pms_attr表，以及关联表pms_attr_attrgroup_relation
     *
     * @param attrId attr.attrId
     * @return 删除成功
     */
    @DeleteMapping("/delete/{attrId}")
    public Result<Object> delete(@PathVariable("attrId") String attrId) {
        attrService.deleteAttrById(attrId);
        return Result.ok();
    }

    /**
     * 使用页面：src/components/attr/BindAttr.vue --> getAttrByAttrGroupId -->/api/attr/getAttrByAttrGroupId/attrGroupId
     * 根据attrGroupId获取已经绑定的Attr基础属性(Attr.type='base')
     *
     * @param attrGroupId :分组id
     * @return ：基础属性的集合
     */
    @GetMapping("getAttrByAttrGroupId/{attrGroupId}")
    public Result<Object> getAttrByAttrGroupId(@PathVariable("attrGroupId") String attrGroupId) {
        List<Attr> attrList = attrService.getAttrsByAttrGroupId(attrGroupId);
        return Result.ok(attrList);
    }

    /**
     * 使用页面：getAttrsByAttrGroupId --> getNoAttrRelation --> /api/attr/getNoAttrRelation/attrGroupId
     * 获取本分类下没有关联其他分组关联的属性
     *
     * @param attrGroupId 属性分组的id
     * @param basePageDTO 所在分类信息
     * @return :查询结果
     */
    @PostMapping("/getNoAttrRelation/{attrGroupId}")
    public Result<Object> attrNoRelation(@PathVariable("attrGroupId") String attrGroupId, @RequestBody BasePageDTO basePageDTO) {
        Page<Attr> attrPage = attrService.getNoRelationAttrs(attrGroupId, basePageDTO);
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


    //mallproduct/attr/base/listforspu/7
    @GetMapping("baseAttrList/{spuId}")
    public Result<Object> baseAttrList(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValue> entities = productAttrValueService.baseAttrListForSpu(spuId);
        return Result.ok(entities);
    }

    @PostMapping("/update/{spuId}")
    public Result<Object> updateSpuAtt(@PathVariable String spuId, @RequestBody List<ProductAttrValue> list) {
        productAttrValueService.updateSpuAttr(spuId, list);
        return Result.ok();
    }
}
