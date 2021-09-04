package com.javaspring.myproject.controller;


import com.javaspring.myproject.beans.Result;
import com.javaspring.myproject.beans.User;
import com.javaspring.myproject.beans.UserVo;
import com.javaspring.myproject.dao.impl.ResultFactory;
import com.javaspring.myproject.service.IEMailService;
import com.javaspring.myproject.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@EnableAutoConfiguration
@RestController
public class MainController {


    //绑定文件上传路径到uploadPath
    @Value("${web.upload-path}")
    private String uploadPath;

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
    public User getUser(String email){
        User user = userService.getUser(new User(email));
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
            user.setUsername((userService.getUser(new User(user.getEmail()))).getUsername());
        }
        return ResultFactory.buildSuccessResult("登陆成功。欢迎您，亲爱的"+user.getUsername()+"用户！");
    }

    /*
    * 使用HttpSession在服务器与浏览器建立对话，以验证邮箱验证码
    * */
    @CrossOrigin
    @PostMapping(value = "/api/sendEmail")
    @ResponseBody
    public Result sendEmail(@Valid @RequestBody User user ,HttpSession httpSession ) {

        if (!EMailService.sendMimeMail(user.getEmail(), httpSession)) {
            String message = String.format("发送失败！邮箱已注册或不可用");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("已发送验证码至邮箱！");
    }

    @CrossOrigin
    @PostMapping(value = "/api/regist")
    @ResponseBody
    public Result regist(@Valid @RequestBody UserVo userVo) {

        if (!EMailService.registered(userVo)) {
            String message = String.format("注册失败！验证码不一致");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("注册成功！");
    }



    /*
    *
    * 上传文件
    * */

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");


    @CrossOrigin
    @PostMapping(value ="/api/upload")
    public String upload(@RequestParam("file") MultipartFile[] files,String username,HttpServletRequest request) {



        // 在 uploadPath 文件夹中通过日期对上传的文件归类保存
        // 比如：/2019/06/06/cf13891e-4b95-4000-81eb-b6d70ae44930.png
        String format = sdf.format(new Date());
        File folder = new File(uploadPath + username+"/"+format);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }


        String fileName = null;
        String msg = "";
        if (files != null && files.length >0) {
            for(int i =0 ;i< files.length; i++){
                try {
                    fileName = files[i].getOriginalFilename();
                    byte[] bytes = files[i].getBytes();
                    BufferedOutputStream buffStream =
                            new BufferedOutputStream(new FileOutputStream(new File(folder +"/"+ fileName)));
                    buffStream.write(bytes);
                    buffStream.close();
                    // 返回上传文件的访问路径
                    String filePath = request.getScheme() + "://" + request.getServerName()
                            + ":" + request.getServerPort()  +"/"+ username+"/"+format + fileName;

                    return "Dear "+username+",you have successfully uploaded!url: "+filePath;
                } catch (Exception e) {
                    return "You failed to upload " + fileName + ": " + e.getMessage();
                }
            }
            return msg;
        } else {
            return "Unable to upload. File is empty.";
        }
    }

}
