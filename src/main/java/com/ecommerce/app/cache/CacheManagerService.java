package com.ecommerce.app.cache;

import java.util.List;


public interface CacheManagerService {

    void clearCacheByName(String key);

    void clearAllCachedKeys();

    Object getCache(String key);

    List<String> getAllCachedKeys();

}