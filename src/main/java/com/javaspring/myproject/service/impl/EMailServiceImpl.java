package com.javaspring.myproject.service.impl;

import com.javaspring.myproject.beans.User;
import com.javaspring.myproject.beans.UserVo;
import com.javaspring.myproject.dao.IUserDao;
import com.javaspring.myproject.dao.impl.UserVoToUser;
import com.javaspring.myproject.service.IEMailService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Random;
@Service
public class EMailServiceImpl implements IEMailService {
    @Autowired
    private JavaMailSender mailSender;//一定要用@Autowired

    @Autowired
    private IUserDao userDao;//userDao

    @Autowired
    private HttpSession httpSession;
    //application.properties中已配置的值
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 给前端输入的邮箱，发送验证码
     */
    public boolean sendMimeMail( String email, HttpSession session) {

        //该邮箱已经注册
        if(userDao.getUser(new User(email))!=null){
            return false;
        }
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject("验证码邮件");//主题
            //生成随机数
            String code = randomCode();
            this.httpSession=session;
            //将随机数放置到session中
            httpSession.setAttribute("email",email);
            httpSession.setAttribute("code",code);

            mailMessage.setText("您收到的验证码是："+code);//内容

            mailMessage.setTo(email);//发给谁

            mailMessage.setFrom(from);//你自己的邮箱

            mailSender.send(mailMessage);//发送
            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 随机生成6位数的验证码
     */
    public String randomCode(){
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    /**
     * 检验验证码是否一致
     */
    public boolean registered(UserVo userVo){
        //获取session中的验证信息
        String email = (String) httpSession.getAttribute("email");
        String code = (String) httpSession.getAttribute("code");

        //获取表单中的提交的验证信息
        String voCode = userVo.getCode();

        //如果email数据为空，或者不一致，注册失败
        if (email == null || email.isEmpty()||!email.equals(userVo.getEmail())){
            //return "error,请重新注册";
            return false;
        }else if (!code.equals(voCode)){
            //return "error,请重新注册";
            return false;
        }

        //保存数据
        User user = UserVoToUser.toUser(userVo);

        //将数据写入数据库
        userDao.insertUser(user);

        //跳转成功页面
        return true;
    }


}
