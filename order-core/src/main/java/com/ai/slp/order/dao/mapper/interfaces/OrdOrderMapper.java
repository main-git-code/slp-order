package com.ai.slp.order.dao.mapper.interfaces;

import com.ai.slp.order.dao.mapper.bo.OrdOrder;
import com.ai.slp.order.dao.mapper.bo.OrdOrderCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrdOrderMapper {
    int countByExample(OrdOrderCriteria example);

    int deleteByExample(OrdOrderCriteria example);

    int insert(OrdOrder record);

    int insertSelective(OrdOrder record);

    List<OrdOrder> selectByExample(OrdOrderCriteria example);

    int updateByExampleSelective(@Param("record") OrdOrder record, @Param("example") OrdOrderCriteria example);

    int updateByExample(@Param("record") OrdOrder record, @Param("example") OrdOrderCriteria example);
}