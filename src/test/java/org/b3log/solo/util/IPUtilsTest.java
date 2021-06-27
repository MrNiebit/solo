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
