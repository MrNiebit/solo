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
