package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.dao.ProductAttrValueDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.vo.AttrGroupVO;
import com.atguigu.gmall.pms.vo.GroupVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Autowired
    private ProductAttrValueDao productAttrValueDao;

    @Autowired
    private AttrDao attrDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryByCidPage(Long catId, QueryCondition condition) {

        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();

        // 判断cateId是否为空
        if (catId != null) {
            wrapper.eq("catelog_id", catId);
        }

        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(condition),
                wrapper
        );

        return new PageVo(page);
    }

    @Override
    public AttrGroupVO queryGroupWithAttrs(Long gid) {

        AttrGroupVO groupVO = new AttrGroupVO();

        // 先查询分组
        AttrGroupEntity groupEntity = this.getById(gid);
        BeanUtils.copyProperties(groupEntity, groupVO);

        // 根据分组id查询关联关系
        List<AttrAttrgroupRelationEntity> relationEntities = this.relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", gid));
        if (CollectionUtils.isEmpty(relationEntities)){
            return groupVO;
        }
        groupVO.setRelations(relationEntities);

        // 根据关联关系的attrId查询属性
        List<Long> attrIds = relationEntities.stream().map(relation -> relation.getAttrId()).collect(Collectors.toList());
        List<AttrEntity> attrEntities = this.attrDao.selectBatchIds(attrIds);
        groupVO.setAttrEntities(attrEntities);

        return groupVO;
    }

    @Override
    public List<AttrGroupVO> queryGroupWithAttrsByCid(Long catId) {

        // 根据分类查询分类下的所有组
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));

        // 查询每个组下的所有规格参数
        return groupEntities.stream().map(attrGroupEntity -> this.queryGroupWithAttrs(attrGroupEntity.getAttrGroupId())).collect(Collectors.toList());
    }

    @Override
    public List<GroupVO> queryGroupVOByCid(Long cid, Long spuId) {
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cid));
        if (CollectionUtils.isEmpty(attrGroupEntities)){
            return null;
        }
        return attrGroupEntities.stream().map(attrGroupEntity -> {
            GroupVO groupVO = new GroupVO();
            groupVO.setGroupName(attrGroupEntity.getAttrGroupName());
            List<ProductAttrValueEntity> productAttrValueEntities = this.productAttrValueDao.queryByGidAndSpuId(spuId, attrGroupEntity.getAttrGroupId());
            groupVO.setBaseAttrValues(productAttrValueEntities);
            return groupVO;
        }).collect(Collectors.toList());
    }

}