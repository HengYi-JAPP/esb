package com.hengyi.japp.esb.oa.application;

import com.google.inject.ImplementedBy;
import com.hengyi.japp.esb.oa.application.intenal.OaRestServiceImpl;
import io.vertx.ext.web.RoutingContext;

/**
 * @author jzb 2019-09-27
 */
@ImplementedBy(OaRestServiceImpl.class)
public interface OaRestService {
    void handler(RoutingContext rc);
}
