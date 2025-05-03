package me.nusimucat.roadmap.objects;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.nusimucat.roadmap.Roadmap;
import me.nusimucat.roadmap.Utils;
import me.nusimucat.roadmap.database.DBMethods;

public class Node extends Element {
    private int coordx; 
    private int coordz; 
    private HashMap<Directions, Integer> connections; // <Direction, SgementIDS>
    private boolean isAuxNode; 
    private boolean hasStopSign; // null if aux
    private boolean hasTrafficLight; // null if aux
    private int associatedSegmentId; // aux node only, null otherwise
    private int lodLevel; //aux node only, null otherwise

    public static enum Directions {
        NORTH("n"), 
        NORTHEAST("ne"), 
        EAST("e"), 
        SOUTHEAST("se"), 
        SOUTH("s"), 
        SOUTHWEST("sw"), 
        WEST("w"), 
        NORTHWEST("nw"), 
        NONE("none"); 

        private final String directionname; 
    
        Directions (String directionname) {
            this.directionname = directionname; 
        }
        
        @Override
        public String toString() {
            return this.directionname; 
        }
    }; 
    
    private Node (
        int coordx, 
        int coordz, 
        String name, 
        boolean isAuxNode, 
        HashMap<Directions, Integer> connections, 
        boolean hasStopSign, 
        boolean hasTrafficLight, 
        Timestamp createTime, 
        UUID lastUpdateUserUUID, 
        Timestamp lastUpdateTime
    ) {
        super(name, createTime, lastUpdateTime, lastUpdateUserUUID); 
        this.coordx = coordx; 
        this.coordz = coordz; 
        this.isAuxNode = isAuxNode; 
        this.connections = connections; 
        this.hasStopSign = hasStopSign; 
        this.hasTrafficLight = hasTrafficLight; 
    }

    /** Constructor from Database */
    public static Node fromDatabase (int nodeId) {
        HashMap<String,Object> nodeInfo; 
        try {
            nodeInfo = DBMethods.getNodeInfo(nodeId);
        } catch (SQLException e) {
            Roadmap.getLoggerInstance().warn("Failed to get node info from database");
            Roadmap.getLoggerInstance().warn(e.getMessage());
            return null; 
        }

        HashMap<Directions, Integer> connections = new HashMap<Directions, Integer>();
        // TODO: import connections from database
        Node nodeToReturn = new Node(
            (int) nodeInfo.get("coord_x"),
            (int) nodeInfo.get("coord_z"),
            (String) nodeInfo.get("name"),
            (boolean) nodeInfo.get("is_aux_node"),
            connections,
            (boolean) nodeInfo.get("has_stop_sign"),
            (boolean) nodeInfo.get("has_traffic_light"),
            (Timestamp) nodeInfo.get("create_time"),
            (UUID) nodeInfo.get("last_update_user_uuid"),
            (Timestamp) nodeInfo.get("last_update_time")
        ); 
        nodeToReturn.id = nodeId;
        nodeToReturn.isInDatabase = true;
        nodeToReturn.isSyncToDatabase = true; 
        return nodeToReturn;
    }

    public static Node simpleConstruct (int coordx, int coordz, boolean isAux, Editor editor) {
        // TODO: get default from editor
        Node toReturn = new Node(        
            coordx, 
            coordz, 
            "", 
            isAux, 
            new HashMap<Directions, Integer>(), 
            false, 
            false, 
            Timestamp.valueOf(LocalDateTime.now()), 
            editor == null ? null : editor.getPlayer().getUniqueId(), 
            Timestamp.valueOf(LocalDateTime.now())
        ); 
        return toReturn; 
    }

    @Override
    public void updateToDatabase () {
        try {
            if (this.isInDatabase && this.isSyncToDatabase) return; 
            else if (this.isInDatabase) {
                // Update
            } else {
                // Insert
                int nodeId = DBMethods.createMainNode(this); 
                this.id = nodeId; 
                this.isInDatabase = true; 
            }
            this.isSyncToDatabase = true; 
        } catch (SQLException e) {
            // TODO: handle exception
        }
    }

    public int getLocationX () {return this.coordx;}
    public int getLocationZ () {return this.coordz;}
    public void setLocation (Location location, Editor editor) {
        this.coordx = location.getBlockX(); 
        this.coordz = location.getBlockZ(); 
        this.updateHistory(editor); 
    }
    public void setLocation (int coordX, int coordZ, Editor editor) {
        this.coordx = coordX; 
        this.coordz = coordZ; 
        this.updateHistory(editor); 
    }
    public void setLocationX (int coordX, Editor editor) {
        this.coordx = coordX;
        this.updateHistory(editor); 
    }
    public void setLocationZ (int coordZ, Editor editor) {
        this.coordz = coordZ;
        this.updateHistory(editor); 
    }
    public void addSegmentConnection (Segment segment, Directions direction, Editor editor) {
        if (this.connections.get(direction) != null) {
            throw new IllegalArgumentException(String.format("Direction %s already has a connection with another segment (ID %g)", direction, segment.getId()));
        }
        this.connections.put(direction, segment.getId()); 
        this.updateHistory(editor); 
    }
    public void removeSegmentConnection (Directions direction, Editor editor) {
        this.connections.remove(direction); 
        updateHistory(editor);    
    }
    public void removeSegmentConnection (Segment segment, Editor editor) {
        if (this.connections.containsValue(segment.getId())) {
            Directions key = Utils.getKeyByValue(this.connections, segment.getId());
            this.removeSegmentConnection(key, editor);
        }
    }
    public Segment getSegmentFromDirection (Directions direction) {
        int segmentId = this.connections.get(direction); 
        return Segment.fromDatabase(segmentId); 
    }
    public Directions getDirectionFromSegment (Segment segment) {
        if (this.connections.containsValue(segment.getId())) {
            Directions key = Utils.getKeyByValue(this.connections, segment.getId()); 
            return key; 
        }
        return null; 
    }
    public boolean isAux () {
        return this.isAuxNode; 
    }
    public void setAux (boolean isAux, Editor editor) {
        if (isAux == this.isAuxNode) return; 
        if (isAux) this.isAuxNode = true; 
        else this.isAuxNode = false; 
        this.updateHistory(editor);
    }
    /**@return If aux, return <code>false</code>*/
    public boolean hasStopSign () {
        if (this.isAuxNode) return false; 
        return this.hasStopSign; 
    }
    public void hasStopSign (boolean state, Editor editor) {
        if (this.isAuxNode) throw new IllegalStateException("Cannot change state hasStopSign for aux nodes"); 
        if (this.hasStopSign == state) return; 
        this.hasStopSign = state; 
        if (state) this.hasTrafficLight = false; 
        this.updateHistory(editor);
    }
    /**@return If aux, return <code>false</code>*/
    public boolean hasTrafficLight () {
        if (this.isAuxNode) return false; 
        return this.hasTrafficLight; 
    }
    public void hasTrafficLight (boolean state, Editor editor) {
        if (this.isAuxNode) throw new IllegalStateException("Cannot change state hasStopSign for aux nodes"); 
        if (this.hasTrafficLight == state) return; 
        this.hasTrafficLight = state; 
        if (state) this.hasStopSign = false; 
        this.updateHistory(editor);
    }

    /**@return If aux, return <code>-1</code>*/
    public int getLODLevel () {
        if (!this.isAuxNode) return -1; 
        return this.lodLevel; 
    }
    public void setLODLevel (int lodLevel, Editor editor) {
        if (!this.isAuxNode) throw new IllegalStateException("Cannot change LOD Level for main nodes"); 
        this.lodLevel = lodLevel; 
        this.updateHistory(editor); 
    }

    public Segment getAssociatedSegment () {
        if (!this.isAuxNode) throw new IllegalStateException("Main nodes do not have associated segment"); 
        return Segment.fromDatabase(this.associatedSegmentId); 
    }
    public void setAssociatedSegment (Segment associatedSegment, int alignmentIndex, int lodLevel) {
        // TODO
    }


    /**
     * Applies styles from another node to this node
     * Changes includes: 
     * - {@link Element#name}
     * - {@link Node#hasStopSign}
     * - {@link Node#hasTrafficLight}
     * - {@link Node#isAuxNode}
     * @param srcNode - source node
     */
    public void stylePainter (Node srcNode, Editor editor) {
        this.setName(srcNode.getName(), editor);
        this.hasStopSign(srcNode.hasStopSign(), editor);
        this.hasTrafficLight(srcNode.hasTrafficLight(), editor);
        this.setAux(srcNode.isAux(), editor);
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
