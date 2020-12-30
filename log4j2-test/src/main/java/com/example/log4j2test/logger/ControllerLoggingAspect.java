package com.example.log4j2test.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 控制器日志切面，用于设置trace_id和request_id
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-12-23 15:21
 */
@Aspect
@Slf4j
public class ControllerLoggingAspect {


    @Autowired(required = false)
    private ObjectMapper objectMapper;


    public static boolean startsWithAny(String text, String... prefixes) {
        if (text == null || prefixes == null) {
            return false;
        }
        for (String prefix : prefixes) {
            if (text.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    @PostConstruct
    public void init() {
        // 对非SpringBoot工程作兼容
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
    }

    /**
     * 统一设置织入点，针对所有HTTP请求
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.GetMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void pointCuts() {
        // Nothing to do.
    }

    private void before() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        // 提取请求对象中的字段信息
        processRequest(request);

        // 设置请求URI
        LoggerHelper.putIfAbsent(LogFieldName.REQUEST_URI, request.getRequestURI());

        // 设置request_id，针对HTTP请求入口
        LoggerHelper.putIfAbsent(LogFieldName.REQUEST_ID, System.nanoTime() + "");

    }

    private void after() {
        MDC.clear();
    }

    private void inputArgs(Signature signature, Object[] args) {
        if (Objects.isNull(signature) || Objects.isNull(args) || args.length == 0) {
            return;
        }

        final Logger log = LoggerFactory.getLogger(signature.getDeclaringType());
        LoggerHelper.putIfAbsent(LogFieldName.INTERCEPT_CLASS, signature.getDeclaringType().toString());

        final List<InputArguments> list = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            // 空值，只记录序号
            if (Objects.isNull(arg)) {
                list.add(new InputArguments(i, Object.class, "NULL"));
            }
            // 系统类、框架类（后期需要持续补充），只记录类型，不记录值
            else if (startsWithAny(arg.getClass().getName(), "org.springframework", "org.apache", "javax.servlet", "com.alibaba")) {
                list.add(new InputArguments(i, arg.getClass(), "IGNORE"));
            }
            // 其它类型作为请求参数对待，对其进行JSON序列化
            else {
                paramToJson(log, list, i, arg);
            }
        }

        // 填充参数字段
        LoggerHelper.put(LogFieldName.METHOD_NAME, signature.getName(), LogFieldName.LOG_TYPE, "input", LogFieldName.SOURCE, "fx");
        list.forEach(arg -> {
            LoggerHelper.put(String.format("arg_%d_type", arg.getNumber()), arg.getType() != null ? arg.getType().getName() : null);
            LoggerHelper.put(String.format("arg_%d_value", arg.getNumber()), arg.getValue());
        });

        // 记录输入参数日志
        log.info("请求日志");

        // 删除参数字段
        LoggerHelper.remove(LogFieldName.METHOD_NAME, LogFieldName.LOG_TYPE, LogFieldName.SOURCE);
        list.forEach(arg -> LoggerHelper.remove(
                String.format("arg_%d_type", arg.getNumber()),
                String.format("arg_%d_value", arg.getNumber()))
        );
    }

    private void paramToJson(Logger log, List<InputArguments> list, int i, Object arg) {
        // 根据参数类型序列化参数
        // 基本类型（含String类型）直接使用 #toString() 转换为字符串
        if (LoggerHelper.isPrimitive(arg.getClass()) || !(arg instanceof Serializable)) {
            list.add(new InputArguments(i, arg.getClass(), arg.toString()));
        } else {
            // 其它类型，使用JSON序列化
            try {
                list.add(new InputArguments(i, arg.getClass(), objectMapper.writeValueAsString(arg)));
            } catch (JsonProcessingException e) {
                list.add(new InputArguments(i, arg.getClass(), e.getMessage()));
                log.warn("对第{}个请求参数JSON序列化时出错!", i + 1, e);
            }
        }
    }

    private void outputArgs(Signature signature, Object result, long elapsed) {
        if (signature == null) {
            return;
        }

        final Logger log = LoggerFactory.getLogger(signature.getDeclaringType());


        // 判断类型，序列化结果
        if (result == null) {
            LoggerHelper.put(LogFieldName.RESPONSE_BODY, "NULL");
        } else if (LoggerHelper.isPrimitive(result.getClass()) || !(result instanceof Serializable)) {
            LoggerHelper.put(LogFieldName.RESPONSE_BODY, result.toString());
        } else {
            try {
                LoggerHelper.put(LogFieldName.RESPONSE_BODY, objectMapper.writeValueAsString(result));
            } catch (JsonProcessingException e) {
                LoggerHelper.put(LogFieldName.RESPONSE_BODY, e.getMessage());
                log.warn("对响应结果JSON序列化时出错!", e);
            }
        }

        // 获取响应状态码
        final Optional<RequestAttributes> requestAttributes = Optional.ofNullable(RequestContextHolder.getRequestAttributes());
        if (requestAttributes.isPresent()) {
            HttpServletResponse response = ((ServletRequestAttributes) requestAttributes.get()).getResponse();
            if (Objects.nonNull(response)) {
                LoggerHelper.put("http_status", String.valueOf(response.getStatus()));
            }
        }

        LoggerHelper.put(LogFieldName.METHOD_NAME, signature.getName(),
                "elapsed_ms", String.valueOf(elapsed),
                LogFieldName.LOG_TYPE, "output", LogFieldName.SOURCE, "fx");
        // 记录日志
        log.info("响应日志");
        // 清除自定义字段
        LoggerHelper.remove(LogFieldName.METHOD_NAME, "elapsed_ms",
                LogFieldName.LOG_TYPE, LogFieldName.SOURCE,
                LogFieldName.RESPONSE_BODY, "response_status", "http_status");
    }

    private void caughtError(Signature signature, Throwable t) {
        if (signature == null) {
            return;
        }

        final Logger log = LoggerFactory.getLogger(signature.getDeclaringType());
        LoggerHelper.put("method_name", signature.getName(),
                "log_type", "exception", "source", "fx");
        log.error("请求返回异常", t);
        LoggerHelper.remove("method_name", "log_type", "source");

    }

    @Around("pointCuts()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 请求前
        before();
        try {
            // 输入参数
            inputArgs(pjp.getSignature(), pjp.getArgs());
            long begin = System.currentTimeMillis();
            // 执行请求
            Object result = pjp.proceed();
            // 输出参数
            outputArgs(pjp.getSignature(), result, System.currentTimeMillis() - begin);
            return result;
        } catch (Throwable t) {
            caughtError(pjp.getSignature(), t);
            throw t;
        } finally {
            // 请求后清除之前的MDC设置，Tomcat可能使用了线程池，避免多次请求间数据干扰
            after();
        }

    }

    /**
     * 抽取request对象中的信息
     *
     * @param request 当前请求
     */
    private void processRequest(HttpServletRequest request) {
        // 从请求消息头中获取 trace_id
        LoggerHelper.putIfAbsent(LogFieldName.TRACE_ID, request.getHeader(LogFieldName.TRACE_ID_HEADER));
        // 该应用CODE为上游请求APP_CODE，需要与当前应用名命名区分开
        LoggerHelper.putIfAbsent(LogFieldName.UPSTREAM_APP_CODE, request.getHeader(LogFieldName.APP_CODE_HEADER));

        LoggerHelper.putIfAbsent(LogFieldName.REQUEST_METHOD, request.getMethod());
        // 当前请求登录态Token信息
        String loginToken = request.getHeader(LogFieldName.LOGIN_TOKEN_HEADER);
        if (loginToken != null) {
            // 提取当前登录用户Id和手机号
            LoggerHelper.putIfAbsent(LogFieldName.LOGIN_TOKEN_HEADER, loginToken);

        }
    }
}
