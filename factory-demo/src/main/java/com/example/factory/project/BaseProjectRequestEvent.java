package com.example.factory.project;

import com.example.factory.support.model.InitializrMetadata;
import com.example.factory.vo.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @author chengjz
 * @version 1.0
 * @date 2019-12-09 11:14
 */
@Getter
@Setter
public abstract class BaseProjectRequestEvent extends ApplicationEvent {
	private final BaseRequest request;

	private final InitializrMetadata metadata;

	protected BaseProjectRequestEvent(BaseRequest request, InitializrMetadata metadata) {
		super(request);
		this.request = request;
		this.metadata = metadata;
	}

}
