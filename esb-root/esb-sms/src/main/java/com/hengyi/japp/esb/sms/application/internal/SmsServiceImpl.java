package com.hengyi.japp.esb.sms.application.internal;

import cn.com.flaginfo.ws.SmsPortType;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.sms.application.SmsService;
import com.hengyi.japp.esb.sms.dto.SmsSendDTO;
import com.hengyi.japp.esb.sms.dto.SmsSendResponseDTO;
import io.reactivex.Single;

import java.util.Properties;

/**
 * 描述：
 *
 * @author jzb 2018-03-22
 */
public class SmsServiceImpl implements SmsService {
    private final Properties smsConfig;
    private final SmsPortType smsPortType;

    @Inject
    SmsServiceImpl(@Named("smsConfig") Properties smsConfig, SmsPortType smsPortType) {
        this.smsConfig = smsConfig;
        this.smsPortType = smsPortType;
    }

    @Override
    public Single<SmsSendResponseDTO> send(SmsSendDTO command) {
        final String SpCode = smsConfig.getProperty("SpCode");
        final String LoginName = smsConfig.getProperty("LoginName");
        final String Password = smsConfig.getProperty("Password");
        final String res = smsPortType.sms(SpCode, LoginName, Password, command.getContent(), command.getPhonesAsString(), command.getSerialNumber(), command.getScheduleTime(), command.getSendCheckType(), null, null, null);
        return Single.just(new SmsSendResponseDTO(res));
    }

}
