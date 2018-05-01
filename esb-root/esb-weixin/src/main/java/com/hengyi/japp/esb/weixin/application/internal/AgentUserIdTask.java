package com.hengyi.japp.esb.weixin.application.internal;

import org.jzb.weixin.work.AgentClient;
import org.jzb.weixin.work.agent.AgentGetResponse;
import org.jzb.weixin.work.contact.AbstractUser;

import java.util.concurrent.RecursiveTask;
import java.util.stream.Stream;

/**
 * 获取微信应用中可见用户的员工号
 *
 * @author jzb 2018-04-29
 */
public class AgentUserIdTask extends RecursiveTask<Stream<String>> {
    private final AgentClient agentClient;

    public AgentUserIdTask(AgentClient agentClient) {
        this.agentClient = agentClient;
    }

    @Override
    protected Stream<String> compute() {
        try {
            final AgentGetResponse agentInfo = agentClient.agentGet().call();
            Stream<String> stream = agentInfo.allow_userinfos().map(AbstractUser::userid);

            final Stream<String> stream1 = agentInfo.allow_partys()
                    .map(it -> new DepartmentUserIdTask(agentClient, it))
                    .peek(RecursiveTask::fork)
                    .flatMap(RecursiveTask::join);
            stream = Stream.concat(stream, stream1);

            final Stream<String> stream2 = agentInfo.allow_tags()
                    .map(it -> new TagUserIdTask(agentClient, it))
                    .peek(RecursiveTask::fork)
                    .flatMap(RecursiveTask::join);
            stream = Stream.concat(stream, stream2);

            return stream;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
