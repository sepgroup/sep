package com.sepgroup.sep.db;

import org.aeonbits.owner.Config;

/**
 * Created by jeremybrown on 2016-05-20.
 */
@Config.Sources({"classpath:db.properties"})
public interface DBConfig extends Config {
    String activeDbPath();
}
