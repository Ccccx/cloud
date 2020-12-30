package com.cjz.webmvc.lambda;

import lombok.Data;
import org.springframework.stereotype.Indexed;

import java.util.Date;

/**
 * @author chengjinzhou
 * @version 1.0
 * @date 2019-05-16 16:36
 */
@Indexed
@Data
public class TestBean {
    private Long id;
    private String userId;
    private Long strategyId;
    private String appCode;
    private Long activityId;
    private Byte voucherStatus;
    private String voucherCode;
    private Integer amount;
    private Long produceOrderId;
    private Long consumeOrderId;
    private Date startTime;
    private Date endTime;
    private Byte deleted;
    private Date createTime;
    private Date updateTime;
}
