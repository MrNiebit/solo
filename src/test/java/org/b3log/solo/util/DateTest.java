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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author: gitsilence
 * @description:
 * @date: 2021/6/26 4:27 下午
 **/
public class DateTest {

    @Test
    public void testDate () {
        LocalDate now = LocalDate.now();
        System.out.println(now);
        System.out.println(now.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli());
        LocalDateTime time = LocalDateTime.now();
        System.out.println(time);
        System.out.println(time.atZone(ZoneOffset.ofHours(8)).toInstant().toEpochMilli());
        LocalDateTime today = LocalDateTime.parse("2021-06-26 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(today.toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
    }

}
