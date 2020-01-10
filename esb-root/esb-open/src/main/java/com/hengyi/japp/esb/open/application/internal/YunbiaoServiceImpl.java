package com.hengyi.japp.esb.open.application.internal;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.ixtf.japp.codec.Jcodec;
import com.github.ixtf.japp.core.J;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.open.application.YunbiaoService;
import com.hengyi.japp.esb.open.application.command.YunbiaoModifyPasswordCommand;
import com.hengyi.japp.esb.open.application.command.YunbiaoModifyPasswordMailCommand;
import com.hengyi.japp.esb.open.config.YunbiaoMailConfig;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import reactor.core.publisher.Mono;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.mail.Message.RecipientType.TO;

/**
 * @author jzb 2020-01-10
 */
@Slf4j
@Singleton
public class YunbiaoServiceImpl implements YunbiaoService {
    private static final Cache<String, String> MAIL_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(30))
            .build();
    private final YunbiaoMailConfig yunbiaoMailConfig;
    private final JDBCClient yunbiaoDs;

    @Inject
    private YunbiaoServiceImpl(YunbiaoMailConfig yunbiaoMailConfig, @Named("yunbiao_DS") JDBCClient yunbiaoDs) {
        this.yunbiaoMailConfig = yunbiaoMailConfig;
        this.yunbiaoDs = yunbiaoDs;
    }

    @Override
    public Mono<Void> handle(YunbiaoModifyPasswordMailCommand command) {
        final String loginId = command.getLoginId();
        return Mono.fromCallable(() -> {
            // 注意顺序
            final JsonArray params = new JsonArray().add(command.getTo()).add(loginId);
            final String sql = yunbiaoMailConfig.getCheckSql();
            return Future.<ResultSet>future(f -> yunbiaoDs.queryWithParams(sql, params, f));
        }).flatMap(Util::mono).map(resultSet -> {
            final List<JsonObject> rows = resultSet.getRows();
            if (rows.size() == 1) {
                return rows.get(0);
            }
            throw new RuntimeException();
        }).doOnSuccess(it -> {
            final String mailKey = Jcodec.uuid58();
            MAIL_CACHE.put(mailKey, loginId);
            final String url = J.strTpl(yunbiaoMailConfig.getUrlTpl(), Map.of("mailKey", mailKey));
            sendModifyPasswordMail(command.getTo(), url);
        }).then();
    }

    @SneakyThrows(MessagingException.class)
    public void sendModifyPasswordMail(String to, String url) {
        final Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", yunbiaoMailConfig.getHost());
        properties.put("mail.smtp.auth", "true");
        final Session session = Session.getDefaultInstance(properties, yunbiaoMailConfig.getAuthenticator());
        final MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(yunbiaoMailConfig.getFrom()));
        message.setSubject(yunbiaoMailConfig.getSubject(), UTF_8.name());
        message.addRecipient(TO, new InternetAddress(to));
        final String content = J.strTpl(yunbiaoMailConfig.getContentTpl(), Map.of("url", url));
        message.setContent(content, "text/html;charset=" + UTF_8);
        Transport.send(message);
    }

    @Override
    public Mono<Void> handle(YunbiaoModifyPasswordCommand command) {
        final String mailKey = command.getMailKey();
        return Mono.fromCallable(() -> {
            final String loginId = MAIL_CACHE.getIfPresent(mailKey);
            if (J.isBlank(loginId)) {
                throw new RuntimeException();
            }
            final String password = DigestUtils.md5Hex(command.getPassword());
            // 注意顺序
            final JsonArray params = new JsonArray().add(password).add(loginId);
            final String sql = yunbiaoMailConfig.getUpdateSql();
            return Future.<UpdateResult>future(f -> yunbiaoDs.updateWithParams(sql, params, f));
        }).flatMap(Util::mono).map(updateResult -> {
            final int updated = updateResult.getUpdated();
            if (updated == 1) {
                MAIL_CACHE.invalidate(mailKey);
                return true;
            }
            throw new RuntimeException();
        }).then();
    }

}
