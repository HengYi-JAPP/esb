package old;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.jzb.J;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.vertx.cas.logout.VertxCasLogoutHandler;
import org.pac4j.vertx.core.store.VertxLocalMapStore;

import java.util.Optional;

/**
 * 描述：
 *
 * @author jzb 2018-03-26
 */
public abstract class BasePac4jConfigurationFactory implements ConfigFactory {
    private final Vertx vertx;

    protected BasePac4jConfigurationFactory(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Config build(Object... parameters) {
        final String baseUrl = Optional.ofNullable(jsonConf())
                .map(it -> it.getString("baseUrl"))
                .filter(J::nonBlank)
                .orElse("http://localhost:8080");
        final Clients clients = new Clients(baseUrl + "/callback",
                casClient(vertx),
                new AnonymousClient());
        final Config config = new Config(clients);
        return config;
    }

    protected Client casClient(Vertx vertx) {
        final String casUrl = Optional.ofNullable(jsonConf())
                .map(it -> it.getString("casUrl"))
                .filter(J::nonBlank)
                .orElse("http://cas.hengyi.com:8080/login");
        final CasConfiguration casConfiguration = new CasConfiguration(casUrl, CasProtocol.CAS30);
        final CasProxyReceptor casProxyReceptor = new CasProxyReceptor();
        casConfiguration.setProxyReceptor(casProxyReceptor);
        casConfiguration.setLogoutHandler(new VertxCasLogoutHandler(new VertxLocalMapStore(vertx.getDelegate()), false));
        final CasClient casClient = new CasClient(casConfiguration);
        return casClient;
    }

    protected abstract JsonObject jsonConf();
}
