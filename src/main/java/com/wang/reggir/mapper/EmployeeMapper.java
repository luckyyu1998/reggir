package com.wang.reggir.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wang.reggir.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
