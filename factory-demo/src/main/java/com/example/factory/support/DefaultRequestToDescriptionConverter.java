package com.example.factory.support;

import com.example.factory.project.ProjectDescription;
import com.example.factory.project.RequestToDescriptionConverter;
import com.example.factory.project.model.MutableProjectDescription;
import com.example.factory.vo.BaseRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 10:27
 */
@Slf4j
public class DefaultRequestToDescriptionConverter implements RequestToDescriptionConverter<BaseRequest> {
	@Override
	public ProjectDescription convert(BaseRequest request) {
		log.debug("开始转换数据 ...");
		final MutableProjectDescription description = new MutableProjectDescription();
		description.setMsg(request.getMsg() == null ? "未定义消息" : request.getMsg());
		description.setId(System.currentTimeMillis());
		return description;
	}
}
