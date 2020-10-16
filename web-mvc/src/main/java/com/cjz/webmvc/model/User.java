package com.cjz.webmvc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-10-13 19:03
 */
@Data
@AllArgsConstructor
public class User {
	private String username;
	private String token;
}