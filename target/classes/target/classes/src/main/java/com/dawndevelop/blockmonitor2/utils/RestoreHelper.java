package com.dawndevelop.blockmonitor2.utils;

import com.dawndevelop.blockmonitor2.api.DataContainerHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static com.dawndevelop.blockmonitor2.BlockMonitor.storageHandler;

/**
 * Created by johnfg10 on 13/06/2017.
 */
public class RestoreHelper {

    public static Text restoreArea(Location<World> worldLocation, int restoreDiameter){
        try {
            String sql = "SELECT * FROM `blockmonitor` WHERE (`worldName` = ?) AND (`locationX` <= ?) AND (`locationX` >= ?) AND (`locationZ` <= ?) AND (`locationZ` >= ?);";
            Connection connection = storageHandler.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, worldLocation.getExtent().getName());
            preparedStatement.setInt(2, worldLocation.getBlockX() + restoreDiameter);
            preparedStatement.setInt(3, worldLocation.getBlockX() - restoreDiameter);
            preparedStatement.setInt(4, worldLocation.getBlockZ() + restoreDiameter);
            preparedStatement.setDouble(5, worldLocation.getBlockZ() - restoreDiameter);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()){
                Optional<DataContainer> dataContainerOptional = DataContainerHelper.getDataContainerFromString(resultSet.getString("datacontainer"));
                if (dataContainerOptional.isPresent()) {
                    DataContainer dataContainer = dataContainerOptional.get();
                    if (dataContainer.contains(DataQuery.of("BlockOriginal"))){
                        Optional<DataView> dataViewOptional = dataContainer.getView(DataQuery.of("BlockOriginal"));
                        if (dataViewOptional.isPresent()){
                            DataView dataView = dataViewOptional.get();
                            Optional<BlockSnapshot> blockSnapshotOptional = Sponge.getDataManager().deserialize(BlockSnapshot.class, dataView);
                            if (blockSnapshotOptional.isPresent()){
                                BlockSnapshot blockSnapshot = blockSnapshotOptional.get();
                                blockSnapshot.restore(true, BlockChangeFlag.NONE);
                                PreparedStatement removeRecord = connection.prepareStatement("DELETE FROM `blockmonitor` WHERE ID = ?");
                                removeRecord.setInt(1, resultSet.getInt("id"));
                                removeRecord.execute();
                            }
                        }
                    }
                }
            }

            
            return Text.builder().color(TextColors.GREEN).append(Text.of("Restore successful")).build();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Text.builder().color(TextColors.RED).append(Text.of("Restore Failed")).build();
    }
}
