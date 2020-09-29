package com.example.influxdb.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-27 15:51
 */
@Data
@ApiModel("车辆历史信息")
@Measurement(name = "busInfoV2")
public class BusInfo {
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
	@ApiModelProperty("经度")
	private BigDecimal lng;
	@ApiModelProperty("纬度")
	private BigDecimal lat;
	@ApiModelProperty("速度")
	private BigDecimal velocity;
	@ApiModelProperty("方位角")
	private Integer orientation;
	@ApiModelProperty("状态")
	private Integer state;
	@ApiModelProperty("油量")
	private BigDecimal oilVol;
	@ApiModelProperty("开关门状态")
	private Integer openDoor;
	@ApiModelProperty("温度")
	private BigDecimal temperature;
	@ApiModelProperty("湿度")
	private BigDecimal humidity;
}
