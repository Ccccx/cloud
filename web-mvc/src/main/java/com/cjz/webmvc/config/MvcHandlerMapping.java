package com.cjz.webmvc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * @author chengjz
 * @version 1.0
 * @since 2021-05-26 11:01
 */
@Slf4j
@Component
public class MvcHandlerMapping implements HandlerMapping {
    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        log.info("MvcHandlerMapping ...");
        final LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        log.info("LocaleContext : {}", localeContext);
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        log.info("RequestAttributes : {}", requestAttributes);
        final WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
        log.info("WebAsyncManager : {}", asyncManager);
        return null;
    }
}
