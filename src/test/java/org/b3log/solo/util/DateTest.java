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
