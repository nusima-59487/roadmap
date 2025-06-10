package me.nusimucat.roadmap.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;

import me.nusimucat.roadmap.util.Result;
import me.nusimucat.roadmap.util.Utils;
import me.nusimucat.roadmap.objects.Node;
import me.nusimucat.roadmap.objects.Segment;
import me.nusimucat.roadmap.objects.Node.Directions;

public class DBMethods {
    private static final Connection connectionSQL = DBConnect.getConnection(); 
    private static final String content = Utils.readFileToString(DBConnect.getStorageMethod() + "-statements.json");
    private static final JSONObject databaseStatements = new JSONObject(content); 

    /**
     * Initialize database
     * @throws SQLException
     */
    public void init () throws SQLException {
        List<Object> initStatements = databaseStatements.getJSONArray("init").toList(); 
        for (Object statement : initStatements) {
            PreparedStatement nodeTableStmt = connectionSQL.prepareStatement(statement.toString());
            nodeTableStmt.executeUpdate(); 
        }
        return; 
    }

    /**@return ID of created segment */
    public static Result<Integer, String> callAddSegment (Segment segment) {
        // verify starting node and ending node?
        try {
            CallableStatement stmt = connectionSQL.prepareCall(databaseStatements.getString("call_add_segment")); 
            stmt.setInt(1, segment.getStartingNode().getId());
            stmt.setString(2, segment.getStartingNode().getDirectionFromSegment(segment).toString());
            stmt.setInt(3, segment.getEndingNode().getId());
            stmt.setString(4, segment.getEndingNode().getDirectionFromSegment(segment).toString());
            stmt.setInt(5, segment.getLaneCountForward());
            stmt.setInt(6, segment.getLaneCountBackward());
            stmt.setInt(7, segment.getSpeedLimit());
            stmt.setString(8, segment.getRoadType());
            stmt.setString(9, segment.getPlayerUpdated() == null ? "console" : segment.getPlayerUpdated().toString());
            stmt.registerOutParameter(10, java.sql.Types.INTEGER); 
            
            stmt.execute(); 
            
            return Result.ok(stmt.getInt(10)); 
        } catch (Exception e) {
            return Result.err(e.toString()); 
        }
    }
    
    /**@return ID of created node */
    public static Result<Integer,String> callCreateAuxNode (Node node) {
        if (!(node.isAux())) return Result.err("Node is in wrong type, use DBMethods#createMainNode instead"); 
        try {
            PreparedStatement stmt = connectionSQL.prepareStatement(databaseStatements.getString("insert_node")); 
            stmt.setInt(1, node.getLocationX());
            stmt.setInt(2, node.getLocationZ());
            stmt.setString(3, node.getName());
            stmt.setBoolean(4, true);
            stmt.setNull(5, java.sql.Types.BOOLEAN);
            stmt.setNull(6, java.sql.Types.BOOLEAN); 
            stmt.setString(7, node.getPlayerUpdated() == null ? "console" : node.getPlayerUpdated().toString());
            
            int rowsAffected = stmt.executeUpdate(); 
            
            if (rowsAffected > 0) {
                // Retrieve the auto-generated keys (insert ID)
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int insertId = generatedKeys.getInt(1);
                    return Result.ok(insertId); 
                }
                return Result.err("Failed to retreive insert ID"); 
            }
            return Result.err("No records inserted");
        } catch (Exception e) {
            return Result.err(e.toString()); 
        }
    }
            
            
    public static Result<Boolean, String> callUpdateSegmentStartingNode (Segment segment, Node startingNode) {
        
    }
    
    public static Result<Boolean, String> callUpdateSegmentEndingNode (Segment segment, Node endingNode) {
        
    }
            
    /**@return ID of created node */
    public static Result<Integer,String> insertNode (Node node) {
        if (node.isAux()) return Result.err("Node is in wrong type, use DBMethods#createAuxNode instead"); 
        try {
            PreparedStatement stmt = connectionSQL.prepareStatement(databaseStatements.getString("insert_node")); 
            stmt.setInt(1, node.getLocationX());
            stmt.setInt(2, node.getLocationZ());
            stmt.setString(3, node.getName());
            stmt.setBoolean(4, false);
            stmt.setBoolean(5, node.hasStopSign());
            stmt.setBoolean(6, node.hasTrafficLight());
            stmt.setString(7, node.getPlayerUpdated() == null ? "console" : node.getPlayerUpdated().toString());

            int rowsAffected = stmt.executeUpdate(); 

            if (rowsAffected > 0) {
                // Retrieve the auto-generated keys (insert ID)
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int insertId = generatedKeys.getInt(1);
                    return Result.ok(insertId); 
                }
                return Result.err("Failed to retreive insert ID"); 
            }
            return Result.err("No records inserted");
        } catch (Exception e) {
            return Result.err(e.toString()); 
        }
    }

    public static Result<Boolean, String> updateNode () throws SQLException {
        // TODO
    }
    
    public static Result<Boolean, String> deleteNode () throws SQLException {
        // TODO
    }

    public static Result<Boolean, String> updateSegment () throws SQLException {
        // TODO
    }
            
    public static Result<Boolean, String> deleteSegment () throws SQLException {
        // TODO
    }
    
    public static Result<Integer, String> insertHistory (String message) {
        
    }

    public static Result<Node,String> selectNodeFromId (int nodeId) {
        try {
            PreparedStatement getNodeStmt = connectionSQL.prepareStatement(databaseStatements.getString("select_node_from_id"));
            getNodeStmt.setInt(1, nodeId);

            ResultSet result = getNodeStmt.executeQuery(); 
            if (result.next()) {
                Result<HashMap<Directions, Integer>, String> connectionsResult = selectConnectionsFromNodeId(nodeId);
                if (connectionsResult.isErr()) return Result.err(connectionsResult.getError());  
                
                Node nodeToReturn = new Node(
                    result.getInt("coord_x"), 
                    result.getInt("coord_z"),
                    result.getString("name"), 
                    result.getBoolean("is_main_node"),
                    connectionsResult.getValueOrThrow(), 
                    result.getBoolean("has_stop_sign"), 
                    result.getBoolean("has_traffic_light"), 
                    result.getTimestamp("create_time"), 
                    UUID.fromString(
                        result.getString("last_update_user_uuid")
                    ),
                    result.getTimestamp("last_update_time")
                ); 
                return Result.ok(nodeToReturn); 
            }

            return Result.err("No nodes match nodeId = " + nodeId);
        } catch (Exception e) {
            return Result.err(e.toString()); 
        } 

    }
    
    public static Result<ArrayList<Node>, String> selectNodesFromCoords (Object coords) {

    }

    public static Result<Node, String> selectNearestNodeFromCoords (Object coords) {
        
    }

    public static Result<Segment, String> selectSegmentFromId(int SegmentID) {
    
    }

    public static Result<HashMap<Directions, Integer>, String> selectConnectionsFromNodeId (int nodeId) {
        // if (node.isAux()) return Result.err("Node is in wrong type, use DBMethods#createAuxNode instead"); 
        try {
            PreparedStatement stmt = connectionSQL.prepareStatement(databaseStatements.getString("select_connections_from_node_id")); 
            stmt.setInt(1, nodeId);
            ResultSet result = stmt.executeQuery(); 
            HashMap<Directions, Integer> toReturn = new HashMap<Directions, Integer>(); 
            while (result.next()) {
                toReturn.put(
                    Node.Directions.fromValue(result.getString("direction")), 
                    result.getInt("segment_id")
                ); 
            }

            return Result.ok(toReturn); 
        } catch (Exception e) {
            return Result.err(e.toString()); 
        }
    }
}
