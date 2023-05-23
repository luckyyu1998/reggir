package com.wang.reggir.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.reggir.mapper.AddressBookMapper;
import com.wang.reggir.pojo.AddressBook;
import com.wang.reggir.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
