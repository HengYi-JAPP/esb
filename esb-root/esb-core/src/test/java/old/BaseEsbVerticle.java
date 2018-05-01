package old;

import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.auth.AuthProvider;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.*;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.vertx.VertxWebContext;
import org.pac4j.vertx.auth.Pac4jAuthProvider;
import org.pac4j.vertx.context.session.VertxSessionStore;
import org.pac4j.vertx.handler.impl.CallbackHandler;
import org.pac4j.vertx.handler.impl.CallbackHandlerOptions;
import org.pac4j.vertx.handler.impl.SecurityHandler;
import org.pac4j.vertx.handler.impl.SecurityHandlerOptions;
import org.pac4j.vertx.http.DefaultHttpActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 描述： esb 调用上下文，一个应用启动需要继承此类
 * 权限检查
 * 服务检查
 * 心跳检测
 * 日志记录
 *
 * @author jzb 2018-03-18
 */
public abstract class BaseEsbVerticle extends AbstractVerticle {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected final Pac4jAuthProvider authProvider = new Pac4jAuthProvider();
    protected Config config = null;
    protected SessionStore<VertxWebContext> sessionStore;
    protected Router router;

    protected void prepareRouter() {
        if (router != null) {
            return;
        }
        router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());
        router.route().handler(CorsHandler.create("*").allowedMethod(HttpMethod.OPTIONS).allowedMethod(HttpMethod.POST).allowedMethod(HttpMethod.PUT).allowedMethod(HttpMethod.PATCH).allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.DELETE));
        router.route().handler(CookieHandler.create());
        // todo 集群环境下，用 ClusteredSessionStore
        final LocalSessionStore vertxSessionStore = LocalSessionStore.create(vertx);
        router.route().handler(SessionHandler.create(vertxSessionStore));

        Optional.ofNullable(getConfigFactory())
                .ifPresent(configFactory -> {
                    sessionStore = new VertxSessionStore(vertxSessionStore.getDelegate());
                    config = configFactory.build();
                    config.setHttpActionAdapter(new DefaultHttpActionAdapter());

                    router.route().handler(UserSessionHandler.create(new AuthProvider(authProvider)));
                    final CallbackHandlerOptions callbackHandlerOptions = new CallbackHandlerOptions().setDefaultUrl("/").setMultiProfile(true);
                    final CallbackHandler callbackHandler = new CallbackHandler(vertx.getDelegate(), sessionStore, config, callbackHandlerOptions);
                    router.getDelegate().get("/callback").handler(callbackHandler);
                    router.post("/callback").handler(BodyHandler.create().setMergeFormAttributes(true));
                    router.getDelegate().post("/callback").handler(callbackHandler);
                });
    }

    protected void addProtectedEndpoint(final String url, final String clientNames) {
        addProtectedEndpoint(url, clientNames, null);
    }

    protected void addProtectedEndpoint(final String url, final String clientNames, final String authName) {
        prepareRouter();
        SecurityHandlerOptions options = new SecurityHandlerOptions().setClients(clientNames);
        if (authName != null) {
            options = options.setAuthorizers(authName);
        }
        final SecurityHandler securityHandler = new SecurityHandler(vertx.getDelegate(), sessionStore, config, authProvider, options);
        router.getDelegate().route(url).handler(securityHandler);
    }

    protected ConfigFactory getConfigFactory() {
        return null;
    }

    /**
     * 返回接口中心分配的应用 id，表身份
     *
     * @return
     */
    public abstract String getAppid();

    /**
     * 返回接口中心分配的应用秘钥
     * 和 {@link #getAppid}一起使用，获取访问令牌
     *
     * @return
     */
    public abstract String getAppsecret();

    // /**
    //  * 对外提供服务的路由
    //  * 必须 / 开始， 为了校验令牌，最终的路由会在前面加上 appid
    //  * 比如： api地址为 /somepath 那么对外提供的服务就是 /${appid}/somepath
    //  *
    //  * @return
    //  */
    // protected Router prepareEsbRouter() {
    //     return null;
    // }
    //
    // /**
    //  * 和esb无关的服务接口， 需自己校验令牌或其他身份验证
    //  * 主要是实现 {@link io.vertx.reactivex.ext.auth.AuthProvider}
    //  *
    //  * @return
    //  */
    // protected Router prepareRouter() {
    //     return null;
    // }
    //
    // protected void esbStart() throws Exception {
    // }
    //
    // protected void esbStop() throws Exception {
    // }
    //
    // @Override
    // public void start() throws Exception {
    //     esbStart();
    //     camelContext.setStreamCaching(true);
    //     camelContext.start();
    //
    //     router = Router.router(vertx);
    //     router.route().handler(BodyHandler.create());
    //     router.route().handler(ResponseContentTypeHandler.create());
    //     router.route("/static/*").handler(StaticHandler.create());
    //     router.route().handler(CorsHandler.create("*").allowedMethod(HttpMethod.OPTIONS).allowedMethod(HttpMethod.POST).allowedMethod(HttpMethod.PUT).allowedMethod(HttpMethod.PATCH).allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.DELETE));
    //     Optional.ofNullable(prepareRouter()).ifPresent(it -> router.mountSubRouter("/", it));
    //     Optional.ofNullable(prepareEsbRouter()).ifPresent(it -> router.mountSubRouter("/" + getAppid(), it));
    //
    //     vertx.createHttpServer()
    //             .requestHandler(router::accept)
    //             .listen(9876);
    //     LOG.info("========开始 ESB SERVICE:" + this.getClass().getName() + "========");
    // }
    //
    // @Override
    // public void stop() throws Exception {
    //     super.stop();
    //     esbStop();
    //
    //     if (camelContext != null) {
    //         camelContext.stop();
    //     }
    //     LOG.info("========结束 ESB SERVICE:" + this.getClass().getName() + "========");
    // }

}
