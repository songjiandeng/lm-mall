package com.macro.mall.common.enums;

public enum ConstantTypesEnum {
    ;

    //订单操作：-1 查看订单详情, 0->待审核；1->待支付；2->代发货；3->待收货；4->租用中；5->逾期；6->订单完成；7->订单关闭 10-> 查看历史订单 8->线下归还 9->订单备注  11->物流信息 12-> 账单信息 13 ->审核拒绝 14 ->审核通过 15 ->订单签收 16->订单创建
    public enum OrderStatus {
        NOTE_DETAIL(-2,"备注详情"),
        DETAIL(-1,"订单详情"),
        PROCESS(0, "待审核"),
        PAYOFF(1, "待支付"),
        CONSIGNMENT(2, "代发货"),
        RECEIVED(3, "待收货"),
        USED(4, "租用中"),
        DELAY(5, "逾期"),
        COMPLETE(6, "订单完成"),
        CLOSE(7, "订单关闭"),
        RETURN(8, "线下归还"),
        NOTE(9, "订单备注"),
        HISTORY(10, "查看历史订单"),
        LOGISTICS(11, "物流信息"),
        BILL(12, "账单信息"),
        REFUSE(13, "审核拒绝"),
        PASS(14, "审核通过"),
        SIGN(15, "订单签收"),
        CREATE(16,"订单创建"),
        COLLECTION(17,"催收记录");

        OrderStatus(int value, String desc) {
            this.desc = desc;
            this.value = value;
        }

        public static OrderStatus getByType(int type) {
            for (OrderStatus value : OrderStatus.values()) {
                if (value.getValue() == type) {
                    return value;
                }
            }
            return null;
        }

        private int value;
        private String desc;

        public int getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum CycleUnit{
        DAY(2,"1"),
        WEEK(1,"7"),
        MONTH(0,"30");

        CycleUnit(int value, String desc) {
            this.desc = desc;
            this.value = value;
        }

        public static CycleUnit getByType(int type) {
            for (CycleUnit value : CycleUnit.values()) {
                if (value.getValue() == type) {
                    return value;
                }
            }
            return null;
        }

        private int value;
        private String desc;

        public int getValue() {
            return value;
        }

        public String getDesc() {
            return desc;
        }
    }
}
