package org.b3log.solo.service;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.*;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Ids;
import org.b3log.solo.model.TreeHole;
import org.b3log.solo.repository.TreeHoleRepository;
import org.json.JSONObject;


/**
 * @author: gitsilence
 * @description: 树洞业务逻辑层
 * @date: 2021/6/26 10:45 上午
 **/
@Service
public class TreeHoleService {

    private static final Logger LOGGER = LogManager.getLogger(TreeHoleService.class);

    @Inject
    private TreeHoleRepository treeHoleRepository;

    public String addMessage(JSONObject requestJSON) throws ServiceException {
        Transaction transaction = treeHoleRepository.beginTransaction();
        try {
            JSONObject treeHole = requestJSON.getJSONObject(TreeHole.hole);
            String ret = treeHole.optString(Keys.OBJECT_ID);
            /* 为空的字段 设置默认值 */
            // 设置id
            if (StringUtils.isBlank(ret)) {
                ret = Ids.genTimeMillisId();
                treeHole.put(Keys.OBJECT_ID, ret);
            }
            // 创建时间
            treeHole.put(TreeHole.createTime, System.currentTimeMillis());
            // 是否展示，默认展示
            treeHole.put(TreeHole.isShow, 1);
            // 点赞数
            treeHole.put(TreeHole.likeCount, 0);
            // 随机数
            treeHole.put(TreeHole.hole_random_double, Math.random());
            treeHoleRepository.add(treeHole);
            transaction.commit();
            return ret;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new ServiceException(e.getMessage());
        }
    }

    public JSONObject getRandomMessage() throws ServiceException {
        return treeHoleRepository.getRandom();
    }

    public long countByIp(long todayDate, String realIp) {
        return treeHoleRepository.countByIp (todayDate, realIp);
    }
}
