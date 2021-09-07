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
     * 发送修改信息时进行安全验证邮箱
     * 路径 /api/sendVerificationEmail
     * 传参(json) email
     * 返回值(json--Result) code,message,data
     * */
    @CrossOrigin
    @PostMapping(value = "/api/sendVerificationEmail")
    @ResponseBody
    public Result sendVerificationEmail(@Valid @RequestBody UserVo userVo,HttpSession httpSession) {
        //邮箱是唯一的，故通过当前邮箱确认待修改对象是否存在
        User user = userService.getUserByEmail(userVo.getEmail());
        if(user==null)
        {
            return ResultFactory.buildFailResult("该邮箱未注册! ");
        }
        else {
            if (!EMailService.sendMimeMail(user.getEmail(), httpSession)) {
                String message = String.format("发送邮箱失败！");
                return ResultFactory.buildFailResult(message);
            }
            return ResultFactory.buildSuccessResult("已发送验证码至邮箱！");
        }
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
    public Result fhangePassword(@Valid @RequestBody UserVo userVo){
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
        System.out.println(userService.getUserTouxiang(user.getUsername()));
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
        Map<String,Object> map=new HashMap<>();

        map.put("blogid",blog1.getBlogid());
        map.put("username",blog1.getUsername());
        map.put("time_",blog1.getTime_());
        map.put("title",blog1.getTitle());
        map.put("content",blog1.getContent());
        map.put("picture",blog1.getPicture());
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
     * 传参   null
     * 返回值(json) blogid,username,time_,title,content,picture,count,visible
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getPublicBlogs")
    @ResponseBody
    public String getPublicBlogs() {
        List<Blog> bloglist=blogService.getPublicBlogs();
        List<Map<String,Object>> maplist=new ArrayList<>();
        for (int i = 0; i < bloglist.size(); i++) {
            Map<String,Object> map=new HashMap<>();

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
     * 返回值(json) blogid,username,time_,title,content,picture,count,visible
     * 功能：以热度从高到低的顺序浏览自己的博客
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getAllHotBlogs")
    @ResponseBody
    public String getAllHotBlogs(@Valid @RequestBody Blog blog) {
        List<Blog> bloglist=blogService.getAllHotBlogs(blog);
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
     * 以热度顺序获取公开的blogs
     * 路径 /api/getPublicHotBlogs
     * 传参: null
     * 返回值(json) blogid,username,time_,title,content,picture,count,visible
     * 功能：以热度从高到低的顺序浏览公开的博客
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getPublicHotBlogs")
    @ResponseBody
    public String getPublicHotBlogs() {
        List<Blog> bloglist=blogService.getPublicHotBlogs();
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
    * 上传备忘录的记录
    * 路径：/api/recordUpload
    * 传参(json):username,context,date_
    * 返回值(String)：Result{"插入记录成功！","插入记录失败！"}
    * */
    @CrossOrigin
    @PostMapping(value="/api/recordUpload")
    @ResponseBody
    public Result recordUpload(@Valid @RequestBody Record record){
        recordService.insertRecord(record);
        Record record1=recordService.getRecordin(record);
        if(record1.getContext().equals(record.getContext())){
            return ResultFactory.buildSuccessResult("插入记录成功！");
        }
        return ResultFactory.buildFailResult("插入记录失败！");
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
  
  
   /*以下为测试接口*/
    /*//测试接口--jwxt_login
    @RequestMapping("/jwxt_login")
    public String jwxt_login(){

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<String, String>();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(postParameters, headers);
        String dataStr= restTemplate.postForObject("https://jwgl.bupt.edu.cn/Logon.do?method=logon&flag=sess", requestEntity, String.class);
        String scode=dataStr.split("#")[0];
        String sxh=dataStr.split("#")[1];
        String code="2019211996"+"%%%"+"JYX014524jyx";//账号密码
        String encoded="";
        for(int i=0;i<code.length();i++){
            if(i<20){
                encoded=encoded+code.substring(i,i+1)+scode.substring(0,parseInt(sxh.substring(i,i+1)));
                scode = scode.substring(parseInt(sxh.substring(i,i+1)));
            }else{
                encoded=encoded+code.substring(i);
                i=code.length();
            }
        }
        for (int i = 0; i <100 ; i++) {

            System.out.println(dataStr+"       "+encoded);
        }
        //登陆
        RestTemplate restTemplate1 = new RestTemplate();

        HttpHeaders headers1 = new HttpHeaders();
        MultiValueMap<String, String> postParameters1 = new LinkedMultiValueMap<String, String>();

        HttpEntity<MultiValueMap<String, String>> requestEntity1 = new HttpEntity<MultiValueMap<String, String>>(postParameters1, headers1);

        postParameters.add("userAccount","2019211996");
        postParameters.add("userPassword","JYX014524jyx");
        return restTemplate1.postForObject("/Logon.do?method=logonByDxfz", requestEntity1, String.class);
    }*/

  /*
  *qqmail_alias=2019211996@bupt.edu.cn; SERVERID=131; JSESSIONID=4E9AE8E8A099F7D73D428399B75F30CD
        postParameters.add("RANDOMCODE","");
        postParameters.add("encoded",encoded);
  *
  * */

//all static for convenience

    //file request parameters
    static String userAccount = "2019211996";        //你的账号
    static String userPassword = "JYX014524jyx";          //你的密码
    static String RANDOMCODE = "";            //验证码默认设置为空，后续通过用户的输入来填充这个值

    //验证码请求地址
    final static String verifyImgSrcURL = "https://jwgl.bupt.edu.cn/verifycode.servlet";//cookie
    //验证码保存的桌面位置
    final static String verifyImgDestURL = "C:\\Users\\86182\\Desktop\\verifyCodeImg.jpg";
    //登陆请求的地址
    final static String HomeUrl = "https://jwgl.bupt.edu.cn/Logon.do?method=logon&flag=sess";

    final static String loginUrl = "https://jwgl.bupt.edu.cn//Logon.do?method=logon";
    //课表的地址
    final static String getTableBaseURL = "https://jwgl.bupt.edu.cn/jsxsd/xskb/xskb_list.do";

    //cookie name
    final static String sessionName = "JSESSIONID";
    final static String SERVERID="SERVERID";
    final static String qqmail_alias="qqmail_alias";

    //cookie value
    static String sessionValue = "";
    static String SERVERIDValue = "";
    static String qqmail_aliasValue = "";
    //request parameters [personType, userAccount, userPassword, RANDOMCODE]
    static Map<String, String> reqData = new HashMap<>();

    static Map<String, String> cookies = new HashMap<>();       //save cookies

    // 'reqData' initialization
    static {
        reqData.put("userAccount", userAccount);
        reqData.put("userPassword", userPassword);
    }

    /*以下为测试接口*/
    //测试接口--jwxt_login
    @RequestMapping("/jwxt_login")
    public String jwxt_login()throws IOException {

        //download verifyImg and
        //get the cookie we getfrom the img
        //use the cookie everywhere around the site.
        downloadImg(verifyImgSrcURL, verifyImgDestURL);
        System.out.println("Download Image Successfully");

        //print cookie
        System.out.println(cookies);

        //input verifycode
        System.out.print("Please input your verifyCode: ");
        RANDOMCODE = new Scanner(System.in).nextLine();
        reqData.put("RANDOMCODE", RANDOMCODE);

        //login using cookie prepared.
        Connection loginConn = Jsoup.connect(HomeUrl);
        Connection.Response loginResponse = loginConn.execute();
        String dataStr= loginResponse.body();
        String scode=dataStr.split("#")[0];
        String sxh=dataStr.split("#")[1];
        String code=userAccount+"%%%"+userPassword;//账号密码
        String encoded="";
        for(int i=0;i<code.length();i++){
            if(i<20){
                encoded=encoded+code.substring(i,i+1)+scode.substring(0,parseInt(sxh.substring(i,i+1)));
                scode = scode.substring(parseInt(sxh.substring(i,i+1)));
            }else{
                encoded=encoded+code.substring(i);
                i=code.length();
            }
        }
        reqData.put("encoded", encoded);
        System.out.println(encoded);
        Connection connect = Jsoup.connect(loginUrl);
        Map<String, String> header = new HashMap<String, String>();
        header.put("GET", "http://www.baidu.com");
        header.put("User-Agent", "	Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0");
        header.put("Accept", "	text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        header.put("Accept-Language", "zh-cn,zh;q=0.5");
        header.put("Accept-Charset", "	GB2312,utf-8;q=0.7,*;q=0.7");
        //Cookie: SERVERID=131; JSESSIONID=B6E9BEB557F1DA10598A92DCFA936DA5
        header.put("Cookie", "SERVERID=131; JSESSIONID=B6E9BEB557F1DA10598A92DCFA936DA5");
        header.put("Connection", "keep-alive");
        Connection data = connect.data(header);
        Document document = data.get();

        return document.html();
/*
        Connection loginConn_ = Jsoup.connect(loginUrl);
        for (int i = 0; i < 100; i++) {

            System.out.println(cookies);
        }
*/
        //login using cookies
       /* Cookie cookie1 = new Cookie(sessionName, sessionValue);
        cookie1.setPath("/jsxsd/");
        cookie1.setDomain("jwgl.bupt.edu.cn");
        cookie1.isHttpOnly();*/


       /*loginConn_.cookie(SERVERID,  SERVERIDValue);
        loginConn_.cookie(qqmail_alias,  qqmail_aliasValue);
        loginConn_.cookie(sessionName,  sessionValue);
        //login parameters
        loginConn_.data(reqData);

        //获取课表
        Connection getTableiConn = Jsoup.connect(getTableBaseURL);

        getTableiConn.cookie(SERVERID,  SERVERIDValue);
        getTableiConn.cookie(qqmail_alias,  qqmail_aliasValue);
        getTableiConn.cookie(sessionName,  sessionValue);
        Connection.Response getTableResponse = loginConn_.execute();
        return getTableResponse.body();*/
        //。。。未完，看下面分析
        /*try {
            return sendPostByCookie(loginUrl,reqData,cookies);
        } catch (Exception e) {
            e.printStackTrace();
        }*/




    }

    // download verifyCode image from 'srcUrl' and save to 'dest' at localhost
    private static void downloadImg(String srcUrl, String dest) throws MalformedURLException, IOException {
        //get image using the cookie
        HttpURLConnection imgConn = (HttpURLConnection) (new URL(srcUrl)).openConnection();
        //get the cookie
        Map header = imgConn.getHeaderFields();
        //这里通过获取每一个Cookie，然后根据实际需要，获取token、JSESSIONID等信息
        List<String> cookielist=((List<String>)header.get("Set-Cookie"));
        for (int i = 0; i <100 ; i++) {
            System.out.println(cookielist);
        }
        String str1 =cookielist.get(0);
        String str2 =cookielist.get(1);
        SERVERIDValue =str1.substring( str1.indexOf('=') + 1, str1.indexOf(';') );
        sessionValue =str2.substring(10,str2.indexOf(';'));
        qqmail_aliasValue = userAccount+"@bupt.edu.cn";
        cookies.put( sessionName,  sessionValue );
        cookies.put( SERVERID,  SERVERIDValue );
        cookies.put( qqmail_alias,  qqmail_aliasValue );

        //new input from network( 'imgConn' )
        try (BufferedInputStream imgInputStream = new BufferedInputStream(imgConn.getInputStream())) {
            //new output to local file system
            try (BufferedOutputStream imgOutputStream = new BufferedOutputStream(new FileOutputStream(dest));) {
                byte[] buf = new byte[1024];
                while (-1 != (imgInputStream.read(buf)))
                    imgOutputStream.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    private static void GetCookie(String srcUrl) throws IOException {
        //get image using the cookie
        HttpURLConnection imgConn = (HttpURLConnection) ( new URL(srcUrl) ).openConnection();
        imgConn.connect();

        //get the cookie
        Map header = imgConn.getHeaderFields();
        //这里通过获取每一个Cookie，然后根据实际需要，获取token、JSESSIONID等信息
        List<String> cookielist=((List<String>)header.get("Set-Cookie"));
        for (int i = 0; i <100 ; i++) {
            System.out.println(cookielist);
           System.out.println(cookie1);
            System.out.println(cookie2);
            System.out.println(cookie3);
        }
        String str1 =cookielist.get(0);
        String str2 =cookielist.get(1);
        SERVERIDValue =str1.substring( str1.indexOf('=') + 1, str1.indexOf(';') );
        sessionValue ="63F4FE0FA39859E1F07A38F72E65818E";
        //sessionValue =str2.substring(10,str2.indexOf(';'));
        qqmail_aliasValue = userAccount+"@bupt.edu.cn";
        cookies.put( sessionName,  sessionValue );
        cookies.put( SERVERID,  SERVERIDValue );
        cookies.put( qqmail_alias,  qqmail_aliasValue );

    }
    */
    private void GetCookie(String srcUrl) throws IOException {
        //时间戳
        HttpClient client = new HttpClient();
        //post请求方式
        GetMethod getMethod = new GetMethod(srcUrl);
        /*PostMethod postMethod = new PostMethod(srcUrl);
        //推荐的数据存储方式,类似key-value形式
       NameValuePair telPair = new NameValuePair();
        telPair.setName("tel");
        telPair.setValue("181****0732");
        NameValuePair pwdPair = new NameValuePair("pwd","a123456");
        //封装请求参数
        postMethod.setRequestBody(new NameValuePair[]{null});
        //这里是设置请求内容为json格式,根据站点的格式决定
        //因为这个网站会将账号密码转为json格式,所以需要这一步
        postMethod.setRequestHeader("Content_Type","application/json");
        //执行请求*/
        getMethod.setRequestHeader("Content_Type", "application/json");
        getMethod.setRequestHeader("SetCoo", "application/json");

        client.executeMethod(getMethod);
        //通过Post/GetMethod对象获取响应头信息
        /*String cookie = postMethod.getResponseHeader("Set-Cookie").getValue();
        //截取需要的内容
        String sub = cookie.substring(cookie.indexOf("&"), cookie.lastIndexOf("&"));
        String[] splitPwd = sub.split("=");
        String pwd = splitPwd[1];
        System.out.println(pwd);*/
        Header header = getMethod.getResponseHeader("Set-Cookie");
    }

    public static String sendPostByCookie(String url, Map<String, String> body, Map<String, String> cookies) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json");
        //写入cookie
        httpPost.addHeader(HttpHeaders.COOKIE, sessionName +"="+"7C85384608AA36D751F106B2C174E9AF");
        //写入body
        httpPost.setEntity(new StringEntity("encoded", Consts.UTF_8));
        //执行请求
        CloseableHttpResponse response = httpClient.execute(httpPost);
        System.out.println(response.toString());
        HttpEntity entity = response.getEntity();
        String responseContent = EntityUtils.toString(entity, Consts.UTF_8);
        response.close();
        httpClient.close();
        return responseContent;
    }
}


