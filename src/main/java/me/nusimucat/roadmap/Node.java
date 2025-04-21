package me.nusimucat.roadmap;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;

import me.nusimucat.roadmap.database.DBMethods;

public class Node {
    private int nodeId; // null if not in database (NO CONNECTIONS)
    private int coordx; 
    private int coordz; 
    private String name; 
    private HashMap<Directions, Integer> connections; // <Direction, SgementIDS>
    private Timestamp createTime;
    private UUID lastUpdateUserUUID;
    private Timestamp lastUpdateTime;
    private boolean isAuxNode; 
    private boolean hasStopSign; // null if aux
    private boolean hasTrafficLight; // null if aux
    private int associatedSegmentId; // aux node only, null otherwise

    private boolean isInDatabase = false; 
    private boolean isSyncToDatabase = false; 
    private Editor activeEditor; 

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
    
    private Node (int coordx, int coordz, String name, boolean isAuxNode, HashMap<Directions, Integer> connections, boolean hasStopSign, boolean hasTrafficLight, Timestamp createTime, UUID lastUpdateUserUUID, Timestamp lastUpdateTime) {
        this.coordx = coordx; 
        this.coordz = coordz; 
        this.name = name; 
        this.isAuxNode = isAuxNode; 
        this.connections = connections; 
        this.hasStopSign = hasStopSign; 
        this.hasTrafficLight = hasTrafficLight; 
        this.createTime = createTime; 
        this.lastUpdateUserUUID = lastUpdateUserUUID; 
        this.lastUpdateTime = lastUpdateTime; 
    }

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
        nodeToReturn.nodeId = nodeId;
        nodeToReturn.isInDatabase = true;
        nodeToReturn.isSyncToDatabase = true; 
        return nodeToReturn;
    }

    public static Node newNode (int coordx, int coordz, boolean isAux, Editor editor) {
        Node toReturn = new Node(coordx, coordz, "", isAux, new HashMap<Directions, Integer>(), false, false, Timestamp.valueOf(LocalDateTime.now()), editor.getPlayer().getUniqueId(), Timestamp.valueOf(LocalDateTime.now())); 
        // toReturn.activeEditor = Bukkit.getPlayer(creatorUUID); 
        toReturn.activeEditor = editor; 
        return toReturn; 
    }

    public void setActiveEditor (Editor editor) {
        this.activeEditor = editor; 
    }

    public void removeActiveEditor () {
        this.activeEditor = null; 
    }

    public void saveToDatabase () {
        if (this.isInDatabase) {
            // Update
            
        } else {
            // Insert
            // DBMethods.
        }
    }

    public int getId () {return this.nodeId;}
    public int getLocationX () {return this.coordx;}
    public int getLocationZ () {return this.coordz;}
    public void setLocation (Location location) {
        if (this.activeEditor == null) throw new IllegalStateException("Unknown player editing"); 
        this.coordx = location.getBlockX(); 
        this.coordz = location.getBlockZ(); 
        this.isSyncToDatabase = false; 
        this.lastUpdateUserUUID = this.activeEditor.getPlayer().getUniqueId(); 
        this.lastUpdateTime = Timestamp.valueOf(LocalDateTime.now()); 
    }
    public void setLocation (int coordX, int coordZ) {
        if (this.activeEditor == null) throw new IllegalStateException("Unknown player editing"); 
        this.coordx = coordX; 
        this.coordz = coordZ; 
        this.isSyncToDatabase = false; 
        this.lastUpdateUserUUID = this.activeEditor.getPlayer().getUniqueId(); 
        this.lastUpdateTime = Timestamp.valueOf(LocalDateTime.now()); 
    }
    public void setLocationX (int coordX) {
        if (this.activeEditor == null) throw new IllegalStateException("Unknown player editing"); 
        this.coordx = coordX;
        this.isSyncToDatabase = false; 
        this.lastUpdateUserUUID = this.activeEditor.getPlayer().getUniqueId(); 
        this.lastUpdateTime = Timestamp.valueOf(LocalDateTime.now()); 
    }
    public void setLocationZ (int coordZ) {
        if (this.activeEditor == null) throw new IllegalStateException("Unknown player editing"); 
        this.coordz = coordZ;
        this.isSyncToDatabase = false; 
        this.lastUpdateUserUUID = this.activeEditor.getPlayer().getUniqueId(); 
        this.lastUpdateTime = Timestamp.valueOf(LocalDateTime.now()); 
    }
    public String getName () {return this.name;}
    public void setName (String name) {
        if (this.activeEditor == null) throw new IllegalStateException("Unknown player editing"); 
        this.name = name;
        this.isSyncToDatabase = false; 
        this.lastUpdateUserUUID = this.activeEditor.getPlayer().getUniqueId(); 
        this.lastUpdateTime = Timestamp.valueOf(LocalDateTime.now()); 
    }
    public void addSegmentConnection (Segment segment, Directions direction) {
        if (this.connections.get(direction) != null) {
            throw new IllegalArgumentException(String.format("Direction %s already has a connection with another segment (ID %g)", direction, segment.getId()));
        }
        if (this.activeEditor == null) throw new IllegalStateException("Unknown player editing"); 
        this.connections.put(direction, segment.getId()); 
        this.isSyncToDatabase = false; 
        this.lastUpdateUserUUID = this.activeEditor.getPlayer().getUniqueId(); 
        this.lastUpdateTime = Timestamp.valueOf(LocalDateTime.now()); 
    }
    public void removeSegmentConnection (Directions direction) {
        if (this.activeEditor == null) throw new IllegalStateException("Unknown player editing"); 
        this.connections.remove(direction); 
        this.isSyncToDatabase = false; 
        this.lastUpdateUserUUID = this.activeEditor.getPlayer().getUniqueId(); 
        this.lastUpdateTime = Timestamp.valueOf(LocalDateTime.now()); 
    }
    public void removeSegmentConnection (Segment segment) {
        if (this.connections.containsValue(segment.getId())) {
            if (this.activeEditor == null) throw new IllegalStateException("Unknown player editing"); 
            Directions key = Utils.getKeyByValue(this.connections, segment.getId());
            this.removeSegmentConnection(key);
            this.isSyncToDatabase = false;  
            this.lastUpdateUserUUID = this.activeEditor.getPlayer().getUniqueId(); 
            this.lastUpdateTime = Timestamp.valueOf(LocalDateTime.now()); 
        }
    }
    // public HashMap<Directions,Integer> getSegmentConnections () {
    //     return this.connections; 
    // }
    public Segment getSegmentFromConnection (Directions direction) {
        int segmentId = this.connections.get(direction); 
        return Segment.fromDatabase(segmentId); 
    }
    public LocalDateTime getCreationTime () {
        return this.createTime.toLocalDateTime(); 
    }
    public LocalDateTime getLastUpdateTime () {
        return this.lastUpdateTime.toLocalDateTime(); 
    }
    /**
     * @return Last update user uuid, <code>null</code> for console
     */
    public UUID getLastUpdateUserUuid () {
        return this.lastUpdateUserUUID; 
    }
    public boolean isAux () {
        return this.isAuxNode; 
    }
    public void setAux (boolean isAux) {
        if (this.activeEditor == null) throw new IllegalStateException("Unknown player editing"); 
        if (isAux == this.isAuxNode) return; 
        if (isAux) this.isAuxNode = true; 
        else this.isAuxNode = false; 
        this.isSyncToDatabase = false; 
        this.lastUpdateUserUUID = this.activeEditor.getPlayer().getUniqueId(); 
        this.lastUpdateTime = Timestamp.valueOf(LocalDateTime.now()); 
    }
    /**@return If aux, return <code>false</code>*/
    public boolean hasStopSign () {
        if (this.isAuxNode) return false; 
        return this.hasStopSign; 
    }
    public void hasStopSign (boolean state) {
        if (this.isAuxNode) throw new IllegalStateException("Cannot change state hasStopSign for aux nodes"); 
        if (this.activeEditor == null) throw new IllegalStateException("Unknown player editing"); 
        if (this.hasStopSign == state) return; 
        this.hasStopSign = state; 
        if (state) this.hasTrafficLight = false; 
        this.lastUpdateUserUUID = this.activeEditor.getPlayer().getUniqueId(); 
        this.lastUpdateTime = Timestamp.valueOf(LocalDateTime.now()); 
    }
    /**@return If aux, return <code>false</code>*/
    public boolean hasTrafficLight () {
        if (this.isAuxNode) return false; 
        return this.hasTrafficLight; 
    }
    public void hasTrafficLight (boolean state) {
        if (this.isAuxNode) throw new IllegalStateException("Cannot change state hasStopSign for aux nodes"); 
        if (this.activeEditor == null) throw new IllegalStateException("Unknown player editing"); 
        if (this.hasTrafficLight == state) return; 
        this.hasTrafficLight = state; 
        if (state) this.hasStopSign = false; 
        this.lastUpdateUserUUID = this.activeEditor.getPlayer().getUniqueId(); 
        this.lastUpdateTime = Timestamp.valueOf(LocalDateTime.now()); 
    }
    public Segment getAssociatedSegment () {
        if (!this.isAuxNode) throw new IllegalStateException("Main nodes do not have associated segment"); 
        return Segment.fromDatabase(this.associatedSegmentId); 
    }
    public void setAssociatedSegment (Segment associatedSegment, int alignmentIndex, int lodLevel) {
        // TODO
    }
}
