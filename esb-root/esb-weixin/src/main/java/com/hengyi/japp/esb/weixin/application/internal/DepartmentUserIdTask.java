package com.hengyi.japp.esb.weixin.application.internal;

import org.jzb.weixin.work.AgentClient;
import org.jzb.weixin.work.contact.AbstractUser;
import org.jzb.weixin.work.contact.UserSimpleListResponse;

import java.util.concurrent.RecursiveTask;
import java.util.stream.Stream;

/**
 * 描述：
 *
 * @author jzb 2018-04-29
 */
public class DepartmentUserIdTask extends RecursiveTask<Stream<String>> {
    private final AgentClient agentClient;
    private final long department_id;

    public DepartmentUserIdTask(AgentClient agentClient, long department_id) {
        this.agentClient = agentClient;
        this.department_id = department_id;
    }

    @Override
    protected Stream<String> compute() {
        try {
            final UserSimpleListResponse res = agentClient.userSimpleList()
                    .department_id(department_id)
                    .fetch_child(true)
                    .call();
            if (!res.isSuccessed()) {
                throw new RuntimeException(res.errmsg());
            }
            return res.userlist().map(AbstractUser::userid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
