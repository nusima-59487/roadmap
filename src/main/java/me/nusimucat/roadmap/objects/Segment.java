package me.nusimucat.roadmap.objects;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.nusimucat.roadmap.Editor;
import me.nusimucat.roadmap.Roadmap;
import me.nusimucat.roadmap.database.DBMethods;

public class Segment {
    private int segmentId; // null if not in database (NO CONNECTIONS)
    private int startNodeId; 
    private int endNodeId; 
    private boolean isOneWay; 
    private int laneCountForward; 
    private int laneCountBackward; 
    private int speedLimit; 
    private String roadType; 
    private Timestamp createTime; 
    private UUID lastUpdateUserUUID; 
    private Timestamp lastUpdateTime; 

    private boolean isInDatabase = false; 
    private boolean isSyncToDatabase = false; 
    private Editor activeEditor; 

    private Segment (int startNodeId, int endNodeId, boolean isOneWay, int laneCountForward, int laneCountBackward, int speedLimit, String roadType, Timestamp createTime, UUID lastUpdateUserUuid, Timestamp lastUpdateTime) {
        this.createTime = createTime; 
        this.endNodeId = endNodeId; 
        this.isOneWay = isOneWay; 
        this.laneCountBackward = laneCountBackward; 
        this.laneCountForward = laneCountForward; 
        this.lastUpdateTime = lastUpdateTime; 
        this.lastUpdateUserUUID = lastUpdateUserUuid; 
        this.roadType = roadType; 
        this.speedLimit = speedLimit; 
        this.startNodeId = startNodeId; 
    }
    
    public static Segment fromDatabase (int segmentId) {
        HashMap<String, Object> segmentInfo; 
        try {
            segmentInfo = DBMethods.getSegmentInfo(segmentId); 
        } catch (SQLException e) {
            Roadmap.getLoggerInstance().warn("Failed to get segment info from database");
            Roadmap.getLoggerInstance().warn(e.getMessage()); 
            return null; 
        }

        Segment segmentToReturn = new Segment(
            (int) segmentInfo.get("starting_node"), 
            (int) segmentInfo.get("ending_node"), 
            (boolean) segmentInfo.get("is_one_way"), 
            (int) segmentInfo.get("lane_count_forward"), 
            (int) segmentInfo.get("lane_count_backward"), 
            (int) segmentInfo.get("speed_limit"), 
            (String) segmentInfo.get("road_type"), 
            (Timestamp) segmentInfo.get("create_time"), 
            (UUID) segmentInfo.get("last_update_user_uuid"), 
            (Timestamp) segmentInfo.get("last_update_time")
        ); 
        segmentToReturn.segmentId = segmentId; 
        segmentToReturn.isInDatabase = true; 
        segmentToReturn.isSyncToDatabase = true; 
        return segmentToReturn;
    }

    public static Segment newSegment (Node startingNode, Node endingNode, Editor editor) {
        Segment toReturn = new Segment(startingNode.getId(), endingNode.getId(), false, 2, 2, 50, "Road", Timestamp.valueOf(LocalDateTime.now()), editor.getPlayer().getUniqueId(), Timestamp.valueOf(LocalDateTime.now())); 
        toReturn.activeEditor = editor; 
        return toReturn;
    }

    public int getId() {return this.segmentId;}    
}
