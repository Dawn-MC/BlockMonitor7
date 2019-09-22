package com.dawndevelop.blockmonitor7.Storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by johnfg10 on 04/06/2017.
 */
public class StorageHandler {
    public HikariDataSource dataSource;
    //h2
    public StorageHandler(Path folderLocation){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2://" + folderLocation + "/blockmonitor");
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(100);
        dataSource = new HikariDataSource(config);

        try {
            System.out.println("create table : " + dataSource.getConnection().createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS `blockmonitor` (" +
                            "`id` BIGINT AUTO_INCREMENT NOT NULL," +
                            "`locationX` INT," +
                            "`locationY` INT," +
                            "`locationZ` INT," +
                            "`worldName` VARCHAR(500)," +
                            "`eventtype` VARCHAR(255)," +
                            "`datacontainer` LONGTEXT," +
                            "`timestamp` TIMESTAMP" +
                            ");")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*    //mysql
    StorageHandler(String databaseType, String hostname, int port, String schema, String username, String password){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:" + databaseType + "://" + hostname + ":" + port + "/" + schema);
        config.setUsername(username);
        config.setPassword(password);
        dataSource = new HikariDataSource(config);
    }*/

    //this is called when the plugin is being shutdown its main job is to close the datasource down
    public void shutdownHook(){
        dataSource.close();
    }


}