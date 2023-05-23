package com.wang.reggir.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.wang.reggir.common.R;
import com.wang.reggir.pojo.User;
import com.wang.reggir.service.UserService;
import com.wang.reggir.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @ResponseBody
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        if (StringUtils.isNotBlank(user.getPhone())) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码为：{}", code);
            //这里应该将导入阿里云的依赖，然后用工具类输入标签、模板、手机号和随机验证码发送短信
            //这里模拟这个过程直接打印生成的验证码通过控制台获得
            session.setAttribute(user.getPhone(), code);
            return R.success("发送成功");
        }
        return R.error("发送失败");
    }

    @ResponseBody
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        String phone = (String) map.get("phone");
        String code = (String) map.get("code");
        String codeCompartion = (String) session.getAttribute(phone);
        if (codeCompartion == null || !codeCompartion.equals(code)) {
            return R.error("验证码输入错误");
        }
        //判断该用户是否在数据库中，若不在则再数据库中创建相关数据
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userService.getOne(wrapper);
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
        }
        //用户信息存session
        session.setAttribute("user", user.getId());
        //清除一下可能存在的employee信息
        // 后台登录完成后没点退出直接进入到移动端的登录页面登录会出现在session中同时存在employee和user的情况
        // 此时由于employee的判断在前，会出现明明是在操作user移动端，但ThreadLocal中存的却是刚刚登录的employee的ID的情况
        // 因此要在存储用户信息到session后，删除可能存在的employee信息
        // 同理在后台employee登录时也要进行相同处理，删除可能存在的user信息
        if(session.getAttribute("employee") != null){
            session.removeAttribute("employee");
        }
        return R.success(user);
    }

    @ResponseBody
    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出成功");
    }


}
