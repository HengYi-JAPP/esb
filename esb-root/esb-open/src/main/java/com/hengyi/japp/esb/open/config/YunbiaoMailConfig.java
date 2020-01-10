package com.hengyi.japp.esb.open.config;

import com.github.ixtf.japp.core.J;
import lombok.Getter;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.mail.Message.RecipientType.TO;

/**
 * @author jzb 2020-01-10
 */
public class YunbiaoMailConfig {
    @Getter
    private final String host;
    @Getter
    private final String from;
    @Getter
    private final Authenticator authenticator;
    @Getter
    private final String subject;
    @Getter
    private final String urlTpl;
    @Getter
    private final String contentTpl;
    @Getter
    private final String checkSql;
    @Getter
    private final String updateSql;

    public YunbiaoMailConfig(String host, String from, String password, String subject, String urlTpl, String contentTpl, String checkSql, String updateSql) {
        this.host = host;
        this.from = from;
        this.authenticator = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        };
        this.subject = subject;
        this.urlTpl = urlTpl;
        this.contentTpl = contentTpl;
        this.checkSql = checkSql;
        this.updateSql = updateSql;
    }

    public Builder builder() {
        return new Builder(this);
    }

    public static class Builder {
        private final YunbiaoMailConfig config;
        private String to;
        private String url;

        private Builder(YunbiaoMailConfig config) {
            this.config = config;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public MimeMessage buid() throws Exception {
            final Properties properties = new Properties();
            properties.setProperty("mail.smtp.host", config.host);
            properties.put("mail.smtp.auth", "true");
            final Session session = Session.getDefaultInstance(properties, config.authenticator);
            final MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.from));
            message.setSubject(config.subject, UTF_8.name());
            message.addRecipient(TO, new InternetAddress(to));
            final String content = J.strTpl(config.contentTpl, Map.of("url", this.url));
            message.setContent(content, "text/html;charset=" + UTF_8);
            return message;
        }
    }
}
