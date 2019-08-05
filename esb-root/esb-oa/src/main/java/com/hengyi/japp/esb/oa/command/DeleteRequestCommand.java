package com.hengyi.japp.esb.oa.command;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jzb 2019-08-02
 */
@Data
public class DeleteRequestCommand implements Serializable {
    private int userid;
    private int requestid;
}
