package me.nusimucat.roadmap.objects;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.nusimucat.roadmap.Roadmap;
import me.nusimucat.roadmap.database.DBMethods;

public class Segment extends Element {
    private int startNodeId; 
    private int endNodeId; 
    private int yIndex; 
    private boolean isOneWay; 
    private int laneCountForward; 
    private int laneCountBackward; 
    private int speedLimit; 
    private String roadType; 
    private ArrayList<Node> alignmentAuxNodes; 

    private Segment (
        int startNodeId, 
        int endNodeId, 
        int yIndex, 
        String name, 
        boolean isOneWay, 
        int laneCountForward, 
        int laneCountBackward, 
        int speedLimit, 
        String roadType, 
        Timestamp createTime, 
        UUID lastUpdateUserUuid, 
        Timestamp lastUpdateTime
    ) {
        super(name, createTime, lastUpdateTime, lastUpdateUserUuid); 
        this.startNodeId = startNodeId; 
        this.endNodeId = endNodeId; 
        this.yIndex = yIndex; 
        this.isOneWay = isOneWay; 
        this.laneCountBackward = laneCountBackward; 
        this.laneCountForward = laneCountForward; 
        this.roadType = roadType; 
        this.speedLimit = speedLimit; 
    }
    
    /** Constructor from Database */
    public static Segment fromDatabase (int segmentId) {
        HashMap<String, Object> segmentInfo; 
        try {
            segmentInfo = DBMethods.getSegmentInfo(segmentId); 
        } catch (SQLException e) {
            Roadmap.getLoggerInstance().warn("Failed to get segment info from database");
            Roadmap.getLoggerInstance().warn(e.getMessage()); 
            return null; 
        }

        // TODO: Import alignmentAuxNodes from database
        Segment segmentToReturn = new Segment(
            (int) segmentInfo.get("starting_node"), 
            (int) segmentInfo.get("ending_node"), 
            (int) segmentInfo.get("y_index"), 
            (String) segmentInfo.get("name"), 
            (boolean) segmentInfo.get("is_one_way"), 
            (int) segmentInfo.get("lane_count_forward"), 
            (int) segmentInfo.get("lane_count_backward"), 
            (int) segmentInfo.get("speed_limit"), 
            (String) segmentInfo.get("road_type"), 
            (Timestamp) segmentInfo.get("create_time"), 
            (UUID) segmentInfo.get("last_update_user_uuid"), 
            (Timestamp) segmentInfo.get("last_update_time")
        ); 
        segmentToReturn.id = segmentId; 
        segmentToReturn.isInDatabase = true; 
        segmentToReturn.isSyncToDatabase = true; 
        return segmentToReturn;
    }

    public static Segment simpleConstruct (Node startingNode, Node endingNode, Editor editor) {
        // TODO: get default from editor
        Segment toReturn = new Segment(
            startingNode.getId(), 
            endingNode.getId(), 
            10, 
            "Unnamed Road", 
            false, 
            2, 
            2, 
            50, 
            "Road", 
            Timestamp.valueOf(LocalDateTime.now()), 
            editor == null ? null : editor.getPlayer().getUniqueId(), 
            Timestamp.valueOf(LocalDateTime.now())
        ); 
        return toReturn;
    }

    @Override
    public void updateToDatabase() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateToDatabase'");
    }

    public Node getStartingNode () {
        return Node.fromDatabase(this.startNodeId); 
    }
    public Node getEndingNode () {
        return Node.fromDatabase(this.endNodeId); 
    }
    // public void setStartingNode (Node startingNode, Editor editor) {
    //     this.startNodeId = startingNode.getId(); 
    //     this.updateHistory(editor);
    // }
    // public void setEndingNode (Node endingNode, Editor editor) {
    //     this.endNodeId = endingNode.getId(); 
    //     this.updateHistory(editor);
    // }

    public int getYIndex () {
        return this.yIndex; 
    }
    public void setYIndex (int yIndex, Editor editor) {
        this.yIndex = yIndex; 
        this.updateHistory(editor);
    }

    public boolean isOneWay () {
        return this.isOneWay; 
    }
    public void setOneWay (boolean oneWay, Editor editor) {
        this.isOneWay = oneWay; 
        this.updateHistory(editor);
    }

    public int getLaneCountForward () {
        return this.laneCountForward; 
    }
    public int getLaneCountBackward () {
        // one way check?
        return this.laneCountBackward; 
    }
    public void setLaneCountForward (int laneCount, Editor editor) {
        this.laneCountForward = laneCount; 
        this.updateHistory(editor);
    }
    public void setLaneCountBackward (int laneCount, Editor editor) {
        // one way check?
        this.laneCountBackward = laneCount; 
        this.updateHistory(editor);
    }
    public void setLaneCount (int laneCount, Editor editor) {
        this.laneCountBackward = laneCount; 
        this.laneCountForward = laneCount; 
        this.updateHistory(editor);
    }

    public int getSpeedLimit () {
        return this.speedLimit; 
    }
    public void setSpeedLimit (int speed, Editor editor) {
        this.speedLimit = speed; 
        this.updateHistory(editor);
    }

    public String getRoadType () {
        return this.roadType; 
    }
    public void setRoadType (String roadType, Editor editor) {
        this.roadType = roadType; 
        this.updateHistory(editor);
    }

    public ArrayList<Node> getAuxNodes () {
        return this.alignmentAuxNodes; 
    }
    public void appendAuxNode (Node node, int alignmentIndex, Editor editor) {
        this.alignmentAuxNodes.add(alignmentIndex, node);
        this.updateHistory(editor);
    }
    public void removeAllAuxNodes (Editor editor) {
        this.alignmentAuxNodes.clear();
        this.updateHistory(editor);
    }

    /**
     * Applies styles from another segment to this segment
     * Changes includes: 
     * - {@link Element#name}
     * - {@link Segment#yIndex}
     * - {@link Segment#isOneWay}
     * - {@link Segment#laneCountForward}
     * - {@link Segment#laneCountBackward}
     * - {@link Segment#speedLimit}
     * - {@link Segment#roadType}
     * @param srcNode - source node
     */
    public void stylePainter (Segment srcSegment, Editor editor) {
        this.setName(srcSegment.getName(), editor);
        this.setYIndex(srcSegment.getYIndex(), editor);
        this.setOneWay(srcSegment.isOneWay(), editor);
        this.setLaneCountForward(srcSegment.getLaneCountForward(), editor);
        this.setLaneCountBackward(srcSegment.getLaneCountBackward(), editor);
        this.setSpeedLimit(srcSegment.getSpeedLimit(), editor);
    }

    @Override
    public void renderTo(Player player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'renderTo'");
    }

    @Override
    public void renderTo(Editor editor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'renderTo'");
    }
}
