package com.cjz.webmvc.liquibase.controller;


import com.cjz.webmvc.liquibase.service.ChangelogManagerComponent;
import com.cjz.webmvc.liquibase.support.TableInfo;
import com.cjz.webmvc.liquibase.utils.ChangeLogGeneratorUtils;
import com.cjz.webmvc.liquibase.utils.ConfigBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * web方式访问工具类
 *
 * @author chengjz
 * @version 1.0
 * @since 2020-09-03 14:44
 */
@RestController
@RequestMapping("/changelog")
@Api(tags = "liubase逆向工程测试", value = "liubase逆向生成测试")
@Slf4j
public class ChangeLogController {

	private final ChangelogManagerComponent component;

	public ChangeLogController(ChangelogManagerComponent component) {
		this.component = component;
	}

	@ApiOperation(value = "通过编程方式配置,生成更新SQL,不自动执行", hidden = true)
	@GetMapping("/params/updateSQL")
	public String paramsUpdateSQL() {
		final String sql = ChangeLogGeneratorUtils.executor(ConfigBuilder.paramsConf()
				.changeLogFile("m1-onlineform-changelog.xml")
				.driver("com.mysql.cj.jdbc.Driver")
				.url("jdbc:mysql://192.168.240.185:3306/demo_cjz?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8")
				.username("root")
				.password("tmkj@zgb123")
				.commandConf()
				.updateSQL().builder());
		log.info("同步SQL为: \n  " + sql);
		return sql;
	}

	@ApiOperation(value = "配置文件方式,生成更新SQL,不自动执行", hidden = true)
	@GetMapping("/propertis/updateSQL")
	public String propertisUpdateSql() {
		final String sql = ChangeLogGeneratorUtils.executor(ConfigBuilder.propertiesConf()
				.changeLogFile("m1-onlineform-changelog.xml")
				.commandConf()
				.updateSQL().builder());
		log.info("同步SQL为: \n  " + sql);
		return sql;
	}

	@ApiOperation(value = "根据changlog同步数据库", hidden = true)
	@GetMapping("/propertis/update")
	public void propertisUpdate() {
		ChangeLogGeneratorUtils.executor(ConfigBuilder.propertiesConf().changeLogFile("m1-onlineform-changelog.xml").commandConf()
				.update().customThenBuilder(map -> map.put("logLevel", "debug")));
	}

	@ApiOperation(value = "根据数据库生成changlog", hidden = true)
	@GetMapping("/propertis/generator")
	public void propertisGenerator() {
		ChangeLogGeneratorUtils.executor(ConfigBuilder.propertiesConf().changeLogFile("new-changelog.xml").commandConf()
				.generator().customThenBuilder(map -> map.put("logLevel", "debug")));
	}

	/**
	 * 使用其他指令, 参考地址: https://docs.liquibase.com/commands/community/home.html
	 * 这里用的是dropAll, 删除表
	 */
	@ApiOperation(value = "删除表", hidden = true)
	@GetMapping("/propertis/dropAll")
	public void propertisDropAll() {
		ChangeLogGeneratorUtils.executor(ConfigBuilder.propertiesConf().changeLogFile("new-changelog.xml").commandConf()
				.command("dropAll").customThenBuilder(map -> map.put("logLevel", "debug")));
	}

	@ApiOperation("查询生成表的changelog记录")
	@GetMapping(value = "/xml/{tableName}", produces = "application/xml;charset=UTF-8")
	public String tableChnagelog(@PathVariable("tableName") @ApiParam(value = "表名称", type = "path") String tableName) {
		return component.tableChnagelog(tableName);
	}

	@ApiOperation("创建表")
	@PostMapping(value = "/createTable", produces = "application/xml;charset=UTF-8")
	public String createTable(@RequestBody @ApiParam TableInfo info) {
		return component.createOrUpdateTable(info);
	}
}
