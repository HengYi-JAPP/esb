package oa;

import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowRequestInfo;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowService;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowServicePortType;
import lombok.SneakyThrows;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

import static com.github.ixtf.japp.core.Constant.MAPPER;

/**
 * @author jzb 2019-08-02
 */
public class WorkflowServiceTest {
    private static final WorkflowServicePortType workflowServicePortType = new WorkflowService().getWorkflowServiceHttpPort();

    @SneakyThrows
    public static void main(String[] args) {
        final int requestid = 2393937;
        final int userid = 615;
        final WorkflowRequestInfo workflowRequestInfo = workflowServicePortType.getWorkflowRequest(requestid, userid, requestid);

        final JAXBContext jaxbContext = JAXBContext.newInstance(WorkflowRequestInfo.class);
        final Marshaller marshaller = jaxbContext.createMarshaller();
        final StringWriter stringWriter = new StringWriter();
        marshaller.marshal(workflowRequestInfo, stringWriter);
        final String s = stringWriter.toString();
        System.out.println(s);

        System.out.println(MAPPER.writeValueAsString(workflowRequestInfo));

//        final String s = workflowServiceHttpPort.doCreateWorkflowRequest(workflowRequestInfo, 615);
//        System.out.println(s);
    }
}
