package oa;

import com.fasterxml.jackson.databind.JsonNode;
import com.hengyi.japp.esb.oa.soap.BasicDataService.BasicDataService;
import com.hengyi.japp.esb.oa.soap.BasicDataService.BasicDataServicePortType;
import lombok.SneakyThrows;

import static com.github.ixtf.japp.core.Constant.MAPPER;

/**
 * @author jzb 2019-08-02
 */
public class BasicDataServiceTest {
    private static final BasicDataServicePortType basicDataServicePortType = new BasicDataService().getBasicDataServiceHttpPort();

    @SneakyThrows
    public static void main(String[] args) {
        final String data = basicDataServicePortType.getHrmresourceData("");
        final JsonNode jsonNode = MAPPER.readTree(data);
        System.out.println(jsonNode);
    }
}
