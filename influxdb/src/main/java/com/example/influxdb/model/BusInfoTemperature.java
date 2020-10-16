package com.example.influxdb.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.Instant;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-27 15:51
 */
@Data
@ApiModel("车辆历史信息")
@Measurement(name = "busInfoV2")
public class BusInfoTemperature {
	@Column(tag = true)
	@ApiModelProperty("机组编号")
	private String machineNo;
	@ApiModelProperty("车辆编号")
	@Column(tag = true)
	private String carNo;
	@ApiModelProperty("车牌号")
	@Column(tag = true)
	private String carNumber;
	@ApiModelProperty("定位时间")
	@Column(timestamp = true, name = "time")
	private Instant siteTime;
	@ApiModelProperty("温度")
	private Double temperature;
	@ApiModelProperty("湿度")
	private Double humidity;
}
