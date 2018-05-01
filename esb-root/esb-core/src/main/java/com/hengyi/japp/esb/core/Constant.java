package com.hengyi.japp.esb.core;

/**
 * 描述：
 *
 * @author jzb 2018-03-18
 */
public class Constant {
    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String TEXT_CONTENT_TYPE = "text/plain";

    public static final class JWT {
        public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwchwDQFwWeVV7uYogEiKIYh9KtYTC9oz3Jjp20btpvIyzVltu8dcUIJlm/ilgLAtT8809HnK/7+yp3CwHst3YpjKvR2CiS3l1mJ1btbQjigdp14Fy/Kb/Qs4+8F1MJyTJlUgThf9CjAZ5H+bRciH/vmJSD2SQ8kzie/VKk2DE6TP13zsAgNh8suXXy0a3H42qS6BFrOq59gLRx8oeQgdDF4bmliHYUW8PVoqFvYgQKHF2SUt321x+MXWErYBkp0nsjDhj4HKnQq5TRDubWQjaQtVSGqRjsTmcLu1iKTxFSPsXn0+g8gd2fPq5j2dAYkgXxL+V5QZsHGxBjrubk2E6QIDAQAB";
        public static final String ALG = "RS512";
        public static final String ISS = "hengyi-esb";
        public static final int EXPIRES_IN_SECONDS = 7200;
    }

}
