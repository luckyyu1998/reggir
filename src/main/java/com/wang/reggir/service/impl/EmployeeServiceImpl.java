package com.wang.reggir.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.reggir.mapper.EmployeeMapper;
import com.wang.reggir.pojo.Employee;
import com.wang.reggir.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
