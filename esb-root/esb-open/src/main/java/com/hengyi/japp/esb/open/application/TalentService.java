package com.hengyi.japp.esb.open.application;

import com.google.inject.ImplementedBy;
import com.hengyi.japp.esb.open.application.internal.TalentServiceImpl;
import io.vertx.core.Future;
import io.vertx.ext.auth.User;

/**
 * @author jzb 2018-03-18
 */
@ImplementedBy(TalentServiceImpl.class)
public interface TalentService {

    Future<String> autoLoginUrl(User user);
}
