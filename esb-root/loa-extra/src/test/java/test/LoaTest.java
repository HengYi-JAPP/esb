package test;

import com.fasterxml.jackson.databind.JsonNode;
import com.hengyi.japp.esb.sap.callback.LoaGuiceModule;
import io.vertx.core.Vertx;
import loa.biz.LOAApp;
import loa.biz.LOAFormDataObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.github.ixtf.japp.core.Constant.MAPPER;

/**
 * @author jzb 2019-10-10
 */
@Slf4j
public class LoaTest {
    @SneakyThrows
    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        LoaGuiceModule.init(vertx);
        final LOAApp app = LoaGuiceModule.getInstance(LOAApp.class);

        String json = "{\"tables\":{\"IT_ORDER_IN\":[{\"VBELN\":\"\",\"VGBEL\":\"\",\"WERKS\":\"5560\",\"BRATEFEEAMT\":0.000,\"SOITEMNUM\":\"1\",\"ITEMOPT\":\"ADD\",\"FIORDER\":\"0\",\"OPT\":\"CRE\",\"ZZBEIZ\":\"\",\"VKBUR\":\"0000\",\"CUS_FLAG\":\"0\",\"TARGET_QTY\":30.000,\"VTWEG\":\"10\",\"MATNR\":\"000000001000500264\",\"KUNNR\":\"0000327866\",\"SALESNUM\":\"12000662\",\"RATEAMT\":0.000,\"CUSTOMERTAB\":\"\",\"ZYXDH\":\"1910130261\",\"PRICELIST\":\"\",\"MEINS\":\"KG\",\"VOLUM\":1.000,\"OPTNUM\":\"yb00005358\",\"VKGRP\":\"000\",\"VKORG\":\"5500\",\"BRATEAMT\":0.000,\"POSNR\":\"000000\",\"AUART\":\"ZTA1\",\"SALESNM\":\"???\",\"BSTKD\":\"\",\"VOLEH\":\"BOX\",\"ZTZDNO\":\"1910130261\",\"AUDAT\":1570982400000,\"ZTZDINO\":\"1\",\"ABGRU\":\"\",\"SONUM\":\"yb00005358\",\"VSTEL\":\"5520\",\"VGPOS\":\"000000\"}],\"IT_PRICE_IN\":[{\"CVALUE\":17.050000000,\"CTYPE\":\"ZPR0\",\"CPUNT\":1.00,\"SONUM\":\"yb00005358\",\"SOITEMNUM\":\"1\"},{\"CVALUE\":0E-9,\"CTYPE\":\"Z030\",\"CPUNT\":1.00,\"SONUM\":\"yb00005358\",\"SOITEMNUM\":\"1\"},{\"CVALUE\":0E-9,\"CTYPE\":\"Z018\",\"CPUNT\":1.00,\"SONUM\":\"yb00005358\",\"SOITEMNUM\":\"1\"},{\"CVALUE\":0E-9,\"CTYPE\":\"Z800\",\"CPUNT\":1.00,\"SONUM\":\"yb00005358\",\"SOITEMNUM\":\"1\"},{\"CVALUE\":0E-9,\"CTYPE\":\"Z004\",\"CPUNT\":1.00,\"SONUM\":\"yb00005358\",\"SOITEMNUM\":\"1\"}],\"ET_ORDER_RE\":[{\"MSG\":\"\",\"VBELN\":\"0011041383\",\"OPT\":\"CRE\",\"ZYXDH\":\"1910130261\",\"POSNR\":\"000010\",\"ZTZDNO\":\"1910130261\",\"ZTZDINO\":\"1\",\"SONUM\":\"yb00005358\",\"SOITEMNUM\":\"1\",\"TYPE\":\"S\",\"ITEMOPT\":\"ADD\",\"OPTNUM\":\"yb00005358\"}]},\"imports\":{},\"exports\":{},\"changings\":{}}";
        final JsonNode node = MAPPER.readTree(json);
        final JsonNode ET_ORDER_RE = node.get("tables").get("ET_ORDER_RE");

        for (JsonNode row : ET_ORDER_RE) {
            LOAFormDataObject vObj = app.newFormDataObject("ZRFC_SD_YX_003回调表");
            vObj.addRawValue("TRANSID", UUID.randomUUID().toString());
            vObj.addRawValue("VBELN", row.get("VBELN").asText());
            vObj.addRawValue("OPT", row.get("OPT").asText());
            vObj.addRawValue("ZYXDH", row.get("ZYXDH").asText());
            vObj.addRawValue("POSNR", row.get("POSNR").asText());
            vObj.addRawValue("ZTZDNO", row.get("ZTZDNO").asText());
            vObj.addRawValue("ZTZDINO", row.get("ZTZDINO").asText());
            vObj.addRawValue("SONUM", row.get("SONUM").asText());
            vObj.addRawValue("SOITEMNUM", row.get("SOITEMNUM").asText());
            vObj.addRawValue("TYPE", row.get("TYPE").asText());
            vObj.addRawValue("ITEMOPT", row.get("ITEMOPT").asText());
            vObj.addRawValue("OPTNUM", row.get("OPTNUM").asText());
            vObj.save();
        }

//        Mono.fromCallable(() -> {
//            final LOAApp vApp = LoaGuiceModule.getInstance(LOAApp.class);
//            LOAFormDataObject vObj = vApp.newFormDataObject("ZRFC_SD_YX_003回调表");
//            vObj.addRawValue("TRANSID", UUID.randomUUID().toString());
//            vObj.addRawValue("VBELN", "aaa111");
//            vObj.addRawValue("OPT", "aaa111");
//            vObj.addRawValue("ZYXDH", "1");
//            vObj.addRawValue("POSNR", "");
//            vObj.addRawValue("ZTZDNO", "");
//            vObj.addRawValue("ZTZDINO", "");
//            vObj.addRawValue("SONUM", "");
//            vObj.addRawValue("SOITEMNUM", "");
//            vObj.addRawValue("TYPE", "");
//            vObj.addRawValue("ITEMOPT", "");
//            vObj.addRawValue("OPTNUM", "");
//            vObj.save();
//            return true;
//        }).subscribe(it -> {
//            System.out.println(it);
//        }, err -> {
//            err.printStackTrace();
//        });
        TimeUnit.DAYS.sleep(1);
    }
}
