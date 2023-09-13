package com.macro.mall.dto;


import lombok.Data;

import java.util.List;

@Data
public class OrderLineData {

    private Integer total;

    private List<String> xLine;

    private List<Integer> yLine;
}
