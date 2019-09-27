package com.hengyi.japp.esb.auth.application;

import com.google.inject.ImplementedBy;
import com.hengyi.japp.esb.auth.application.internal.AuthServiceImpl;

/**
 * @author jzb 2018-03-18
 */
@ImplementedBy(AuthServiceImpl.class)
public interface AuthService {
    String auth(String id, String password);
}
