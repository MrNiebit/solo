/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.solo.util;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.http.Request;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbMakerConfigException;
import org.lionsoul.ip2region.DbSearcher;

import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * @author: gitsilence
 * @description:
 * @date: 2021/6/26 1:34 下午
 **/
public class IPUtils {

    private static final Logger LOGGER = LogManager.getLogger(IPUtils.class);

    private static DbSearcher dbSearcher = null;

    private static final String UNKNOWN = "unknown";


    private IPUtils () {}

    static {
        String path = IPUtils.class.getResource("/files/ip2region.db").getPath();
        LOGGER.log(Level.INFO, "file path is ---------------->>>>>>  " + path);
        try {
            dbSearcher = new DbSearcher(new DbConfig(), path);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        } catch (DbMakerConfigException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
    }

    public static String getRegion (String ip) {
        if (StringUtils.isBlank(ip)) {
            LOGGER.info("The ip is empty ...");
            return "";
        }
        LOGGER.info("current ip is --->>> {}", ip);
        try {
            DataBlock dataBlock = dbSearcher.btreeSearch(ip);
            return ipFilter(dataBlock.getRegion());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("get region exception : ", e);
        }
        return "";
    }

    public static String ipFilter (String region) {
        return Optional.ofNullable(region)
                .orElse("").replace("0", "")
                .replaceAll("\\|+", "-");
    }

    public static String getRealIp (Request request) {
        String ip = "";
        try {
            ip = request.getHeader("X-forwarded-for");
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            LOGGER.error("IPUtils ERROR ", e);
        }
        // 使用代理，则获取第一个ip地址
        if (StringUtils.isNotEmpty(ip) && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

}
