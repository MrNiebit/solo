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
package org.b3log.solo.model;

/**
 * @author: gitsilence
 * @description: 树洞 字段
 * @date: 2021/6/26 10:27 上午
 **/
public class TreeHole {


    /**
     * table name suffix
     */
    public static final String hole = "hole";

    /**
     * 留言内容
     */
    public static final String content = "content";

    /**
     * 客户端IP
     */
    public static final String clientIp = "clientIp";

    /**
     * 客户端IP所在位置
     */
    public static final String location = "location";

    /**
     * 创建时间
     */
    public static final String createTime = "createTime";

    /**
     * 是否展示 1：展示；0：隐藏
     */
    public static final String isShow = "isShow";

    /**
     * 点赞数量
     */
    public static final String likeCount = "likeCount";

    /**
     * 被查看的数量
     */
    public static final String watchedCount = "watchedCount";

    /**
     * Key of random double.
     */
    public static final String hole_random_double = "holeRandomDouble";


    private TreeHole () {}

}
