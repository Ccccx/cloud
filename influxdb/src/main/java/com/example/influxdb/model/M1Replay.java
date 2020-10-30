package com.example.influxdb.model;


import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;

/**
 * @author Chen
 */
@Setter
@Getter
@ApiModel(description = "轨迹回放")
@Measurement(name = "replays")
public class M1Replay {

	@Column(tag = true)
	String id;
	@Column(tag = true)
	String line;

	@Column(name = "lon")
	@ApiModelProperty(required = true, value = "经度")
	Double lng;
	@Column
	@ApiModelProperty(required = true, value = "纬度")
	Double lat;
	@Column
	@ApiModelProperty(required = true, value = "速度")
	Integer speed;
	@Column
	@ApiModelProperty(required = true, value = "方位角")
	Integer direction;
	@ApiModelProperty(required = true, value = "定位时间")
	@Column(timestamp = true)
	Instant instant;

	public Date getTime() {
		return new Date(instant.toEpochMilli());
	}
}