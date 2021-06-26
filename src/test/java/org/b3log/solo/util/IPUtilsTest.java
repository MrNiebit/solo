package org.b3log.solo.util;

import org.testng.annotations.Test;

import java.util.Optional;

/**
 * @author: gitsilence
 * @description:
 * @date: 2021/6/26 1:45 下午
 **/
public class IPUtilsTest {


    @Test
    public void ipTest () {
//        String region = IPUtils.getRegion("27.185.22.170");
        String region = null;
        region = Optional.ofNullable(region)
                .orElse("").replace("0", "")
                .replaceAll("\\|+", "-");
//        region = region.replace("0", "")
//                .replaceAll("\\|+", "-");
        System.out.println(region);
    }

}
