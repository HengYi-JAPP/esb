package test;

import loa.biz.LOAApp;
import loa.biz.LOAFormDataObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2019-10-10
 */
@Slf4j
public class LoaTest {
    @SneakyThrows
    public static void main(String[] args) {
        final byte[] bytes = Base64.getDecoder().decode("cm9vdDEyMzQ");
        System.out.println(new String(bytes));


        LOAApp vApp = LOAApp.getInstance();
        vApp.init("http://wms.hengyi.com:8400/10001/openapi/1.0", "e7cdbb3b-7f9f-42bd-910c-f4091c6b12a2", "360057f7-9295-4e77-afcd-aea3384906cf", false);

        Mono.fromCallable(() -> {
            vApp.login("hywsc", "123456");
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
