package com.tgcity.profession.network.greendao;

import com.tgcity.profession.network.greendao.model.HttpRequestLog;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.Map;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * @author TGCity
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig httpRequestLogDaoConfig;

    private final HttpRequestLogDao httpRequestLogDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        httpRequestLogDaoConfig = daoConfigMap.get(HttpRequestLogDao.class).clone();
        httpRequestLogDaoConfig.initIdentityScope(type);

        httpRequestLogDao = new HttpRequestLogDao(httpRequestLogDaoConfig, this);

        registerDao(HttpRequestLog.class, httpRequestLogDao);
    }
    
    public void clear() {
        httpRequestLogDaoConfig.clearIdentityScope();
    }

    public HttpRequestLogDao getHttpRequestLogDao() {
        return httpRequestLogDao;
    }

}
