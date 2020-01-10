package sign;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hengyi.japp.esb.open.OpenGuiceModule;
import com.hengyi.japp.esb.open.application.YunbiaoService;
import com.hengyi.japp.esb.open.application.command.YunbiaoModifyPasswordCommand;
import com.hengyi.japp.esb.open.application.command.YunbiaoModifyPasswordMailCommand;
import reactor.core.publisher.Mono;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author jzb 2020-01-10
 */
public class MailTest {
    private static final Cache<String, String> MAIL_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(30))
            .build();

    public static void main(String[] args) throws Exception {
        final Object test = Mono.fromCallable(() -> null).block();
        System.out.println(test);


//        final Vertx vertx = Vertx.vertx();
//        OpenGuiceModule.init(vertx);
//
//        sendMail();
//        System.out.println("sendMail success");
//        modifyPassword();
//        System.out.println("modifyPassword success");
//        modifyPassword();

    }

    private static void modifyPassword() {
        final YunbiaoService yunbiaoService = OpenGuiceModule.getInstance(YunbiaoService.class);
        final YunbiaoModifyPasswordCommand command = new YunbiaoModifyPasswordCommand();
        command.setPassword("124");
        yunbiaoService.handle(command).block();
    }

    private static void sendMail() {
        final YunbiaoService yunbiaoService = OpenGuiceModule.getInstance(YunbiaoService.class);
        final YunbiaoModifyPasswordMailCommand command = new YunbiaoModifyPasswordMailCommand();
        command.setLoginId("jjf001");
        command.setTo("jzb@hengyi.com");

//        command.setLoginId("jjf001");
//        command.setTo("12000082@hengyi.com");
        yunbiaoService.handle(command).block();
    }

    public static void test1() throws Exception {
        // 收件人电子邮箱
        final String to = "jzb@hengyi.com";
        // 发件人电子邮箱
        final String from = "66461063@qq.com";
        // 指定发送邮件的主机为 smtp.qq.com
        final String host = "smtp.qq.com";  //QQ 邮件服务器

        // 获取系统属性
        final Properties properties = new Properties();
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");
        properties.setProperty("mail.user", "66461063@qq.com");
        properties.setProperty("mail.password", "jciywflhlmuicahc");
        // 获取默认session对象
//        final Session session = Session.getDefaultInstance(properties);
        final Session session = Session.getDefaultInstance(properties, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("66461063@qq.com", "jciywflhlmuicahc"); //发件人邮件用户名、密码
            }
        });

        // 创建默认的 MimeMessage 对象
        final MimeMessage message = new MimeMessage(session);

        // Set From: 头部头字段
        message.setFrom(new InternetAddress(from));

        // Set To: 头部头字段
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        // Set Subject: 头部头字段
        message.setSubject("恒逸云表密码修改", UTF_8.name());

        // 设置消息体
        final String content = "<h1>修改密码</h1><hr>" +
                "http://www.baidu.com";
        message.setContent(content, "text/html;charset=" + UTF_8);

        // 发送消息
        Transport.send(message);
        System.out.println("Sent message successfully....from runoob.com");
    }
}