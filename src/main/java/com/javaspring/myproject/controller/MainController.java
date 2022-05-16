package com.javaspring.myproject.controller;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import com.alibaba.fastjson.JSON;
import com.javaspring.myproject.beans.*;
import com.javaspring.myproject.beans.Record;
import com.javaspring.myproject.dao.IRecordDao;
import com.javaspring.myproject.dao.impl.ResultFactory;
import com.javaspring.myproject.service.*;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Integer.parseInt;
import static org.hibernate.loader.internal.AliasConstantsHelper.get;

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
    @Autowired
    private IBlogService blogService;
    @Autowired
    private IFileService fileService;
    @Autowired
    private IUserLikeService userLikeService;
    @Autowired
    private IRecordService recordService;


    //测试接口--home

    @RequestMapping("/home")
    @ModelAttribute
    public String home(String username,String password,Model model){
        model.addAttribute(
                "username",
                username);
        return "home";
    }

    //测试接口--login
    @RequestMapping("/login")
    @ModelAttribute
    public String login() {
        return "login";
    }

    /*
     * 登陆
     * 路径 /api/login
     * 传参(json) username,password
     * 返回值(json--Result) code,message,data
     * */
    @CrossOrigin
    @RequestMapping(value = "/api/login", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Result login(@Valid @RequestBody UserVo userVo, BindingResult bindingResult) {
        if (userVo.getPassword().equals("")||userVo.getUsername().equals("")) {
            String message = String.format("账号或密码不能为空！");
            return ResultFactory.buildFailResult(message);
        }
        if (bindingResult.hasErrors()) {
            String message = String.format("登陆失败，详细信息[%s]。", bindingResult.getFieldError().getDefaultMessage());
            return ResultFactory.buildFailResult(message);
        }
        if (!userService.judgeByUserName(userVo)) {
            userVo.setEmail(userVo.getUsername());
            if (!userService.judgeByEMail(userVo)) {
                String message = String.format("登陆失败，账号/密码信息不正确。");
                return ResultFactory.buildFailResult(message);
            }
            userVo.setUsername((userService.getUserByEmail(new User(userVo.getEmail()).getEmail())).getUsername());
        }
        return ResultFactory.buildSuccessResult("登陆成功。欢迎您，亲爱的"+userVo.getUsername()+"用户！");
    }

    /*
     * 发送注册邮箱
     * 路径 /api/sendEmail
     * 传参(json) email
     * 返回值(json--Result) code,message,data
     * */
    @CrossOrigin
    @PostMapping(value = "/api/sendEmail")
    @ResponseBody
    public Result sendEmail(@Valid @RequestBody UserVo userVo ,HttpSession httpSession ) {
        /*
         * 使用HttpSession在服务器与浏览器建立对话，以验证邮箱验证码
         * */
        if (!EMailService.sendMimeMail(userVo.getEmail(), httpSession)) {
            String message = String.format("发送失败！邮箱已注册或不可用");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("已发送验证码至邮箱！");
    }



    /*
     * 注册新用户
     * 路径 /api/regist
     * 传参(json) username,password,email,code
     * 返回值(json--Result) code,message,data
     * */
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
     * 找回密码
     * 路径 /api/findPassword
     * 传参(json) email
     * 返回值(json--Result) code,message,data
     * */
    @CrossOrigin
    @PostMapping(value = "/api/findPassword")
    @ResponseBody
    public Result findPassWord(@Valid @RequestBody UserVo userVo){
        if(!EMailService.findPassword_sendEmail(userVo.getEmail())){
            String message=String.format("此邮箱非您注册时使用的邮箱,找回失败！");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("找回成功,密码已发送至您的邮箱！");
    }


    /*
     * 修改用户密码
     * 路径 /api/changePassword
     * 传参(json) email,Password,newPassword,newPasswordRepeat
     * 返回值(json--Result) code,message,data
     * */
    @CrossOrigin
    @PostMapping(value = "/api/changePassword")
    @ResponseBody
    public Result changePassword(@Valid @RequestBody UserVo userVo){
        if(!EMailService.changePassword(userVo.getEmail(),userVo.getPassword(),userVo.getNewPassword(),userVo.getNewPasswordRepeat())){
            String message=String.format("信息有误,修改失败！");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("修改成功,修改后密码已发送至您的邮箱，请确认！");
    }
    /*
     * 获取用户头像url
     * 路径 /api/getUserTouxiang
     * 传参(json):username/email
     * 返回值(String) url
     * 功能：登陆时加载用户头像
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getUserTouxiang")
    @ResponseBody
    public String getUserTouxiang(@Valid @RequestBody User user){
        return userService.getUserTouxiang(user.getUsername());
    }
    /*
     * 获取用户信息
     * 路径 /api/getUser
     * 传参(json):username/email
     * 返回值(json) 一个完整的User类实例
     * 功能：得到用户信息
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getUser")
    @ResponseBody
    public User getUser(@Valid @RequestBody User user) {
        return userService.getUser(user.getUsername());
    }
    /*
     * 修改用户信息
     * 路径 /api/updateUser
     * 传参(json) username(定位需要修改的人） #修改属性#(newUsername,newEmail,newTel等等）
     * 返回值 Result
     * */
    @CrossOrigin
    @PostMapping(value ="/api/updateUser")
    @ResponseBody
    public Result updateUser(@Valid @RequestBody UserVo userVo){
        if (!userService.updateUser(userVo)) {
            String message = String.format("更改个人信息失败！");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("已成功修个人信息！");
    }
  

    /*
     * 上传file
     * 路径 /api/upload
     * 传参(MultipartFile) file,username,(int) usage
     * 返回值(json) result{"success","fail"},url,filename
     * */
    @CrossOrigin
    @PostMapping(value ="/api/upload")
    public String upload(@RequestParam("file") MultipartFile[] files,String username,int usage,HttpServletRequest request) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH-mm-ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");

        Map<String,Object> map=new HashMap<>();
        // 在 uploadPath 文件夹中通过username和日期对上传的文件归类保存
        // 比如：2019211996/2021/09/04/15:23:46/xxx.jpg
        String format = sdf.format(new Date());
        String date_ = sdf1.format(new Date());
        java.io.File folder = new java.io.File(uploadPath + username+"/"+format+"/");
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }


        String fileName = null;
        String msg = "";
        if (files != null && files.length >0) {
            for(int i =0 ;i< files.length; i++){
                    fileName = files[i].getOriginalFilename();
                    byte[] bytes = files[i].getBytes();
                    BufferedOutputStream buffStream =
                            new BufferedOutputStream(new FileOutputStream(new java.io.File(folder +"/"+ fileName)));
                    buffStream.write(bytes);
                    buffStream.close();

                    String filePath = request.getScheme() + "://" + request.getServerName()
                            + ":" + request.getServerPort()  +"/"+ username+"/"+format +"/"+ fileName;
                    //将文件数据写进数据库
                    String size=fileService.getFileSize(files[i].getSize());

                    File file2=new File(fileName,username,filePath,date_, size);

                    fileService.insertFile(file2);
                    // 返回上传文件的访问路径

                    map.put("result","success");
                    map.put("url",filePath);
                    map.put("filename",fileName);
                    if(usage==1)
                    {
                        UserVo userVo = new UserVo();
                        userVo.setUsername(username);
                        userVo.setNewTouxiang(filePath);
                        userService.updateUser(userVo);
                    }
                    return JSON.toJSONString(map);
                }
        }
            map.put("result","fail");
            return JSON.toJSONString(map);
    }





    /*
     * 获取file
     * 路径 /api/getFile
     * 传参(json) username,filename,date_
     * 返回值(json) filename,username,url,date_,size_
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getFile")
    @ResponseBody
    public String getFile(@Valid @RequestBody File file) {

        Map<String,Object> map=new HashMap<>();
        File file1=fileService.getFile(file);
        if(file1==null){
            return null;
        }
        map.put("filename",file1.getFilename());
        map.put("username",file1.getUsername());
        map.put("url",file1.getUrl());
        map.put("date_",file1.getDate_());
        map.put("size_",file1.getSize_());

        return JSON.toJSONString(map);

    }



    /*
     * 获取files
     * 路径 /api/getAllFiles
     * 传参(json) username
     * 返回值(json--数组) filename,username,url,date_,size_
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getAllFiles")
    @ResponseBody
    public String getAllFiles(@Valid @RequestBody File file) {
        List<File> fileList=fileService.getAllFiles(file);
        if(fileList==null){
            return null;
        }
        List<Map<String,Object>> maplist=new ArrayList<>();
        for (int i = 0; i < fileList.size(); i++) {
            Map<String,Object> map=new HashMap<>();
            map.put("filename",fileList.get(i).getFilename());
            map.put("username",fileList.get(i).getUsername());
            map.put("url",fileList.get(i).getUrl());
            map.put("date_",fileList.get(i).getDate_());
            map.put("size_",fileList.get(i).getSize_());
            maplist.add(map);
        }

        return JSON.toJSONString(maplist);
    }



    /*
     * 删除file
     * 路径 /api/deleteFile
     * 传参(json) username,filename,date_
     * 返回值(String) result{"success","fail"}
     * */
    @CrossOrigin
    @PostMapping(value ="/api/deleteFile")
    @ResponseBody
    public String deleteFile(@Valid @RequestBody File file) {
        Map<String,Object> map=new HashMap<>();
        if(fileService.deleteFile(file)){
            map.put("result","success");
        }else{

            map.put("result","fail");
        }


    return JSON.toJSONString(map);

    }





    /*
     * 上传blog
     * 路径 /api/blogUpload
     * 传参(json) username,content,title,picture,visible
     * 返回值(string) blogid
     * */
    @CrossOrigin
    @PostMapping(value ="/api/blogUpload")
    @ResponseBody
    public String blogUpload(@Valid @RequestBody Blog blog) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        String format = sdf.format(new Date());
        blog.setBlogid(format+"-"+blog.getUsername());
        blog.setTime_(format);
        blogService.insertBlog(blog);

        return blog.getBlogid();
    }

   /*
     * 获取blog
     * 路径 /api/getBlog
     * 传参(json) blogid
     * 返回值(json) blogid,username,time_,title,content,picture,count,visible
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getBlog")
    @ResponseBody
    public String getBlog(@Valid @RequestBody Blog blog) {
        Blog blog1=blogService.getBlog(blog);
        if(blog1==null){
            return null;
        }
        Map<String,Object> map=new HashMap<>();

        map.put("blogid",blog1.getBlogid());
        map.put("username",blog1.getUsername());
        map.put("time_",blog1.getTime_());
        map.put("title",blog1.getTitle());
        map.put("content",blog1.getContent());
        map.put("picture",blog1.getPicture());
        map.put("count",blog1.getCount());
        map.put("visible",blog1.getVisible());
        return JSON.toJSONString(map);
    }


    /*
     * 获取blogs
     * 路径 /api/getAllBlogs
     * 传参(json) username
     * 返回值(json) blogid,username,time_,title,content,picture,count,visible
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getAllBlogs")
    @ResponseBody
    public String getAllBlogs(@Valid @RequestBody Blog blog) {
        List<Blog> bloglist=blogService.getAllBlogs(blog);
        if(bloglist==null){
            return null;
        }
        List<Map<String,Object>> maplist=new ArrayList<>();
        for (int i = 0; i < bloglist.size(); i++) {
            Map<String,Object> map=new HashMap<>();

            map.put("blogid",bloglist.get(i).getBlogid());
            map.put("username",bloglist.get(i).getUsername());
            map.put("time_",bloglist.get(i).getTime_());
            map.put("title",bloglist.get(i).getTitle());
            map.put("content",bloglist.get(i).getContent());
            map.put("picture",bloglist.get(i).getPicture());
            map.put("visiable",bloglist.get(i).getVisible());
            map.put("count",bloglist.get(i).getCount());
            maplist.add(map);
        }

        return JSON.toJSONString(maplist);
    }


    /*
     * 获取公开的所有人blogs
     * 路径 /api/getPublicBlogs
     * 传参   username
     * 返回值(json) blogid,username,time_,title,content,picture,count,isliked
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getPublicBlogs")
    @ResponseBody
    public String getPublicBlogs(@RequestBody Blog blog) {
        List<Blog> bloglist=blogService.getPublicBlogs();
        List<Map<String,Object>> maplist=new ArrayList<>();
        if(bloglist==null){
            return null;
        }
        for (int i = 0; i < bloglist.size(); i++) {
            Map<String,Object> map=new HashMap<>();
            Object object=userLikeService.find(new UserLike(bloglist.get(i).getBlogid(),blog.getUsername()));
            if(object!=null){
                map.put("isLiked","1");
            }else {
                map.put("isLiked","0");
            }
            map.put("touxiang",userService.getUserTouxiang(bloglist.get(i).getUsername()));
            map.put("blogid",bloglist.get(i).getBlogid());
            map.put("username",bloglist.get(i).getUsername());
            map.put("time_",bloglist.get(i).getTime_());
            map.put("title",bloglist.get(i).getTitle());
            map.put("content",bloglist.get(i).getContent());
            map.put("picture",bloglist.get(i).getPicture());
            map.put("count",bloglist.get(i).getCount());
            maplist.add(map);
        }

        return JSON.toJSONString(maplist);
    }
    /*
     * 以热度顺序获取blogs
     * 路径 /api/getAllHotBlogs
     * 传参(json):username
     * 返回值(json) blogid,username,time_,title,content,picture,count,visible,isliked
     * 功能：以热度从高到低的顺序浏览自己的博客
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getAllHotBlogs")
    @ResponseBody
    public String getAllHotBlogs(@Valid @RequestBody Blog blog) {
        List<Blog> bloglist=blogService.getAllHotBlogs(blog);
        List<Map<String,Object>> maplist=new ArrayList<>();
        if(bloglist==null){
            return null;
        }
        for (int i = 0; i < bloglist.size(); i++) {
            Map<String,Object> map=new HashMap<>();

            map.put("blogid",bloglist.get(i).getBlogid());
            map.put("username",bloglist.get(i).getUsername());
            map.put("time_",bloglist.get(i).getTime_());
            map.put("title",bloglist.get(i).getTitle());
            map.put("content",bloglist.get(i).getContent());
            map.put("picture",bloglist.get(i).getPicture());
            map.put("visiable",bloglist.get(i).getVisible());
            map.put("count",bloglist.get(i).getCount());
            maplist.add(map);
        }

        return JSON.toJSONString(maplist);

    }
    /*
     * 以热度顺序获取公开的blogs
     * 路径 /api/getPublicHotBlogs
     * 传参: username
     * 返回值(json) blogid,username,time_,title,content,picture,count,visible,isliked
     * 功能：以热度从高到低的顺序浏览公开的博客
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getPublicHotBlogs")
    @ResponseBody
    public String getPublicHotBlogs(@RequestBody Blog blog) {
        List<Blog> bloglist=blogService.getPublicHotBlogs();
        List<Map<String,Object>> maplist=new ArrayList<>();
        if(bloglist==null){
            return null;
        }
        for (int i = 0; i < bloglist.size(); i++) {
            Map<String,Object> map=new HashMap<>();
            if(userLikeService.find(new UserLike(bloglist.get(i).getBlogid(),blog.getUsername()))!=null){
                map.put("isLiked","1");
            }else {
                map.put("isLiked","0");
            }

            map.put("touxiang",userService.getUserTouxiang(bloglist.get(i).getUsername()));
            map.put("blogid",bloglist.get(i).getBlogid());
            map.put("username",bloglist.get(i).getUsername());
            map.put("time_",bloglist.get(i).getTime_());
            map.put("title",bloglist.get(i).getTitle());
            map.put("content",bloglist.get(i).getContent());
            map.put("picture",bloglist.get(i).getPicture());
            map.put("visiable",bloglist.get(i).getVisible());
            map.put("count",bloglist.get(i).getCount());
            maplist.add(map);
        }

        return JSON.toJSONString(maplist);
    }
    /*
     * 删除blog
     * 路径 /api/deleteBlog
     * 传参(json) blogid,username
     * 返回值(json)(String) result{"success","fail"}
     * */
    @CrossOrigin
    @PostMapping(value ="/api/deleteBlog")
    @ResponseBody
    public String deleteBlog(@Valid @RequestBody Blog blog) {
        Map<String, Object> map = new HashMap<>();
        if (blogService.deleteBlog(blog)) {
            map.put("result", "success");
        } else {

            map.put("result", "fail");
        }
        return JSON.toJSONString(map);
    }
  

    /*
     * 修改blog的内容
     * 路径 /api/updateBlog
     * 传参(json):blogid,username,time_,title,content,picture,visible
     * 返回值(String) Result{message,data}
     * 功能：进行判断，只能修改自己发布的blog，判断依据为username要与数据库相关blog中存储的username相同
     * */
    @CrossOrigin
    @PostMapping(value ="/api/updateBlog")
    @ResponseBody
    public Result updateBlog(@Valid @RequestBody Blog blog){
        Blog blog1=blogService.getBlog(blog);
        if(blog1!=null&&blog1.getUsername().equals(blog.getUsername())){
            blogService.updateBlog(blog);
        }else{
            String message=String.format("更改blog失败，不能更改其他用户的blog！");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("已成功修改blog信息！");
    }


    /*
     * 点赞逻辑
     * 路径 /api/giveALike
     * 传参(json):username, blogid，type（当前只支持type为1且默认为1，故此参数不用添加）
     * 返回值: null
     * 功能：给喜欢的博客点赞
     * */
    @CrossOrigin
    @PostMapping(value ="/api/giveALike")
    @ResponseBody
    public void giveALike(@Valid @RequestBody UserLike userLike)
    {
        if (!userLikeService.giveALike(userLike)) {
            String message = String.format("系统繁忙，请稍后");
        }
    }
    /*
     * 点赞查询
     * 路径 /api/findALike
     * 传参(json):username, blogid，type（当前只支持type为1且默认为1，故此参数不用添加）
     * 返回值(json):username, blogid，type（为null代表无点赞记录)
     * 功能：查找user是否给博客点赞
     * */
    @CrossOrigin
    @PostMapping(value ="/api/findALike")
    @ResponseBody
    public UserLike findALike(@Valid @RequestBody UserLike userLike)
    {
        return userLikeService.find(userLike);
    }



   

  
    /*
    * 上传备忘录的记录
    * 路径：/api/recordUpload
    * 传参(json):username,context,date_
    * 返回值(String)：Result{"插入记录成功！","插入记录失败！"}
    * */
    @CrossOrigin
    @PostMapping(value="/api/recordUpload")
    @ResponseBody
    public Result recordUpload(@Valid @RequestBody Record record){
        if(recordService.getRecordin(record)==null) {
            recordService.insertRecord(record);
            Record record1 = recordService.getRecordin(record);
            if (record1 == null) {
                return ResultFactory.buildFailResult("插入记录失败！");
            } else if (record1.getContext().equals(record.getContext())) {
                return ResultFactory.buildSuccessResult("插入记录成功！");
            }
            return ResultFactory.buildFailResult("插入记录失败！");
        }
        else{
            return recordUpdate(record);
        }
    }

    /*
     * 删除备忘录的记录
     * 路径：/api/deleteRecord
     * 传参(json):username,context(可为空),date_
     * 返回值(String)：Result{"删除记录失败!","删除记录成功！"}
     * */
    @CrossOrigin
    @PostMapping(value="/api/deleteRecord")
    @ResponseBody
    public Result recordDelete(@Valid @RequestBody Record record){
        if(!recordService.deleteRecord(record)){
            return ResultFactory.buildFailResult("删除记录失败!");
        }
        else{
            return ResultFactory.buildSuccessResult("删除记录成功！");
        }
    }

    /*
     * 修改备忘录的记录
     * 路径：/api/updateRecord
     * 传参(json):username,context,date_
     * 返回值(String)：Result{"更新记录成功!","更新记录失败!"}
     * */
    @CrossOrigin
    @PostMapping(value="/api/updateRecord")
    @ResponseBody
    public Result recordUpdate(@Valid @RequestBody Record record){
        Record record_old=recordService.getRecordin(record);    //更新前
        recordService.updateRecord(record);
        Record record_new=recordService.getRecordin(record);    //更新后
        if(!record_new.getContext().equals(record_old.getContext())) {
            return ResultFactory.buildSuccessResult("更新记录成功!");
        }
        return ResultFactory.buildFailResult("更新记录失败！");
    }

  
    /*
     * 获取备忘录的记录
     * 路径：/api/getRecord
     * 传参(json):username,context=null,date_=null
     * 返回值(json): username,context,date_
     * */
    @CrossOrigin
    @PostMapping(value="/api/getRecord")
    @ResponseBody
    public String recordGet(@Valid @RequestBody Record record){
        List<Record> Recordlist= recordService.getRecord(record);
        if(Recordlist==null){
            return null;
        }
        List<Map<String,Object>> mapList=new ArrayList<>();
        for(int i=0;i<Recordlist.size();i++){
            Map<String,Object> map=new HashMap<>();
            map.put("username",Recordlist.get(i).getUsername());
            map.put("context",Recordlist.get(i).getContext());
            map.put("date_",Recordlist.get(i).getDate_());
            mapList.add(map);
        }
        return JSON.toJSONString((mapList));
    }
  
  

}


