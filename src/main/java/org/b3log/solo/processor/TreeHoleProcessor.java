package org.b3log.solo.processor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.JsonRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.TreeHole;
import org.b3log.solo.service.TreeHoleService;
import org.b3log.solo.util.IPUtils;
import org.b3log.solo.util.StatusCodes;
import org.json.JSONObject;

import java.time.LocalDate;

import java.time.ZoneOffset;


/**
 * @author: gitsilence
 * @description: 树洞
 * @date: 2021/6/26 10:24 上午
 **/
@Singleton
public class TreeHoleProcessor {

    @Inject
    private TreeHoleService treeHoleService;

    @Inject
    private LangPropsService langPropsService;

    private static final Logger LOGGER = LogManager.getLogger(TreeHoleProcessor.class);

    public boolean checkIsAllowAdd (String content, String realIp) {
        LocalDate now = LocalDate.now();
        LOGGER.info("Today date is {}", now.toString());
        // 获取当天日期的时间戳 13位的
        long todayDate = now.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
        long count = treeHoleService.countByIp (todayDate, realIp);
        return count < Common.MAX_MES_DAY_COUNT;
    }

    public void addMessage (final RequestContext context) {
        JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        JSONObject ret = new JSONObject();
        // 客户端IP
        String realIp = IPUtils.getRealIp(context.getRequest());
        JSONObject requestJSON = context.requestJSON();
        JSONObject jsonObject = requestJSON.getJSONObject(TreeHole.hole);
        if (!checkIsAllowAdd(jsonObject.optString(TreeHole.content), realIp)) {
            LOGGER.warn("the current IP {} can't be allowed to invoke...", realIp);
            ret.put(Keys.CODE, StatusCodes.ERR);
            ret.put(Keys.MSG, "not allowed");
            renderer.setJSONObject(ret);
            return;
        }
        try {
            LOGGER.log(Level.INFO, "add Message, request body: [ " + requestJSON.toString() + " ]");
            jsonObject.put(TreeHole.clientIp, realIp);
            jsonObject.put(TreeHole.location, IPUtils.getRegion(realIp));
            String messageId = treeHoleService.addMessage (requestJSON);
            ret.put(Keys.OBJECT_ID, messageId);
            ret.put(Keys.MSG, langPropsService.get("addSuccLabel"));
            ret.put(Keys.CODE, StatusCodes.SUCC);

            renderer.setJSONObject(ret);
        } catch (Exception e) {
            jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }

    public void getRandomMessage (final RequestContext context) {
        JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        JSONObject jsonObject = new JSONObject();
        JSONObject ret = getRandomly ();
        jsonObject.put(Common.RANDOM_TREE_HOLE, ret);
        renderer.setJSONObject(jsonObject);
    }

    public JSONObject getRandomly () {
        try {
            JSONObject randomMessage = treeHoleService.getRandomMessage();
            randomMessage.remove(TreeHole.hole_random_double);
            randomMessage.remove(TreeHole.createTime);
            randomMessage.remove(TreeHole.isShow);
            randomMessage.remove(TreeHole.location);
            randomMessage.remove(Keys.OBJECT_ID);
            randomMessage.remove(TreeHole.clientIp);
            return randomMessage;
        } catch (ServiceException e) {
            LOGGER.error("get Randomly , {}", e.getMessage());
            return new JSONObject();
        }
    }
}
