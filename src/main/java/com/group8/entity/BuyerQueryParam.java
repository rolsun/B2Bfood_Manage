package com.group8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class BuyerQueryParam {
    private Integer page;
    private Integer limit;
    private String keyword;
    private Integer categoryId;

    //无参构造方法
    public BuyerQueryParam() {}

    //全参构造方法
    public BuyerQueryParam(Integer page, Integer limit, String keyword, Integer categoryId) {}

}
