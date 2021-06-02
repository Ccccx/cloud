package com.cjz.webmvc;

import com.alibaba.fastjson.JSONObject;
import com.cjz.webmvc.utils.TreeFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author chengjz
 * @version 1.0
 * @since 2021-04-09 16:23
 */
@Slf4j
 class TreeTest {

    @Test
    void t1() {
        final List<TreeBean> treeBeans = JSONObject.parseArray(str, TreeBean.class);
        final List<TreeBean> treeList = TreeFactory.build(treeBeans, TreeBean::getID, TreeBean::getPID, TreeBean::setChildren);
        log.info("\n{}", JSONObject.toJSONString(treeList));
    }

    void t2() {
    }

    @Data
    public static class TreeBean {
        private String ID;
        private String PID;
        private String SID;
        private String NAME;
        private String SPID;
        private String RANK;
        private String LASTSTAGE;
        private String TENANTID;
        private List<TreeBean> children;
    }

    String str = "[\n" +
            "{\"ID\":\"101_PAGE_erpMobile.controller\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"erpMobile.controller\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"101_STRATEGY_platform.action\",\"PID\":\"141_PLAN_platform.action\",\"SID\":\"\",\"NAME\":\"四分公司看ABC车队\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"11_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"报表系统管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"121_PAGE_platform.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"platform.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"122_PLAN_platform.action\",\"PID\":\"121_PAGE_platform.action\",\"SID\":\"\",\"NAME\":\"项目版本控制页面\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"128_aq.action\",\"PID\":\"61_STRATEGY_aq.action\",\"SID\":\"\",\"NAME\":\"安全系统管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"154_platform.action\",\"PID\":\"101_STRATEGY_platform.action\",\"SID\":\"\",\"NAME\":\"物资系统管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"168_rlzy.action\",\"PID\":\"43_STRATEGY_rlzy.action\",\"SID\":\"\",\"NAME\":\"职工基础信息\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"16_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"自行车系统开发员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"178_rlzy.action\",\"PID\":\"63_STRATEGY_rlzy.action\",\"SID\":\"\",\"NAME\":\"培训按部门过滤\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"17_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"宇通新能源开发员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"186_rlzy.action\",\"PID\":\"42_STRATEGY_rlzy.action\",\"SID\":\"\",\"NAME\":\"单位过滤\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"18_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"公司管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"19_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"售后管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"1_PAGE_rlzy.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"rlzy.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"1_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"测试部门\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"1_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"系统管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"20_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"区管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"20_right\",\"PID\":\"3_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"区管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"211_aq.action\",\"PID\":\"61_STRATEGY_aq.action\",\"SID\":\"\",\"NAME\":\"安保处\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"212_aq.action\",\"PID\":\"62_STRATEGY_aq.action\",\"SID\":\"\",\"NAME\":\"巴士捷运\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"213_rlzy.action\",\"PID\":\"43_STRATEGY_rlzy.action\",\"SID\":\"\",\"NAME\":\"系统管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"214_rlzy.action\",\"PID\":\"43_STRATEGY_rlzy.action\",\"SID\":\"\",\"NAME\":\"职工档案信息查询\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"21_PAGE_rlzy.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"rlzy.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"21_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"街道管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"21_right\",\"PID\":\"3_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"街道管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"228_rlzy.action\",\"PID\":\"64_STRATEGY_rlzy.action\",\"SID\":\"\",\"NAME\":\"培训管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"22_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"22_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"场站管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"22_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"测试部门\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"2501_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"张明\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"2502_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"李明\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"25_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"项目管理系统角色\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"26_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"郑州市自行车出租公司\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"2_PAGE_rlzy.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"rlzy.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"3_platform.action\",\"PID\":\"81_STRATEGY_platform.action\",\"SID\":\"\",\"NAME\":\"系统管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"3_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"系统管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"3_right\",\"PID\":\"3_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"系统管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"408_platform.action\",\"PID\":\"81_STRATEGY_platform.action\",\"SID\":\"\",\"NAME\":\"平台管理员\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"40_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"40\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"41_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"41_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"41\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"41_right\",\"PID\":\"3_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"41\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"42_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"42_PLAN_rlzy.action\",\"PID\":\"1_PAGE_rlzy.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"42_PLAN_rlzy.action\",\"PID\":\"2_PAGE_rlzy.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"42_STRATEGY_rlzy.action\",\"PID\":\"42_PLAN_rlzy.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"43_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"43_STRATEGY_rlzy.action\",\"PID\":\"42_PLAN_rlzy.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"44_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"45_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"45_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"45\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"46_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"47_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"47_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"47\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"47_right\",\"PID\":\"3_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"47\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"48_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"48_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"zzz\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"49_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"50_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"51_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"52_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"53_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"54_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"55_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"56_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"57_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"58_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"59_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"60_PAGE_aq.action\",\"PID\":\"\",\"SID\":\"\",\"NAME\":\"aq.action\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"22_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"41_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"42_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"43_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"44_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"45_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"46_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"47_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"48_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"49_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"50_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"51_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"52_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"53_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"54_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"55_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"56_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"57_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"58_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"59_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_PLAN_aq.action\",\"PID\":\"60_PAGE_aq.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"61_STRATEGY_aq.action\",\"PID\":\"61_PLAN_aq.action\",\"SID\":\"\",\"NAME\":\"安保处_驾驶员信息\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"62_PLAN_rlzy.action\",\"PID\":\"21_PAGE_rlzy.action\",\"SID\":\"\",\"NAME\":\"\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"62_STRATEGY_aq.action\",\"PID\":\"61_PLAN_aq.action\",\"SID\":\"\",\"NAME\":\"巴士捷运默认单位\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"63_STRATEGY_rlzy.action\",\"PID\":\"62_PLAN_rlzy.action\",\"SID\":\"\",\"NAME\":\"默认部门\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"64_STRATEGY_rlzy.action\",\"PID\":\"62_PLAN_rlzy.action\",\"SID\":\"\",\"NAME\":\"所有权限\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"6_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"4\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"81_STRATEGY_platform.action\",\"PID\":\"122_PLAN_platform.action\",\"SID\":\"\",\"NAME\":\"四分公司\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"82_STRATEGY_platform.action\",\"PID\":\"123_PLAN_platform.action\",\"SID\":\"\",\"NAME\":\"test\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"88010_platform.action\",\"PID\":\"101_STRATEGY_platform.action\",\"SID\":\"\",\"NAME\":\"张舒\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"8_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"天迈科技\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"91035_platform.action\",\"PID\":\"101_STRATEGY_platform.action\",\"SID\":\"\",\"NAME\":\"褚良\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"91035_platform.action\",\"PID\":\"82_STRATEGY_platform.action\",\"SID\":\"\",\"NAME\":\"褚良\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"liuhualiang_right\",\"PID\":\"23_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"刘化亮\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "{\"ID\":\"liuhualiang_right\",\"PID\":\"3_STRATEGY_right\",\"SID\":\"\",\"NAME\":\"刘化亮\",\"SPID\":\"\",\"RANK\":\"0\",\"LASTSTAGE\":\"0\",\"TENANTID\":\"\"},\n" +
            "\n" +
            "]";
}
