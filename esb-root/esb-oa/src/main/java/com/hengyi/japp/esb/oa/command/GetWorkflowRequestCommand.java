package com.hengyi.japp.esb.oa.command;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jzb 2019-08-02
 */
@Data
public class GetWorkflowRequestCommand implements Serializable {
    private int requestid;
    private int userid;
    private int fromrequestid;
}
