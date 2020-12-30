package com.cjz.webmvc.job;

import com.cjz.webmvc.user.service.UserComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-20 14:06
 */
@Slf4j
@Component
public class JobTask {

    private final UserComponent userComponent;

    public JobTask(UserComponent userComponent) {
        this.userComponent = userComponent;
    }

    @Scheduled(cron = "*/5 * * * * ?")
    public void pushListUser() {
        userComponent.pushListUser();
    }
}
