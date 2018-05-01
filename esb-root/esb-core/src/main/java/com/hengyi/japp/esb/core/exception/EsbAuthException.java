package com.hengyi.japp.esb.core.exception;

/**
 * 描述：
 *
 * @author jzb 2018-03-19
 */
public class EsbAuthException extends EsbException {
    public static EsbAuthException noSubject() {
        return new EsbAuthException();
    }

    public static EsbAuthException noIssuer() {
        return new EsbAuthException();
    }

    public static EsbAuthException wrongIssuer() {
        return new EsbAuthException();
    }

    public static TokenExpiredException tokenExpired() {
        return new TokenExpiredException();
    }
}
