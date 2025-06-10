package me.nusimucat.roadmap.objects;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.nusimucat.roadmap.database.DBMethods;
import me.nusimucat.roadmap.util.Result;

public class Segment extends Element {
    private int startNodeId; 
    private int endNodeId; 
    private int yIndex; 
    private boolean isOneWay; 
    private int laneCountForward; 
    private int laneCountBackward; 
    private int distance; 
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
        int distance, 
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
        this.distance = distance; 
        this.roadType = roadType; 
        this.speedLimit = speedLimit; 
    }
    
    /** Constructor from Database
     *  @throws Exception if no segments match the segment id
     */
    public static Segment fromDatabase (int segmentId) throws Exception {
        Result<Segment, String> result = DBMethods.selectSegmentFromId(segmentId); 
        Segment toReturn = result.getValueOrThrow(); 
        
        toReturn.id = segmentId; 
        toReturn.isInDatabase = true; 
        toReturn.isSyncToDatabase = true; 
        return toReturn;
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
            0, 
            50, 
            "Road", 
            Timestamp.valueOf(LocalDateTime.now()), 
            editor == null ? null : editor.getPlayer().getUniqueId(), 
            Timestamp.valueOf(LocalDateTime.now())
        ); 
        return toReturn;
    }

    @Override
    public Result<Boolean,String> updateToDatabase (Editor editor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateToDatabase'");
    }

    public Result<Node, String> getStartingNode () {
        try {
            return Result.ok(Node.fromDatabase(this.startNodeId));
        } catch (Exception e) {
            return Result.err(e.toString()); 
        } 
    }
    public Result<Node, String> getEndingNode () {
        try {
            return Result.ok(Node.fromDatabase(this.endNodeId));
        } catch (Exception e) {
            return Result.err(e.toString()); 
        } 
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
    public void setYIndex (int yIndex) {
        this.yIndex = yIndex; 
        this.updateHistory();
    }

    public boolean isOneWay () {
        return this.isOneWay; 
    }
    public void setOneWay (boolean oneWay) {
        this.isOneWay = oneWay; 
        this.updateHistory();
    }

    public int getLaneCountForward () {
        return this.laneCountForward; 
    }
    public int getLaneCountBackward () {
        // TODO: one way check?
        return this.laneCountBackward; 
    }
    public void setLaneCountForward (int laneCount) {
        this.laneCountForward = laneCount; 
        this.updateHistory();
    }
    public void setLaneCountBackward (int laneCount) {
        // one way check?
        this.laneCountBackward = laneCount; 
        this.updateHistory();
    }
    public void setLaneCount (int laneCount) {
        this.laneCountBackward = laneCount; 
        this.laneCountForward = laneCount; 
        this.updateHistory();
    }

    public int getDistance () {
        return this.distance; 
    }
    public void setDistance (int distance) {
        this.distance = distance; 
    }

    public int getSpeedLimit () {
        return this.speedLimit; 
    }
    public void setSpeedLimit (int speed) {
        this.speedLimit = speed; 
        this.updateHistory();
    }

    public String getRoadType () {
        return this.roadType; 
    }
    public void setRoadType (String roadType) {
        this.roadType = roadType; 
        this.updateHistory();
    }

    public ArrayList<Node> getAuxNodes () {
        return this.alignmentAuxNodes; 
    }
    public void appendAuxNode (Node node, int alignmentIndex) {
        this.alignmentAuxNodes.add(alignmentIndex, node);
        this.updateHistory();
    }
    public void removeAllAuxNodes () {
        this.alignmentAuxNodes.clear();
        this.updateHistory();
    }

    /**
     * Applies styles from another segment to this segment 
     * <hr>
     * Changes includes: 
     * <ul>
     *     <li> {@link Element#name}
     *     <li> {@link Segment#yIndex}
     *     <li> {@link Segment#isOneWay}
     *     <li> {@link Segment#laneCountForward}
     *     <li> {@link Segment#laneCountBackward}
     *     <li> {@link Segment#speedLimit}
     *     <li> {@link Segment#roadType}
     * </ul> 
     * @param srcSegment - source segment
     */
    public void stylePainter (Segment srcSegment) {
        this.setName(srcSegment.getName());
        this.setYIndex(srcSegment.getYIndex());
        this.setOneWay(srcSegment.isOneWay());
        this.setLaneCountForward(srcSegment.getLaneCountForward());
        this.setLaneCountBackward(srcSegment.getLaneCountBackward());
        this.setSpeedLimit(srcSegment.getSpeedLimit());
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
