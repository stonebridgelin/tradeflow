package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stonebridge.tradeflow.business.entity.attribute.Attr;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import org.apache.commons.lang3.StringUtils;
import com.stonebridge.tradeflow.business.entity.attribute.AttrAttrgroupRelation;
import com.stonebridge.tradeflow.business.entity.attribute.AttrGroup;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrDTO;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrRespVo;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrVo;
import com.stonebridge.tradeflow.business.entity.category.Category;
import com.stonebridge.tradeflow.business.mapper.AttrAttrGroupRelationMapper;
import com.stonebridge.tradeflow.business.mapper.AttrGroupMapper;
import com.stonebridge.tradeflow.business.mapper.AttrMapper;
import com.stonebridge.tradeflow.business.mapper.CategoryMapper;
import com.stonebridge.tradeflow.business.service.AttrService;
import com.stonebridge.tradeflow.business.service.CategoryService;
import com.stonebridge.tradeflow.common.constant.Constant;
import com.stonebridge.tradeflow.common.result.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;

import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {

    private final JdbcTemplate jdbcTemplate;
    AttrAttrGroupRelationMapper relationMapper;

    AttrGroupMapper attrGroupMapper;

    CategoryMapper categoryMapper;

    CategoryService categoryService;

    MyRedisCache myRedisCache;

    @Autowired
    public AttrServiceImpl(AttrAttrGroupRelationMapper relationMapper, CategoryService categoryService, CategoryMapper categoryMapper, AttrGroupMapper attrGroupMapper, MyRedisCache myRedisCache,@Qualifier("businessJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.relationMapper = relationMapper;
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
        this.attrGroupMapper = attrGroupMapper;
        this.myRedisCache = myRedisCache;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Page<AttrRespVo> queryPage(AttrDTO attrDTO) {
        String type=StringUtils.trim(attrDTO.getType());
        //当前页面
        String pageNumber = StringUtils.trim(attrDTO.getPage());
        //每页数据数量
        String limit = StringUtils.trim(attrDTO.getLimit());
        //和attr_name自动进行模糊匹配的关键词
        String keyWord = StringUtils.trim(attrDTO.getKeyword());
        //分类的id，对应attr.category_id
        String categoryId = StringUtils.trim(attrDTO.getCategoryId());
        // 参数校验和转换
        int pageNum = 1;
        int size = 10;

        try {
            if (StringUtils.isNotBlank(pageNumber)) {
                pageNum = Integer.parseInt(pageNumber);
            }
            if (StringUtils.isNotBlank(limit)) {
                size = Integer.parseInt(limit);
            }
        } catch (NumberFormatException e) {
            // 如果转换失败，使用默认值
        }

        // 确保页码和页大小在合理范围内
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (size < 1) {
            size = 10;
        }
        if (size > 100) {
            size = 100;
        }

        // 构建分页对象
        Page<Attr> page = new Page<>(pageNum, size);

        QueryWrapper<Attr> queryWrapper = new QueryWrapper<Attr>().eq("attr_type", "base".equalsIgnoreCase(type) ? Constant.AttrEnum.ATTR_TYPE_BASE.getCode() : Constant.AttrEnum.ATTR_TYPE_SALE.getCode());

        // 添加分类ID条件
        if (StringUtils.isNotBlank(categoryId)) {
            queryWrapper.eq("category_id",categoryId);
        }

        // 添加关键词模糊查询条件
        if (!StringUtils.isEmpty(keyWord)) {
            //attr_id  attr_name
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", keyWord).or().like("attr_name", keyWord);
            });
        }

        // 添加排序条件，按attr_id升序排列
        queryWrapper.orderByAsc("attr_id");

        // 执行分页查询,查询结果为：attrPage
        Page<Attr> attrPage = this.page(page, queryWrapper);
        Page<AttrRespVo> attrRespVoPage = new Page<>();
        BeanUtils.copyProperties(attrPage, attrRespVoPage);

        //将attrPage的records数据拿出来，将CategoryId转化为CategoryName保存到AttrPageVO对象，返回到前端
        List<Attr> records = attrPage.getRecords();
        List<AttrRespVo> attrRespVoList = new ArrayList<>();
        for (Attr attr : records) {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attr, attrRespVo);

            //设置分组名字
            if ("base".equalsIgnoreCase(type)) {
                String sql = "select attr_group_id from pms_attr_attrgroup_relation where attr_id=?";
                List<String> results = jdbcTemplate.query(sql,
                        (rs, rowNum) -> rs.getString("attr_group_id"),
                        attr.getAttrId());

                if (!results.isEmpty()) {
                    String attrGroupId = results.get(0);
                    AttrGroup attrGroup = attrGroupMapper.selectById(attrGroupId);
                    if (attrGroup != null) {
                        attrRespVo.setGroupName(attrGroup.getAttrGroupName());
                    }
                }
            }
            //设置分类的名字
            attrRespVo.setCategoryName(myRedisCache.getCategoryNameById(attr.getCategoryId()));
            attrRespVoList.add(attrRespVo);
        }
        attrRespVoPage.setRecords(attrRespVoList);
        return attrRespVoPage;
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        Attr attrObject = new Attr();
//        Attr.setAttrName(attr.getAttrName());
        BeanUtils.copyProperties(attr, attrObject);
        //1、保存基本数据
        attrObject.setAttrId(null);
        this.save(attrObject);
        //2、保存关联关系
        if (attr.getAttrType() == Constant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelation attrgroupRelation = new AttrAttrgroupRelation();
            attrgroupRelation.setAttrGroupId(String.valueOf(attr.getAttrGroupId()));
            attrgroupRelation.setAttrId(attrObject.getAttrId());
            relationMapper.insert(attrgroupRelation);
        }
    }


    @Override
    public AttrRespVo getAttrInfo(String attrId) {
        AttrRespVo respVo = new AttrRespVo();
        Attr Attr = this.getById(attrId);
        BeanUtils.copyProperties(Attr, respVo);

        if (Attr.getAttrType() == Constant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //1、设置分组信息
            AttrAttrgroupRelation attrgroupRelation = relationMapper.selectOne(new QueryWrapper<AttrAttrgroupRelation>().eq("attr_id", attrId));
            if (attrgroupRelation != null) {
                respVo.setAttrGroupId(Long.valueOf(attrgroupRelation.getAttrGroupId()));
                AttrGroup attrGroupEntity = attrGroupMapper.selectById(attrgroupRelation.getAttrGroupId());
                if (attrGroupEntity != null) {
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }


        //2、设置分类信息
        String catelogId = Attr.getCategoryId();
        respVo.setCategoryName(myRedisCache.getCategoryNameById(catelogId));
        return respVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        Attr Attr = new Attr();
        BeanUtils.copyProperties(attr, Attr);
        this.updateById(Attr);

        if (Attr.getAttrType() == Constant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //1、修改分组关联
            AttrAttrgroupRelation relationEntity = new AttrAttrgroupRelation();

            relationEntity.setAttrGroupId(String.valueOf(attr.getAttrGroupId()));
            relationEntity.setAttrId(attr.getAttrId());

            Integer count = Math.toIntExact(relationMapper.selectCount(new QueryWrapper<AttrAttrgroupRelation>().eq("attr_id", attr.getAttrId())));
            if (count > 0) {
                relationMapper.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelation>().eq("attr_id", attr.getAttrId()));
            } else {
                relationMapper.insert(relationEntity);
            }
        }


    }

    @Transactional
    @Override
    public void deleteAttrById(String attrId) {
        // 删除属性与分组的关联关系
        relationMapper.delete(new QueryWrapper<AttrAttrgroupRelation>().eq("attr_id", attrId));
        // 删除属性本身
        this.removeById(attrId);
    }
}