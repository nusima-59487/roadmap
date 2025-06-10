package me.nusimucat.roadmap.objects;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.nusimucat.roadmap.util.Utils;
import me.nusimucat.roadmap.util.Result;
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

        public static Directions fromValue(String val) {
            for (Directions dir : Directions.values()) {
                if (dir.directionname.equalsIgnoreCase(val)) return dir; 
            }
            throw new IllegalArgumentException("No matching value from string " + val);
        }
        
        @Override
        public String toString() {
            return this.directionname; 
        }
    }; 
    
    public Node (
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

    /** Constructor from Database 
     * @throws Exception if no nodes match the node id */
    public static Node fromDatabase (int nodeId) throws Exception {
        Result<Node, String> result = DBMethods.selectNodeFromId(nodeId); 
        Node toReturn = result.getValueOrThrow(); 
        
        toReturn.id = nodeId;
        toReturn.isInDatabase = true;
        toReturn.isSyncToDatabase = true; 
        return toReturn;
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
    public Result<Boolean,String> updateToDatabase (Editor editor) {
        if (this.isInDatabase && this.isSyncToDatabase) return Result.ok(true); 
        else if (this.isInDatabase) {
            // Update
        } else {
            // Insert
            Result<Integer,String> nodeIdResult = DBMethods.insertNode(this); 
            if (nodeIdResult.isErr()) return Result.err(nodeIdResult.getError()); 
            this.id = nodeIdResult.getValueOrDefault(null); // default value never reaches
            this.isInDatabase = true; 
        }
        this.isSyncToDatabase = true; 
        return Result.ok(true); 
    }

    public int getLocationX () {return this.coordx;}
    public int getLocationZ () {return this.coordz;}
    public void setLocation (Location location) {
        this.coordx = location.getBlockX(); 
        this.coordz = location.getBlockZ(); 
        this.updateHistory(); 
    }
    public void setLocation (int coordX, int coordZ) {
        this.coordx = coordX; 
        this.coordz = coordZ; 
        this.updateHistory(); 
    }
    public void setLocationX (int coordX) {
        this.coordx = coordX;
        this.updateHistory(); 
    }
    public void setLocationZ (int coordZ) {
        this.coordz = coordZ;
        this.updateHistory(); 
    }
    public void addSegmentConnection (Segment segment, Directions direction) {
        if (this.connections.get(direction) != null) {
            throw new IllegalArgumentException(String.format("Direction %s already has a connection with another segment (ID %g)", direction, segment.getId()));
        }
        this.connections.put(direction, segment.getId()); 
        this.updateHistory(); 
    }
    public void removeSegmentConnection (Directions direction) {
        this.connections.remove(direction); 
        this.updateHistory();    
    }
    public void removeSegmentConnection (Segment segment) {
        if (this.connections.containsValue(segment.getId())) {
            Directions key = Utils.getKeyByValue(this.connections, segment.getId());
            this.removeSegmentConnection(key);
        }
    }
    public Result<Segment, String> getSegmentFromDirection (Directions direction) {
        try {
            int segmentId = this.connections.get(direction); 
            return Result.ok(Segment.fromDatabase(segmentId)); 
        } catch (Exception e) {
            return Result.err(e.toString()); 
        }
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
    public void setAux (boolean isAux) {
        if (isAux == this.isAuxNode) return; 
        if (isAux) this.isAuxNode = true; 
        else this.isAuxNode = false; 
        this.updateHistory();
    }
    /**@return If aux, return <code>false</code>*/
    public boolean hasStopSign () {
        if (this.isAuxNode) return false; 
        return this.hasStopSign; 
    }
    public void hasStopSign (boolean state) {
        if (this.isAuxNode) throw new IllegalStateException("Cannot change state hasStopSign for aux nodes"); 
        if (this.hasStopSign == state) return; 
        this.hasStopSign = state; 
        if (state) this.hasTrafficLight = false; 
        this.updateHistory();
    }
    /**@return If aux, return <code>false</code>*/
    public boolean hasTrafficLight () {
        if (this.isAuxNode) return false; 
        return this.hasTrafficLight; 
    }
    public void hasTrafficLight (boolean state) {
        if (this.isAuxNode) throw new IllegalStateException("Cannot change state hasStopSign for aux nodes"); 
        if (this.hasTrafficLight == state) return; 
        this.hasTrafficLight = state; 
        if (state) this.hasStopSign = false; 
        this.updateHistory();
    }

    /**@return If not aux, return <code>-1</code>*/
    public int getLODLevel () {
        if (!this.isAuxNode) return -1; 
        return this.lodLevel; 
    }
    public void setLODLevel (int lodLevel) {
        if (!this.isAuxNode) return; 
        this.lodLevel = lodLevel; 
        this.updateHistory(); 
    }

    public Result<Segment, String> getAssociatedSegment () {
        if (!this.isAuxNode) return Result.err("Main nodes do not have associated segment"); 
        try {
            return Result.ok(Segment.fromDatabase(this.associatedSegmentId)); 
        } catch (Exception e) {
            return Result.err(e.toString()); 
        }
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
    public void stylePainter (Node srcNode) {
        this.setName(srcNode.getName());
        this.hasStopSign(srcNode.hasStopSign());
        this.hasTrafficLight(srcNode.hasTrafficLight());
        this.setAux(srcNode.isAux());
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
