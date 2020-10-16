package com.example.influxdb.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Chen
 */
@Setter
@Getter
@ApiModel(description = "轨迹回放")
public class Replay {

	@ApiModelProperty(required = true, value = "经度")
	double lng;
	@ApiModelProperty(required = true, value = "纬度")
	double lat;
	@ApiModelProperty(required = true, value = "速度")
	int speed;
	@ApiModelProperty(required = true, value = "方位角")
	int direction;
	@ApiModelProperty(required = true, value = "定位时间")
	Date time;
}