package com.javaspring.myproject.controller;


import com.javaspring.myproject.beans.Result;
import com.javaspring.myproject.beans.User;
import com.javaspring.myproject.beans.UserChangePW;
import com.javaspring.myproject.beans.UserVo;
import com.javaspring.myproject.dao.impl.ResultFactory;
import com.javaspring.myproject.service.IEMailService;
import com.javaspring.myproject.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
@EnableAutoConfiguration
@RestController
public class MainController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IEMailService EMailService;

    @RequestMapping("/home")
    @ModelAttribute
    public String home(String username,String password,Model model){
        model.addAttribute(
                "username",
                username);
        return "home";
    }

    @RequestMapping("/user/addUser")
    public String addUser(String name,String password){
        User user = new User(name,password);
        userService.addUser(user);
        return "add successfully";
    }
    @RequestMapping("/user/getUser")
    public User getUser(String name){
        User user = userService.getUser(name);
        return user;
    }
    @RequestMapping("/user/deleteUser")
    public String deleteUser(String name,String password){
        User user = new User(name,password);
        userService.deleteUser(user);
        return "delete successfully";
    }
    @RequestMapping("/login")
    @ModelAttribute
    public String login() {
        return "login";
    }

    /**
     * 登录控制器，前后端分离用的不同协议和端口，所以需要加入@CrossOrigin支持跨域。
     * 给VueLoginInfoVo对象加入@Valid注解，并在参数中加入BindingResult来获取错误信息。
     * 在逻辑处理中我们判断BindingResult知否含有错误信息，如果有错误信息，则直接返回错误信息。
     */

    @CrossOrigin
    @RequestMapping(value = "/api/login", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Result login(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (user.getPassword().equals("")||user.getUsername().equals("")) {
            String message = String.format("账号或密码不能为空！");
            return ResultFactory.buildFailResult(message);
        }
        if (bindingResult.hasErrors()) {
            String message = String.format("登陆失败，详细信息[%s]。", bindingResult.getFieldError().getDefaultMessage());
            return ResultFactory.buildFailResult(message);
        }
        if (!userService.JudgeByUserName(user)) {
            user.setEmail(user.getUsername());
            if (!userService.JudgeByEMail(user)) {
                String message = String.format("登陆失败，账号/密码信息不正确。");
                return ResultFactory.buildFailResult(message);
            }
        }
        return ResultFactory.buildSuccessResult("登陆成功。");
    }

    /*
    * 使用HttpSession在服务器与浏览器建立对话，以验证邮箱验证码
    * */
    @CrossOrigin
    @PostMapping(value = "/api/sendEmail")
    @ResponseBody
    public Result sendEmail(@Valid @RequestBody User user ,HttpSession httpSession ) {

        if (!EMailService.sendMimeMail(user.getEmail(), httpSession)) {
            String message = String.format("发送邮箱失败！");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("已发送验证码至邮箱！");
    }

    @CrossOrigin
    @PostMapping(value = "/api/regist")
    @ResponseBody
    public Result regist(@Valid @RequestBody UserVo userVo) {

        if (!EMailService.registered(userVo)) {
            String message = String.format("注册失败！");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("注册成功！");
    }

    @CrossOrigin
    @PostMapping(value = "/api/FindPassword")
    @ResponseBody
    public Result FoundPassWord(@Valid @RequestBody User user){
        if(!EMailService.FindPassword_sendEmail(user.getEmail())){
            String message=String.format("此邮箱非您注册时使用的邮箱，找回失败！");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("找回成功，密码已发送至您的邮箱！");
    }

    @CrossOrigin
    @PostMapping(value = "/api/ChangePassword")
    @ResponseBody
    public Result ChangePassword(@Valid @RequestBody UserChangePW userChangePW){
        if(!EMailService.ChangePassword(userChangePW.getEmail(),userChangePW.getOldPassword(),userChangePW.getNewPassword(),userChangePW.getNewPasswordRepeat())){
            String message=String.format("信息有误，修改失败！");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("修改成功，修改后密码已发送至您的邮箱，请确认！");
    }
}
