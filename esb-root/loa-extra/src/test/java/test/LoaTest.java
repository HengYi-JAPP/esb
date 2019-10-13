package test;

import com.hengyi.japp.esb.sap.callback.LoaGuiceModule;
import io.vertx.core.Vertx;
import loa.biz.LOAApp;
import loa.biz.LOAFormDataObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2019-10-10
 */
@Slf4j
public class LoaTest {
    @SneakyThrows
    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        LoaGuiceModule.init(vertx);

        Mono.fromCallable(() -> {
            final LOAApp vApp = LoaGuiceModule.getInstance(LOAApp.class);
            LOAFormDataObject vObj = vApp.newFormDataObject("ZRFC_SD_YX_003回调表");
            vObj.addRawValue("TRANSID", UUID.randomUUID().toString());
            vObj.addRawValue("VBELN", "aaa111");
            vObj.addRawValue("OPT", "aaa111");
            vObj.addRawValue("ZYXDH", "1");
            vObj.addRawValue("POSNR", "");
            vObj.addRawValue("ZTZDNO", "");
            vObj.addRawValue("ZTZDINO", "");
            vObj.addRawValue("SONUM", "");
            vObj.addRawValue("SOITEMNUM", "");
            vObj.addRawValue("TYPE", "");
            vObj.addRawValue("ITEMOPT", "");
            vObj.addRawValue("OPTNUM", "");
            vObj.save();
            return true;
        }).subscribe(it -> {
            System.out.println(it);
        }, err -> {
            err.printStackTrace();
        });
        TimeUnit.DAYS.sleep(1);
    }
}
