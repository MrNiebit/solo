package org.b3log.solo.processor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    public static final Set<String> WATCHED_SET = Collections.synchronizedSet(new HashSet<>());

    public static final Set<String> LIKED_SET = Collections.synchronizedSet(new HashSet<>());


    public boolean checkIsAllowAdd (String content, String realIp) {
        LocalDate now = LocalDate.now();
        LOGGER.info("Today date is {}", now.toString());
        // 获取当天日期的时间戳 13位的
        long todayDate = now.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
        long count = treeHoleService.countByIp (todayDate, realIp);
        return count < Common.MAX_MES_DAY_COUNT;
    }

    public void addMessage (final RequestContext context) {
        setCrossOrigin(context.getResponse());
        JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        JSONObject ret = new JSONObject();
        if ("options".equalsIgnoreCase(context.getRequest().getMethod())) {
            renderer.setJSONObject(ret);
            return;
        }
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

    public void addLike (final RequestContext context) {
        setCrossOrigin(context.getResponse());
        JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        String realIp = IPUtils.getRealIp(context.getRequest());
        String id = context.pathVar(Keys.OBJECT_ID);
        String likedKey = realIp + id;
        if (LIKED_SET.contains(likedKey)){
            JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            jsonObject.put(Keys.MSG, "已经点过赞了");
            renderer.setJSONObject(jsonObject);
            return;
        }
        try {
            LIKED_SET.add(likedKey);
            JSONObject ret = new JSONObject();
            // http://xxxx.com/a/b/c/{oId} 获取oId的参数值
            treeHoleService.updateCountById(TreeHole.likeCount, id);
            ret.put(Keys.OBJECT_ID, id);
            ret.put(Keys.MSG, langPropsService.get("addSuccLabel"));
            ret.put(Keys.CODE, StatusCodes.SUCC);
            renderer.setJSONObject(ret);
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject().put(Keys.CODE, StatusCodes.ERR);
            jsonObject.put(Keys.MSG, e.getMessage());
            renderer.setJSONObject(jsonObject);
        }
    }

    public void getRandomMessage (final RequestContext context) {
        Response response = context.getResponse();
        setCrossOrigin(response);
        JsonRenderer renderer = new JsonRenderer();
        context.setRenderer(renderer);
        JSONObject jsonObject = new JSONObject();
        JSONObject ret = getRandomly (IPUtils.getRealIp(context.getRequest()));
        jsonObject.put(Common.RANDOM_TREE_HOLE, ret);
        renderer.setJSONObject(jsonObject);
    }

    public JSONObject getRandomly (String clientIp) {
        try {
            JSONObject randomMessage = treeHoleService.getRandomMessage();
            String id = randomMessage.optString(Keys.OBJECT_ID);
            String watchedKey = clientIp + id;
            if (!WATCHED_SET.contains(watchedKey)) {
                treeHoleService.updateCountById(TreeHole.watchedCount, randomMessage.optString(Keys.OBJECT_ID));
                WATCHED_SET.add(watchedKey);
            }
            randomMessage.remove(TreeHole.hole_random_double);
            randomMessage.remove(TreeHole.createTime);
            randomMessage.remove(TreeHole.isShow);
            randomMessage.remove(TreeHole.location);
            randomMessage.remove(TreeHole.clientIp);
            return randomMessage;
        } catch (ServiceException e) {
            LOGGER.error("get Randomly , {}", e.getMessage());
            return new JSONObject();
        }
    }

    public void setCrossOrigin (Response response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        // 解决post请求跨域，因为 解决POST请求跨域，会先发送一个options请求
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
    }
}
