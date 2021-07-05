# 启动顺序
## 配置/注册中心
```
账户 nacos
密码 nacos
docker-compose -f  ./nacos-docker/example/standalone-mysql-8.yaml up
```
## 限流面板
客户端正确配置并启动后，会在初次调用后主动向控制台发送心跳包，汇报自己的存在； 控制台收到客户端心跳包之后，会在左侧导航栏中显示该客户端信息。如果控制台能够看到客户端的机器信息，则表明客户端接入成功了。
```
账户 sentinel
密码 sentinel
com.alibaba.csp.sentinel.dashboard.DashboardApplication
```

## 网关
gateway模块
```
com.cjz.gateway.GatewayApplication
```

## web应用
``` 
com.cjz.webmvc.WebMvcApplication
```

