package com.stonebridge.tradeflow.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stonebridge.tradeflow.business.entity.BasePageDTO;
import com.stonebridge.tradeflow.business.entity.attribute.Attr;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrAttrgroupRelationDto;
import com.stonebridge.tradeflow.business.entity.category.Category;
import com.stonebridge.tradeflow.business.service.AttrGroupService;
import com.stonebridge.tradeflow.common.cache.MyRedisCache;
import com.stonebridge.tradeflow.business.entity.attribute.AttrAttrgroupRelation;
import com.stonebridge.tradeflow.business.entity.attribute.AttrGroup;
import com.stonebridge.tradeflow.business.entity.attribute.dto.AttrDTO;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrRespVo;
import com.stonebridge.tradeflow.business.entity.attribute.vo.AttrVo;
import com.stonebridge.tradeflow.business.mapper.AttrAttrGroupRelationMapper;
import com.stonebridge.tradeflow.business.mapper.AttrGroupMapper;
import com.stonebridge.tradeflow.business.mapper.AttrMapper;
import com.stonebridge.tradeflow.business.service.AttrService;
import com.stonebridge.tradeflow.common.constant.Constant;
import com.stonebridge.tradeflow.common.utils.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;

@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {

    private final JdbcTemplate jdbcTemplate;
    private final AttrAttrGroupRelationMapper attrAttrGroupRelationMapper;

    private final AttrGroupMapper attrGroupMapper;

    private final AttrGroupService attrGroupService;

    private final MyRedisCache myRedisCache;

    @Autowired
    public AttrServiceImpl(AttrGroupService attrGroupService, AttrGroupMapper attrGroupMapper, MyRedisCache myRedisCache, @Qualifier("businessJdbcTemplate") JdbcTemplate jdbcTemplate, AttrAttrGroupRelationMapper attrAttrGroupRelationMapper) {
        this.attrGroupService = attrGroupService;
        this.attrGroupMapper = attrGroupMapper;
        this.myRedisCache = myRedisCache;
        this.jdbcTemplate = jdbcTemplate;
        this.attrAttrGroupRelationMapper = attrAttrGroupRelationMapper;
    }

    @Override
    public Page<AttrRespVo> queryPage(AttrDTO attrDTO) {
        String type = StringUtil.trim(attrDTO.getType());
        //当前页面
        String pageNumber = StringUtil.trim(attrDTO.getPage());
        //每页数据数量
        String limit = StringUtil.trim(attrDTO.getLimit());
        //和attr_name自动进行模糊匹配的关键词
        String keyWord = StringUtil.trim(attrDTO.getKeyword());
        //分类的id，对应attr.category_id
        String categoryId = StringUtil.trim(attrDTO.getCategoryId());
        // 参数校验和转换
        int pageNum = 1;
        int size = 10;

        try {
            if (StringUtil.isNotBlank(pageNumber)) {
                pageNum = Integer.parseInt(pageNumber);
            }
            if (StringUtil.isNotBlank(limit)) {
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
        if (StringUtil.isNotBlank(categoryId)) {
            queryWrapper.eq("category_id", categoryId);
        }

        // 添加关键词模糊查询条件
        if (!StringUtil.isEmpty(keyWord)) {
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
            attrAttrGroupRelationMapper.insert(attrgroupRelation);
        }
    }


    @Override
    public AttrRespVo getAttrInfo(String attrId) {
        AttrRespVo respVo = new AttrRespVo();
        Attr Attr = this.getById(attrId);
        BeanUtils.copyProperties(Attr, respVo);

        if (Attr.getAttrType() == Constant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //1、设置分组信息
            AttrAttrgroupRelation attrgroupRelation = attrAttrGroupRelationMapper.selectOne(new QueryWrapper<AttrAttrgroupRelation>().eq("attr_id", attrId));
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

            Integer count = Math.toIntExact(attrAttrGroupRelationMapper.selectCount(new QueryWrapper<AttrAttrgroupRelation>().eq("attr_id", attr.getAttrId())));
            if (count > 0) {
                attrAttrGroupRelationMapper.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelation>().eq("attr_id", attr.getAttrId()));
            } else {
                attrAttrGroupRelationMapper.insert(relationEntity);
            }
        }
    }

    @Transactional
    @Override
    public void deleteAttrById(String attrId) {
        // 删除属性与分组的关联关系
        attrAttrGroupRelationMapper.delete(new QueryWrapper<AttrAttrgroupRelation>().eq("attr_id", attrId));
        // 删除属性本身
        this.removeById(attrId);
    }

    /**
     * 根据attrGroupId先关联表pms_attr_attrgroup_relation查询已经关联的Attr表id数据，再根据id查询出所有的Attr数据
     *
     * @param attrGroupId :属性分组表的id
     * @return :已经关联了attrGroupId的基础属性数据
     */
    @Override
    public List<Attr> getAttrByAttrGoupId(String attrGroupId) {
        if (StringUtil.isBlank(attrGroupId)) {
            return Collections.emptyList();
        }
        QueryWrapper<AttrAttrgroupRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_group_id", attrGroupId);
        List<AttrAttrgroupRelation> relations = attrAttrGroupRelationMapper.selectList(queryWrapper);
        if (relations == null || relations.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> attrIds = relations.stream()
                .map(AttrAttrgroupRelation::getAttrId)
                .filter(StringUtil::isNotBlank)
                .collect(Collectors.toList());
        if (attrIds.isEmpty()) {
            return Collections.emptyList();
        }
        return this.listByIds(attrIds);
    }

    /**
     * 根据AttrGroupRelationDto里面的attrGroupId和attrId匹配pms_attr_attrgroup_relation表里的attr_id和attr_group_id字段完成批量删除
     *
     * @param attrAttrgroupRelationDtos ：AttrAttrgroupRelation匹配积极而
     */
    @Override
    public void deleteRelation(List<AttrAttrgroupRelationDto> attrAttrgroupRelationDtos) {
        if (attrAttrgroupRelationDtos == null || attrAttrgroupRelationDtos.isEmpty()) {
            return;
        }
        QueryWrapper<AttrAttrgroupRelation> queryWrapper = new QueryWrapper<>();
        for (AttrAttrgroupRelationDto dto : attrAttrgroupRelationDtos) {
            queryWrapper.eq("attr_id", dto.getAttrId()).eq("attr_group_id", dto.getAttrGroupId());
            attrAttrGroupRelationMapper.delete(queryWrapper);
        }
    }


    /**
     * 要求：1.当前分组只能关联自己所属分类里面的所有属性（即pms_attr.category_id==当前分类的id）
     * 2.当前分组只能关联别的分组(别的分组也是category_id相同的分组)没有引用的属性
     *
     * @param attrGroupId 当前分组的id
     * @param basePageDTO 分页基础数据
     */
    public Page<Attr> getNoRelationAttr(String attrGroupId, BasePageDTO basePageDTO) {
        //1.根据attrgroupId获取对应<属性分组pms_attr_group>表中的数据，再获取分类主键pms_attr_group.category_id（pms_category的主键）
        //1.1.根据attrgroupId查询当前AttrGroup的数据
        AttrGroup attrGroup = attrGroupService.getById(attrGroupId);
        //1.2.根据当前AttrGroup的数据获取当前分组数据的分类主键category_id
        String categoryId = attrGroup.getCategoryId();

        //2.根据当前分组数据的category_id，获取当前分类下的所有分组AttrGroup数据集合
        //2.1.获取当前分类下的所有分组AttrGroup数据集合
        QueryWrapper<AttrGroup> attrGroupQueryWrapper = new QueryWrapper<>();
        attrGroupQueryWrapper.eq("category_id", categoryId);
        List<AttrGroup> attrGroupList = attrGroupService.list(attrGroupQueryWrapper);
        //2.2.收集当前分类下的所有<属性分组表>数据的属性分组id，即属性分组关联表（pms_attr_attrgroup_relation）表的attr_group_id属性
        List<String> attrGroupIdList = attrGroupList.stream().map(AttrGroup::getAttrGroupId).collect(Collectors.toList());
        //2.3.查询分组关联表（pms_attr_attrgroup_relation）表的attr_group_id属性为attrGroupIdList的所有数据
        QueryWrapper<AttrAttrgroupRelation> attrgroupRelationQueryWrapper = new QueryWrapper<>();
        attrgroupRelationQueryWrapper.in("attr_group_id", attrGroupIdList);
        List<AttrAttrgroupRelation> attrAttrgroupRelations = attrAttrGroupRelationMapper.selectList(attrgroupRelationQueryWrapper);
        //2.4.根据<属性&属性分组关联表>的数据获取当前分类下所有的属性id（主键）
        List<String> attrIds = attrAttrgroupRelations.stream().map(AttrAttrgroupRelation::getAttrId).collect(Collectors.toList());
        //2.5.获取<当前分类category_id> && <移除已经绑定了关联属性分组的属性> 所有属性
        QueryWrapper<Attr> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id", categoryId);
        queryWrapper.eq("attr_type", Constant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!attrIds.isEmpty()) {
            queryWrapper.notIn("attr_id", attrIds);
        }
        //2.6.如果有关键字查询，加上关键字模糊查询
        String keyWord = StringUtil.trim(basePageDTO.getKeyword());
        if (!StringUtil.isEmpty(keyWord)) {
            queryWrapper.and((w) -> {
                w.eq("attr_id", keyWord).or().like("attr_name", keyWord);
            });
        }

        //3.分页参数处理
        int pageNum = 1;
        int size = 10;
        try {
            if (StringUtil.isNotBlank(basePageDTO.getPage())) {
                pageNum = Integer.parseInt(basePageDTO.getPage());
            }
            if (StringUtil.isNotBlank(basePageDTO.getLimit())) {
                size = Integer.parseInt(basePageDTO.getLimit());
            }
        } catch (NumberFormatException e) {
            // ignore, use default
        }
        if (pageNum < 1) pageNum = 1;
        if (size < 1) size = 10;
        if (size > 100) size = 100;
        Page<Attr> page = new Page<>(pageNum, size);
        queryWrapper.orderByAsc("attr_id");
        //5.分页查询
        return this.page(page, queryWrapper);
    }

    /**
     * 根据条件查询pms_attr表中数据
     *
     * @param params    ：参数
     * @param catelogId ：分类的id（pms_category.id）
     * @return ：数据集
     */
    @Override
    public Page<AttrRespVo> queryBaseAttrPage(Map<String, Object> params, String catelogId, String attrType) {
        QueryWrapper<Attr> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_type", "base".equalsIgnoreCase(attrType) ? Constant.AttrEnum.ATTR_TYPE_BASE.getCode() : Constant.AttrEnum.ATTR_TYPE_SALE.getCode());
        //1.如果分组id为0,则表示分组id不作为条件查询所有的属性
        if (StringUtil.isNotBlank(catelogId)) {
            queryWrapper.eq("category_id", catelogId);
        }
        //2.如果用户输入关键字作为查询条件，则进行模糊匹配查询。同时匹配attr_id和attr_name
        String key = StringUtil.trim(params.get("key"));
        if (!StringUtil.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        //3.从pms_attr查询数据后，前台除了显示pms_attr数据外还需要显示；
        // - 关联pms_attr的pms_category的名字，他们通过中间表pms_attr_attrgroup_relation关联
        // - 关联pms_category的分类名称pms_attr.category_id。关联查询即可
        // 3.1.查询出pms_attr的数据
        String sql = "selcect name FROM pms_category WHERE ID=?";
        // 修复分页参数
        long current = Long.parseLong(params.getOrDefault("page", "1").toString());
        long size = Long.parseLong(params.getOrDefault("limit", "500").toString());
        Page<Attr> page = new Page<>(current, size);
        Page<Attr> attrPage = this.page(page, queryWrapper);
        List<Attr> attrList = attrPage.getRecords();
        // 3.2.遍历查询出pms_attr的数
        List<AttrRespVo> attrRespVoList = attrList.stream().map((attr -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attr, attrRespVo);
            // 3.3.查询出关联pms_attr的pms_category的名字，他们通过中间表pms_attr_attrgroup_relation关联
            // 仅为规格属性为base时查询关联属性组表
            if ("base".equalsIgnoreCase(attrType)) {
                QueryWrapper<AttrAttrgroupRelation> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("attr_id", attr.getAttrId());
                AttrAttrgroupRelation attrgroupRelation = attrAttrGroupRelationMapper.selectOne(queryWrapper1);
                if (attrgroupRelation != null && attrgroupRelation.getAttrGroupId() != null) {
                    AttrGroup attrGroup = attrGroupMapper.selectById(StringUtil.trim(attrgroupRelation.getAttrGroupId()));
                    attrRespVo.setGroupName(attrGroup.getAttrGroupName());
                }
            }
            // 3.4.关联pms_category的分类名称，根据pms_attr.category_id关联查询即可
            String categoryName = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getString("name"), attr.getCategoryId());
            if (StringUtil.isNotEmpty(categoryName)) {
                attrRespVo.setCategoryName(categoryName);
            }
            return attrRespVo;
        })).collect(Collectors.toList());
        Page<AttrRespVo> attrRespVoPage = new Page<>(attrPage.getTotal(), attrPage.getTotal(), attrPage.getPages());
        attrRespVoPage.setRecords(attrRespVoList);
        return attrRespVoPage;
    }
}