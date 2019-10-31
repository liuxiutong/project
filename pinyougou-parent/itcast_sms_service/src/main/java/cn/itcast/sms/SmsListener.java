package cn.itcast.sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 消息监听类
 */
@Component
public class SmsListener {
    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "sms")
    public void sendSms(Map<String, String> map) {
        //签名乱码
        String name = map.get("sign_name");
        try {
            String s = new String(name.getBytes("iso-8859-1"), "utf-8");
            map.put("sign_name",s);
            System.out.println(s);
        } catch (UnsupportedEncodingException e) {

        }

        try {
            SendSmsResponse response = smsUtil.sendSms(
                    map.get("mobile"),
                    map.get("template_code"),
                    map.get("sign_name"),
                    map.get("param")
            );
            /**//**/
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());

        } catch (ClientException e) {
            e.printStackTrace();

        }
    }

}
