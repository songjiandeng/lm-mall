package com.macro.mall.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Desc 腾讯云短信模版枚举类型
 *
 * @author Mintimate
 */
@Getter
@AllArgsConstructor
public enum SmsTemplateEnum {
    /**
     * 短信登录
     */
    REGISTER("1901811", "注册验证");

    private final String templateID;
    private final String templateDesc;
}
