package com.cjz.webmvc.liquibase.support;

import lombok.Data;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-08 11:41
 */
@Data
public class DbConf {
    private String url;
    private String user;
    private String password;
    private String driver;
}
