package com.stonebridge.tradeflow.business.controller;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrDTO;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrRespVo;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrVo;
import com.stonebridge.tradeflow.business.entity.product.ProductAttrValue;
import com.stonebridge.tradeflow.business.service.AttrService;
import com.stonebridge.tradeflow.business.service.ProductAttrValueService;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attr")
public class AttrController {
    private final AttrService attrService;

    private final ProductAttrValueService productAttrValueService;

    @Autowired
    public AttrController(AttrService attrService, ProductAttrValueService productAttrValueService) {
        this.attrService = attrService;
        this.productAttrValueService = productAttrValueService;
    }

    /**
     * 根据attrId获取pms_attr表对应的所有信息，以及关联的pms_attr_attrgroup_relation表里面的信息
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
     * @param attrDTO ：查询条件 ：关键字，分页信息等
     * @return ：符合条件的pms_attr表对应的所有信息，以及关联的pms_attr_attrgroup_relation表里面的信息封装的分页列表
     */
    @PostMapping("/list")
    public Result<Object> list(@RequestBody AttrDTO attrDTO) {
        Page<AttrRespVo> attrGroupPage = attrService.queryPage(attrDTO);
        return Result.ok(attrGroupPage);
    }

    /**
     * 保存信息到pms_attr表，以及关联表pms_attr_attrgroup_relation
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
     * @param attrId attr.attrId
     * @return 删除成功
     */
    @DeleteMapping("/delete")
    public Result<Object> delete(@PathVariable("attrId") String attrId) {
        attrService.deleteAttrById(attrId);
        return Result.ok();
    }
}
