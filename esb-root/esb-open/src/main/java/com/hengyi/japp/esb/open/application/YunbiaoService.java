package com.hengyi.japp.esb.open.application;

import com.google.inject.ImplementedBy;
import com.hengyi.japp.esb.open.application.command.YunbiaoModifyPasswordCommand;
import com.hengyi.japp.esb.open.application.command.YunbiaoModifyPasswordMailCommand;
import com.hengyi.japp.esb.open.application.internal.YunbiaoServiceImpl;
import reactor.core.publisher.Mono;

/**
 * @author jzb 2020-01-10
 */
@ImplementedBy(YunbiaoServiceImpl.class)
public interface YunbiaoService {
    Mono<Void> handle(YunbiaoModifyPasswordMailCommand command);

    Mono<Void> handle(YunbiaoModifyPasswordCommand command);
}
