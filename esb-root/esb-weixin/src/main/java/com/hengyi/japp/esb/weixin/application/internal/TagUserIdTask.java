package com.hengyi.japp.esb.weixin.application.internal;

import org.jzb.weixin.work.AgentClient;
import org.jzb.weixin.work.contact.AbstractUser;
import org.jzb.weixin.work.contact.TagGetResponse;

import java.util.concurrent.RecursiveTask;
import java.util.stream.Stream;

/**
 * 描述：
 *
 * @author jzb 2018-04-29
 */
public class TagUserIdTask extends RecursiveTask<Stream<String>> {
    private final AgentClient agentClient;
    private final long tagid;

    public TagUserIdTask(AgentClient agentClient, long tagid) {
        this.agentClient = agentClient;
        this.tagid = tagid;
    }

    @Override
    protected Stream<String> compute() {
        try {
            final TagGetResponse res = agentClient.tagGet()
                    .tagid(tagid)
                    .call();
            if (!res.isSuccessed()) {
                throw new RuntimeException(res.errmsg());
            }
            Stream<String> stream = res.userlist().map(AbstractUser::userid);

            final Stream<String> stream1 = res.partylist()
                    .map(it -> new DepartmentUserIdTask(agentClient, it))
                    .peek(RecursiveTask::fork)
                    .flatMap(RecursiveTask::join);

            return Stream.concat(stream, stream1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
