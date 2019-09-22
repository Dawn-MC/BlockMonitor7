package com.dawndevelop.blockmonitor2.utils;

import com.dawndevelop.blockmonitor2.api.DataContainerHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.DataFormat;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.dawndevelop.blockmonitor2.BlockMonitor.storageHandler;
import static org.spongepowered.api.text.TextTemplate.arg;
import static org.spongepowered.api.text.TextTemplate.of;
/**
 * Created by johnfg10 on 08/06/2017.
 */
public class SearchHelper {
    public static List<Text> searchArea(Location<World> worldLocation, int searchDiameter, Locale locale) throws SQLException {

        String sql = "SELECT * FROM `blockmonitor` WHERE (`worldName` = ?) AND (`locationX` <= ?) AND (`locationX` >= ?) AND (`locationZ` <= ?) AND (`locationZ` >= ?);";
        Connection connection = storageHandler.dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, worldLocation.getExtent().getName());
        preparedStatement.setInt(2, worldLocation.getBlockX() + searchDiameter);
        preparedStatement.setInt(3, worldLocation.getBlockX() - searchDiameter);
        preparedStatement.setInt(4, worldLocation.getBlockZ() + searchDiameter);
        preparedStatement.setDouble(5, worldLocation.getBlockZ() - searchDiameter);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<org.spongepowered.api.text.Text> contents = new ArrayList<>();

        while (resultSet.next()) {
            String info = resultSet.getString("datacontainer");
            try {
                InputStream inputStream = new ByteArrayInputStream(info.getBytes());
                DataFormat dataFormat = DataFormats.JSON;
                DataContainer dataContainer = dataFormat.readFrom(inputStream);
                Text text = formattedText(
                        resultSet.getInt("id"),
                        resultSet.getInt("locationX"),
                        resultSet.getInt("locationY"),
                        resultSet.getInt("locationZ"),
                        resultSet.getString("worldName"),
                        resultSet.getString("eventtype"),
                        dataContainer,
                        resultSet.getTimestamp("timestamp").toLocalDateTime(),
                        locale
                );
                contents.add(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        preparedStatement.close();
        connection.close();
        return contents;
    }

    public static List<Text> searchWorld(Location<World> worldLocation, int searchDiameter, Locale locale) throws SQLException {

        String sql = "SELECT * FROM `blockmonitor` WHERE (`worldName` = ?);";
        Connection connection = storageHandler.dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, worldLocation.getExtent().getName());

        ResultSet resultSet = preparedStatement.executeQuery();
        List<org.spongepowered.api.text.Text> contents = new ArrayList<>();

        while (resultSet.next()) {
            String string = resultSet.getString("datacontainer");
            Optional<DataContainer> dataContainerOptional = DataContainerHelper.getDataContainerFromString(string);
            if (dataContainerOptional.isPresent()){
                DataContainer dataContainer = dataContainerOptional.get();
                Text text = formattedText(
                        resultSet.getInt("id"),
                        resultSet.getInt("locationX"),
                        resultSet.getInt("locationY"),
                        resultSet.getInt("locationZ"),
                        resultSet.getString("worldName"),
                        resultSet.getString("eventtype"),
                        dataContainer,
                        resultSet.getTimestamp("timestamp").toLocalDateTime(),
                        locale
                );
                contents.add(text);
            }
        }

        preparedStatement.close();
        connection.close();
        return contents;
    }

    public static Text formattedText(int id, int locationx, int locationy, int locationz, String worldname, String eventTypeString, DataContainer dataContainer, LocalDateTime eventDateTime, Locale locale) {
        TextTemplate textTemplate = of(
                "ID:",
                arg("ID"),
                " - ",
                arg("eventType"),
                " ",
                "Date: ",
                arg("date"),
                Text.NEW_LINE,
                "Pos: ",
                arg("locx").color(TextColors.RED),
                ", ",
                arg("locy").color(TextColors.BLUE),
                ", ",
                arg("locz").color(TextColors.DARK_GREEN),
                " ",
                arg("cause").optional().color(TextColors.YELLOW),
                " ",
                arg("additionalInfo").optional().color(TextColors.GOLD)
        );

        Map<String, TextElement> textTemplateMap = new TreeMap<>();
        textTemplateMap.put("ID", Text.of(id));
        textTemplateMap.put("eventType", Text.of(eventTypeString));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/LL/YYYY");
        String dateFormatted = eventDateTime.format(formatter);
        textTemplateMap.put("date", Text.of(dateFormatted));
        textTemplateMap.put("locx", Text.of(locationx));
        textTemplateMap.put("locy", Text.of(locationy));
        textTemplateMap.put("locz", Text.of(locationz));

        Text.Builder cause = Text.builder();
        Text.Builder additionalInfo = Text.builder();

        //dataviews
        Optional<DataView> blockOriginalOptional = dataContainer.getView(DataQuery.of("BlockOriginal"));
        Optional<DataView> blockFinalOptional = dataContainer.getView(DataQuery.of("BlockFinal"));
        Optional<DataView> itemOriginalOptional = dataContainer.getView(DataQuery.of("ItemOriginal"));
        Optional<DataView> itemFinalOptional = dataContainer.getView(DataQuery.of("ItemFinal"));

        //logic
        if (blockOriginalOptional.isPresent()) {
            DataView dataView = blockOriginalOptional.get();
            Optional<BlockSnapshot> blockSnapshotOptional = Sponge.getDataManager().deserialize(BlockSnapshot.class, dataView);
            if (blockSnapshotOptional.isPresent()) {
                BlockSnapshot blockSnapshot = blockSnapshotOptional.get();
                additionalInfo.append(Text.of(blockSnapshot.getState().getType().getName())).append(Text.of(" "));
            }
        }

        if (blockFinalOptional.isPresent()) {
            DataView dataView = blockFinalOptional.get();
            Optional<BlockSnapshot> blockSnapshotOptional = Sponge.getDataManager().deserialize(BlockSnapshot.class, dataView);
            if (blockSnapshotOptional.isPresent()) {
                BlockSnapshot blockSnapshot = blockSnapshotOptional.get();
                additionalInfo.append(Text.of(blockSnapshot.getState().getType().getName())).append(Text.of(" "));
            }
        }

        if (itemOriginalOptional.isPresent()) {
            DataView dataView = itemOriginalOptional.get();
            Optional<ItemStackSnapshot> itemStackSnapshotOptional = Sponge.getDataManager().deserialize(ItemStackSnapshot.class, dataView);
            if (itemStackSnapshotOptional.isPresent()) {
                ItemStackSnapshot itemStackSnapshot = itemStackSnapshotOptional.get();
                additionalInfo.append(Text.of(itemStackSnapshot.createStack().getTranslation().get(locale))).append(Text.of(" "));
            }
        }

        if (itemFinalOptional.isPresent()) {
            DataView dataView = itemFinalOptional.get();
            Optional<ItemStackSnapshot> itemStackSnapshotOptional = Sponge.getDataManager().deserialize(ItemStackSnapshot.class, dataView);
            if (itemStackSnapshotOptional.isPresent()) {
                ItemStackSnapshot itemStackSnapshot = itemStackSnapshotOptional.get();
                additionalInfo.append(Text.of(itemStackSnapshot.createStack().getTranslation().get(locale))).append(Text.of(" "));
            }
        }

        if (dataContainer.contains(DataQuery.of("User"))) {
            //add player info
            Optional<User> userOptional = Sponge.getDataManager().deserialize(User.class, dataContainer.getView(DataQuery.of("User")).get());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                cause.append(Text.of(user.getName()));
            }
        } else if (dataContainer.contains(DataQuery.of("Entity"))) {
            //add entity info
            Optional<Entity> entityOptional = Sponge.getDataManager().deserialize(Entity.class, dataContainer.getView(DataQuery.of("Entity")).get());
            if (entityOptional.isPresent()) {
                Entity entity = entityOptional.get();
                cause.append(Text.of(entity.getType().getTranslation().get(locale)));
            }
        }

        textTemplateMap.put("cause", cause);
        textTemplateMap.put("additionalInfo", additionalInfo);
        return textTemplate.apply(textTemplateMap).build();

    }
}
