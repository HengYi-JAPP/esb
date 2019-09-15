package oa;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

/**
 * @author jzb 2019-09-12
 */
public class SoapActionInInterceptor extends AbstractSoapInterceptor {
    public SoapActionInInterceptor() {
        super(Phase.RECEIVE);
//        super(Phase.READ);
//        addAfter(ReadHeadersInterceptor.class.getName());
//        addAfter(EndpointSelectionInterceptor.class.getName());
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        System.out.println(message);
    }
}
