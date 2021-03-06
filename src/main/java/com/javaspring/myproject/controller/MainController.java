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


    //???????????????????????????uploadPath
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


    //????????????--home

    @RequestMapping("/home")
    @ModelAttribute
    public String home(String username,String password,Model model){
        model.addAttribute(
                "username",
                username);
        return "home";
    }

    //????????????--login
    @RequestMapping("/login")
    @ModelAttribute
    public String login() {
        return "login";
    }

    /*
     * ??????
     * ?????? /api/login
     * ??????(json) username,password
     * ?????????(json--Result) code,message,data
     * */
    @CrossOrigin
    @RequestMapping(value = "/api/login", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Result login(@Valid @RequestBody UserVo userVo, BindingResult bindingResult) {
        if (userVo.getPassword().equals("")||userVo.getUsername().equals("")) {
            String message = String.format("??????????????????????????????");
            return ResultFactory.buildFailResult(message);
        }
        if (bindingResult.hasErrors()) {
            String message = String.format("???????????????????????????[%s]???", bindingResult.getFieldError().getDefaultMessage());
            return ResultFactory.buildFailResult(message);
        }
        if (!userService.judgeByUserName(userVo)) {
            userVo.setEmail(userVo.getUsername());
            if (!userService.judgeByEMail(userVo)) {
                String message = String.format("?????????????????????/????????????????????????");
                return ResultFactory.buildFailResult(message);
            }
            userVo.setUsername((userService.getUserByEmail(new User(userVo.getEmail()).getEmail())).getUsername());
        }
        return ResultFactory.buildSuccessResult("????????????????????????????????????"+userVo.getUsername()+"?????????");
    }

    /*
     * ??????????????????
     * ?????? /api/sendEmail
     * ??????(json) email
     * ?????????(json--Result) code,message,data
     * */
    @CrossOrigin
    @PostMapping(value = "/api/sendEmail")
    @ResponseBody
    public Result sendEmail(@Valid @RequestBody UserVo userVo ,HttpSession httpSession ) {
        /*
         * ??????HttpSession???????????????????????????????????????????????????????????????
         * */
        if (!EMailService.sendMimeMail(userVo.getEmail(), httpSession)) {
            String message = String.format("??????????????????????????????????????????");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("??????????????????????????????");
    }



    /*
     * ???????????????
     * ?????? /api/regist
     * ??????(json) username,password,email,code
     * ?????????(json--Result) code,message,data
     * */
    @CrossOrigin
    @PostMapping(value = "/api/regist")
    @ResponseBody
    public Result regist(@Valid @RequestBody UserVo userVo) {

        if (!EMailService.registered(userVo)) {
            String message = String.format("?????????????????????????????????");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("???????????????");
    }



    /*
     * ????????????
     * ?????? /api/findPassword
     * ??????(json) email
     * ?????????(json--Result) code,message,data
     * */
    @CrossOrigin
    @PostMapping(value = "/api/findPassword")
    @ResponseBody
    public Result findPassWord(@Valid @RequestBody UserVo userVo){
        if(!EMailService.findPassword_sendEmail(userVo.getEmail())){
            String message=String.format("???????????????????????????????????????,???????????????");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("????????????,?????????????????????????????????");
    }


    /*
     * ??????????????????
     * ?????? /api/changePassword
     * ??????(json) email,Password,newPassword,newPasswordRepeat
     * ?????????(json--Result) code,message,data
     * */
    @CrossOrigin
    @PostMapping(value = "/api/changePassword")
    @ResponseBody
    public Result changePassword(@Valid @RequestBody UserVo userVo){
        if(!EMailService.changePassword(userVo.getEmail(),userVo.getPassword(),userVo.getNewPassword(),userVo.getNewPasswordRepeat())){
            String message=String.format("????????????,???????????????");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("????????????,??????????????????????????????????????????????????????");
    }
    /*
     * ??????????????????url
     * ?????? /api/getUserTouxiang
     * ??????(json):username/email
     * ?????????(String) url
     * ????????????????????????????????????
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getUserTouxiang")
    @ResponseBody
    public String getUserTouxiang(@Valid @RequestBody User user){
        return userService.getUserTouxiang(user.getUsername());
    }
    /*
     * ??????????????????
     * ?????? /api/getUser
     * ??????(json):username/email
     * ?????????(json) ???????????????User?????????
     * ???????????????????????????
     * */
    @CrossOrigin
    @PostMapping(value ="/api/getUser")
    @ResponseBody
    public User getUser(@Valid @RequestBody User user) {
        return userService.getUser(user.getUsername());
    }
    /*
     * ??????????????????
     * ?????? /api/updateUser
     * ??????(json) username(??????????????????????????? #????????????#(newUsername,newEmail,newTel?????????
     * ????????? Result
     * */
    @CrossOrigin
    @PostMapping(value ="/api/updateUser")
    @ResponseBody
    public Result updateUser(@Valid @RequestBody UserVo userVo){
        if (!userService.updateUser(userVo)) {
            String message = String.format("???????????????????????????");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("???????????????????????????");
    }
  

    /*
     * ??????file
     * ?????? /api/upload
     * ??????(MultipartFile) file,username,(int) usage
     * ?????????(json) result{"success","fail"},url,filename
     * */
    @CrossOrigin
    @PostMapping(value ="/api/upload")
    public String upload(@RequestParam("file") MultipartFile[] files,String username,int usage,HttpServletRequest request) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH-mm-ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");

        Map<String,Object> map=new HashMap<>();
        // ??? uploadPath ??????????????????username???????????????????????????????????????
        // ?????????2019211996/2021/09/04/15:23:46/xxx.jpg
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
                    //??????????????????????????????
                    String size=fileService.getFileSize(files[i].getSize());

                    File file2=new File(fileName,username,filePath,date_, size);

                    fileService.insertFile(file2);
                    // ?????????????????????????????????

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
     * ??????file
     * ?????? /api/getFile
     * ??????(json) username,filename,date_
     * ?????????(json) filename,username,url,date_,size_
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
     * ??????files
     * ?????? /api/getAllFiles
     * ??????(json) username
     * ?????????(json--??????) filename,username,url,date_,size_
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
     * ??????file
     * ?????? /api/deleteFile
     * ??????(json) username,filename,date_
     * ?????????(String) result{"success","fail"}
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
     * ??????blog
     * ?????? /api/blogUpload
     * ??????(json) username,content,title,picture,visible
     * ?????????(string) blogid
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
     * ??????blog
     * ?????? /api/getBlog
     * ??????(json) blogid
     * ?????????(json) blogid,username,time_,title,content,picture,count,visible
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
     * ??????blogs
     * ?????? /api/getAllBlogs
     * ??????(json) username
     * ?????????(json) blogid,username,time_,title,content,picture,count,visible
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
     * ????????????????????????blogs
     * ?????? /api/getPublicBlogs
     * ??????   username
     * ?????????(json) blogid,username,time_,title,content,picture,count,isliked
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
     * ?????????????????????blogs
     * ?????? /api/getAllHotBlogs
     * ??????(json):username
     * ?????????(json) blogid,username,time_,title,content,picture,count,visible,isliked
     * ????????????????????????????????????????????????????????????
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
     * ??????????????????????????????blogs
     * ?????? /api/getPublicHotBlogs
     * ??????: username
     * ?????????(json) blogid,username,time_,title,content,picture,count,visible,isliked
     * ????????????????????????????????????????????????????????????
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
     * ??????blog
     * ?????? /api/deleteBlog
     * ??????(json) blogid,username
     * ?????????(json)(String) result{"success","fail"}
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
     * ??????blog?????????
     * ?????? /api/updateBlog
     * ??????(json):blogid,username,time_,title,content,picture,visible
     * ?????????(String) Result{message,data}
     * ???????????????????????????????????????????????????blog??????????????????username?????????????????????blog????????????username??????
     * */
    @CrossOrigin
    @PostMapping(value ="/api/updateBlog")
    @ResponseBody
    public Result updateBlog(@Valid @RequestBody Blog blog){
        Blog blog1=blogService.getBlog(blog);
        if(blog1!=null&&blog1.getUsername().equals(blog.getUsername())){
            blogService.updateBlog(blog);
        }else{
            String message=String.format("??????blog????????????????????????????????????blog???");
            return ResultFactory.buildFailResult(message);
        }
        return ResultFactory.buildSuccessResult("???????????????blog?????????");
    }


    /*
     * ????????????
     * ?????? /api/giveALike
     * ??????(json):username, blogid???type??????????????????type???1????????????1??????????????????????????????
     * ?????????: null
     * ?????????????????????????????????
     * */
    @CrossOrigin
    @PostMapping(value ="/api/giveALike")
    @ResponseBody
    public void giveALike(@Valid @RequestBody UserLike userLike)
    {
        if (!userLikeService.giveALike(userLike)) {
            String message = String.format("????????????????????????");
        }
    }
    /*
     * ????????????
     * ?????? /api/findALike
     * ??????(json):username, blogid???type??????????????????type???1????????????1??????????????????????????????
     * ?????????(json):username, blogid???type??????null?????????????????????)
     * ???????????????user?????????????????????
     * */
    @CrossOrigin
    @PostMapping(value ="/api/findALike")
    @ResponseBody
    public UserLike findALike(@Valid @RequestBody UserLike userLike)
    {
        return userLikeService.find(userLike);
    }



   

  
    /*
    * ????????????????????????
    * ?????????/api/recordUpload
    * ??????(json):username,context,date_
    * ?????????(String)???Result{"?????????????????????","?????????????????????"}
    * */
    @CrossOrigin
    @PostMapping(value="/api/recordUpload")
    @ResponseBody
    public Result recordUpload(@Valid @RequestBody Record record){
        if(recordService.getRecordin(record)==null) {
            recordService.insertRecord(record);
            Record record1 = recordService.getRecordin(record);
            if (record1 == null) {
                return ResultFactory.buildFailResult("?????????????????????");
            } else if (record1.getContext().equals(record.getContext())) {
                return ResultFactory.buildSuccessResult("?????????????????????");
            }
            return ResultFactory.buildFailResult("?????????????????????");
        }
        else{
            return recordUpdate(record);
        }
    }

    /*
     * ????????????????????????
     * ?????????/api/deleteRecord
     * ??????(json):username,context(?????????),date_
     * ?????????(String)???Result{"??????????????????!","?????????????????????"}
     * */
    @CrossOrigin
    @PostMapping(value="/api/deleteRecord")
    @ResponseBody
    public Result recordDelete(@Valid @RequestBody Record record){
        if(!recordService.deleteRecord(record)){
            return ResultFactory.buildFailResult("??????????????????!");
        }
        else{
            return ResultFactory.buildSuccessResult("?????????????????????");
        }
    }

    /*
     * ????????????????????????
     * ?????????/api/updateRecord
     * ??????(json):username,context,date_
     * ?????????(String)???Result{"??????????????????!","??????????????????!"}
     * */
    @CrossOrigin
    @PostMapping(value="/api/updateRecord")
    @ResponseBody
    public Result recordUpdate(@Valid @RequestBody Record record){
        Record record_old=recordService.getRecordin(record);    //?????????
        recordService.updateRecord(record);
        Record record_new=recordService.getRecordin(record);    //?????????
        if(!record_new.getContext().equals(record_old.getContext())) {
            return ResultFactory.buildSuccessResult("??????????????????!");
        }
        return ResultFactory.buildFailResult("?????????????????????");
    }

  
    /*
     * ????????????????????????
     * ?????????/api/getRecord
     * ??????(json):username,context=null,date_=null
     * ?????????(json): username,context,date_
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


