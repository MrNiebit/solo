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
package org.b3log.solo.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.Session;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.TreeHole;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: gitsilence
 * @description:
 * @date: 2021/6/26 11:15 上午
 **/
@Repository
public class TreeHoleRepository extends AbstractRepository {

    private static final Logger LOGGER = LogManager.getLogger(TreeHoleRepository.class);

    /**
     * Constructs a repository with the specified name.
     *
     */
    public TreeHoleRepository() {
        super(TreeHole.hole);
    }

    public JSONObject getRandom (Session session) throws ServiceException {
        try {
            String ids = session.getAttribute(Common.SESSION_LIST_KEY);
            if (null == ids) {
                ids = "\"\"";
            }
            if (ids.endsWith(",")) {
                ids = ids.substring(0, ids.lastIndexOf(','));
            }
            String sql = "SELECT * FROM b3_solo_hole WHERE createTime not in ( %s ) ORDER BY RAND() LIMIT 10";
            List<JSONObject> select = super.select(String.format(sql, ids));
            if (null == select || select.size() == 0) {
//                ids = "''";
                List<JSONObject> results = super.select(String.format(sql, ids));
                if (null == results || results.size() == 0) {
                    return new JSONObject();
                }
                JSONObject jsonObject = results.get(0);
                session.setAttribute(Common.SESSION_LIST_KEY, ids + ",'" + jsonObject.optString(TreeHole.createTime) + "\",");
                return results.get(0);
            }
            JSONObject jsonObject = select.get(0);
            session.setAttribute(Common.SESSION_LIST_KEY, ids + ",\"" + jsonObject.optString(TreeHole.createTime) + "\",");
            return select.get(0);
        } catch (Exception e) {
            LOGGER.error("get random message failed ..");
            throw new ServiceException(e);
        }
    }

//    @Override
//    public List<JSONObject> getRandomly(int fetchSize) throws RepositoryException {
//        String sql = "SELECT * FROM b3_solo_hole WHERE createTime >=" +
//                " ( (SELECT MAX(createTime) FROM b3_solo_hole) - (SELECT MIN(createTime) FROM b3_solo_hole ) )" +
//                " * RAND() + (SELECT MIN(createTime) FROM b3_solo_hole) order by RAND() LIMIT " + fetchSize;
//        return super.select(sql);
//    }

    @Override
    public List<JSONObject> getRandomly(int fetchSize) throws RepositoryException {
        final List<JSONObject> ret = new ArrayList<>();
        int loops = 0;
        do {
            loops++;
            double min = Math.random();
            double max = Math.random();
            if (min > max) {
                final double tmp = min;
                min = max;
                max = tmp;
            }
            final Query query = new Query().setFilter(
                    CompositeFilterOperator.and(
                            new PropertyFilter(TreeHole.hole_random_double, FilterOperator.GREATER_THAN_OR_EQUAL, min),
                            new PropertyFilter(TreeHole.hole_random_double, FilterOperator.LESS_THAN_OR_EQUAL, max),
                            new PropertyFilter(TreeHole.isShow, FilterOperator.EQUAL, 1))).
                    setPage(1, (int) Math.ceil(fetchSize / 5)).setPageCount(1);
            if (0.05 <= min) {
                query.addSort(Keys.OBJECT_ID, SortDirection.DESCENDING);
            }

            final List<JSONObject> records = getList(query);
            for (final JSONObject record : records) {
                final String id = record.optString(Keys.OBJECT_ID);
                boolean contain = false;
                for (final JSONObject retRecord : ret) {
                    if (retRecord.optString(Keys.OBJECT_ID).equals(id)) {
                        contain = true;
                        break;
                    }
                }
                if (!contain) {
                    ret.add(record);
                }
            }
        } while (1 <= fetchSize - ret.size() && 10 >= loops);
        return ret;
    }

    public long countByIp(long todayDate, String realIp) {
        Query query = new Query()
                .setFilter(CompositeFilterOperator.and(
                        new PropertyFilter(TreeHole.createTime, FilterOperator.GREATER_THAN_OR_EQUAL, todayDate),
                        new PropertyFilter(TreeHole.clientIp, FilterOperator.EQUAL, realIp)
                ));
        try {
            return count(query);
        } catch (RepositoryException e) {
            e.printStackTrace();
            return Common.MAX_MES_DAY_COUNT;
        }
    }

    public void updateCountById(String column, String id) throws RepositoryException {
        JSONObject jsonObject = get(id);
        if (jsonObject == null) {
            throw new RepositoryException();
        }
        long count = jsonObject.optLong(column) + 1;
        jsonObject.put(column, count);
        update(id, jsonObject, column);
    }
}
