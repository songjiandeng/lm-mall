package com.macro.mall.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author songjd
 * @description: 身份认证工具类
 * @date 2023/9/13
 */
public class IdentityAuthUtil {

    private final static String KEY_IDENTITY_AUTH = "c106c2fb8c8d909c07e9dfb2e9d65858";

    private final static String KEY_IMAGES = "d4613784db16696ef9f4ac0874cca563";

    private final static String KEY_REAL = "d654e614a0aedff28d80929a450711e1";

    private final static String URL_IDENTITY_AUTH = "https://api.chinadatapay.com/communication/personal/1882";

    private final static String URL_IMAGES = "https://api.chinadatapay.com/communication/personal/2061";


    /**
     * 实名认证
     */

    public static String identityAuth(String name, String idCard) {

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("idcard", idCard);
        params.put("key", KEY_IDENTITY_AUTH);

        return HttpUtil.invokePostWithMap(URL_IDENTITY_AUTH, params);
    }


    /**
     * 人像对比
     *
     * @param name
     * @param idCard
     * @return
     */
    public static String identityAuth(String name, String idCard, String imageId) {

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("idcard", idCard);
        params.put("key", KEY_IMAGES);
        params.put("imageId", imageId);

        return HttpUtil.invokePostWithMap(URL_IDENTITY_AUTH, params);
    }

}
