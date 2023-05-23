package com.wang.reggir.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.reggir.common.R;
import com.wang.reggir.pojo.Employee;
import com.wang.reggir.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public R<Employee> login(@RequestBody Employee employee, HttpSession session) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if(emp == null){
            return R.error("登录失败用户名或密码错误");
        }
        if(!password.equals(emp.getPassword())){
            return R.error("登录失败用户名或密码错误");
        }
        if(emp.getStatus() != 1){
            return R.error("账号已禁用");
        }

        session.setAttribute("employee",emp.getId());
        if(session.getAttribute("user") != null){
            session.removeAttribute("user");
        }
        return R.<Employee>success(emp);
    }

    @ResponseBody
    @PostMapping("/logout")
    public R<Employee> logout(HttpSession session){
        session.removeAttribute("employee");
        return R.success(null);
    }

    @ResponseBody
    @PostMapping
    public R<Employee> addEmployee(@RequestBody Employee employee, HttpSession session){
        log.info("新增员工：{}",employee.toString());
        //设置初始密码123456，并进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateUser((Long)session.getAttribute("employee"));
        employee.setUpdateUser((Long)session.getAttribute("employee"));
        employeeService.save(employee);
        return R.success(null);
    }

    @ResponseBody
    @GetMapping("/page")
    public R<Page> pageshow(int page, int pageSize, String name){
        log.info("page={}.pageSize={},name={}",page,pageSize,name);
        //分页构造器
        Page pageInfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @ResponseBody
    @PutMapping
    public R<String> update(@RequestBody Employee employee,HttpSession session){
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getId,employee.getId());
        employee.setUpdateTime(LocalDateTime.now());
        Long updateUserId = (Long)session.getAttribute("employee");
        employee.setUpdateUser(updateUserId);
        employeeService.update(employee,wrapper);
        return R.success(null);
    }

    @ResponseBody
    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable("id") Long id){
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("查询无对应员工");

    }
}
