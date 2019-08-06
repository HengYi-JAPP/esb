package com.hengyi.japp.esb.auth.application;

/**
 * @author jzb 2018-03-18
 */
public interface AuthService {
    String auth(String id, String password) throws Exception;
}
