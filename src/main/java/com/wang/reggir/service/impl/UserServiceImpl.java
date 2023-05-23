package com.wang.reggir.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.reggir.mapper.UserMapper;
import com.wang.reggir.pojo.User;
import com.wang.reggir.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
