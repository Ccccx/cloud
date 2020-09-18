package com.example.factory.support;

import com.example.factory.project.BaseProjectRequestEvent;
import com.example.factory.support.model.InitializrMetadata;
import com.example.factory.vo.BaseRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 11:18
 */
@Slf4j
public class ChildFinishEvent extends BaseProjectRequestEvent {
	public ChildFinishEvent(BaseRequest request, InitializrMetadata metadata) {
		super(request, metadata);
	}
}
