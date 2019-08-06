package oa;

import com.hengyi.japp.esb.oa.soap.HrmService.ArrayOfSubCompanyBean;
import com.hengyi.japp.esb.oa.soap.HrmService.HrmService;
import com.hengyi.japp.esb.oa.soap.HrmService.HrmServicePortType;

/**
 * @author jzb 2019-08-02
 */
public class HrmServiceTest {
    private static final HrmServicePortType hrmServiceHttpPort = new HrmService().getHrmServiceHttpPort();

    public static void main(String[] args) {
        final ArrayOfSubCompanyBean arrayOfSubCompanyBean = hrmServiceHttpPort.getHrmSubcompanyInfo("localhost");
        System.out.println(arrayOfSubCompanyBean);

//        final Tracer tracer = GuiceOaModule.Tracer();
//        final Span span = tracer.buildSpan("test").start();
//        span.setTag(Tags.ERROR, false);
//        span.finish();
    }
}
