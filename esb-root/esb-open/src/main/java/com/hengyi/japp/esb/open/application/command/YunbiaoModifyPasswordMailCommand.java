package com.hengyi.japp.esb.open.application.command;

import lombok.Data;

/**
 * @author jzb 2020-01-10
 */
@Data
public class YunbiaoModifyPasswordMailCommand {
    private String loginId;
    private String to;
}
