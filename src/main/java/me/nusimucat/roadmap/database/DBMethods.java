package me.nusimucat.roadmap.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;

import me.nusimucat.roadmap.Roadmap;
import me.nusimucat.roadmap.Utils;

public class DBMethods {
    private static final Connection connectionSQL = DBConnect.getConnection(); 
    private static final String content = Utils.readFileToString("database-statements.json");
    private static final JSONObject databaseStatements = new JSONObject(content); 

    public void initialization () throws SQLException {
        List<Object> initStatements = databaseStatements.getJSONArray("init").toList(); 
        for (int x = 0; x < initStatements.size(); x++) {
            String statement = initStatements.get(x).toString(); 
            PreparedStatement nodeTableStmt = connectionSQL.prepareStatement(statement);
            nodeTableStmt.executeUpdate(); 
        }        
    }

    /**
     * Create a main node
     * @param xcoords int
     * @param zcoords int
     * @param name String
     * @param hasStopSign Bool
     * @param hasTrafficLight Bool
     * @param creatorUuid UUID
     * @return int - Created Node ID
     * @throws SQLException if SQL error occurs
     */
    public static int createMainNode(
        int xcoords, int zcoords, String name, Boolean hasStopSign, Boolean hasTrafficLight, UUID creatorUuid
    ) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createMainNode'");
    }

    public static int createAuxNode(int xcoords, int zcoords, String name, UUID creatorUuid) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAuxNode'");
    }

    public static int createSegment () throws SQLException {
        // TODO
    }

    public static void editMainNode () throws SQLException {
        // TODO
    }

    public static void editAuxNode () throws SQLException {
        // TODO
    }

    public static void editSegment () throws SQLException {
        // TODO
    }

    public static void deleteMainNode () throws SQLException {
        // TODO
    }

    public static void deleteAuxNode () throws SQLException {
        // TODO
    }

    public static void deleteSegment () throws SQLException {
        // TODO
    }

    public static HashMap<String, Object> getNodeInfo(int NodeID) throws SQLException {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getNode'");

        PreparedStatement getNodeStmt = connectionSQL.prepareStatement(
            "SELECT * FROM Nodes WHERE id = ?"
        );
        getNodeStmt.setInt(1, NodeID);

        ResultSet result = getNodeStmt.executeQuery(); 
        if (result.next()) {
            HashMap<String, Object> nodeInfo = new HashMap<String, Object>(); 
            nodeInfo.put("coord_x", result.getInt("coord_x")); 
            nodeInfo.put("coord_z", result.getInt("coord_z")); 
            nodeInfo.put("is_main_node", result.getBoolean("is_main_node")); 
            nodeInfo.put("connection_n", result.getInt("connection_n")); 
            nodeInfo.put("connection_ne", result.getInt("connection_ne")); 
            nodeInfo.put("connection_e", result.getInt("connection_e")); 
            nodeInfo.put("connection_se", result.getInt("connection_se")); 
            nodeInfo.put("connection_s", result.getInt("connection_s")); 
            nodeInfo.put("connection_sw", result.getInt("connection_sw")); 
            nodeInfo.put("connection_w", result.getInt("connection_w")); 
            nodeInfo.put("connection_nw", result.getInt("connection_nw")); 
            nodeInfo.put("create_time", result.getTimestamp("create_time")); 
            nodeInfo.put("last_update_username", result.getString("last_update_username")); 
            nodeInfo.put("last_update_time", result.getTimestamp("last_update_time")); 
            return nodeInfo; 
        }
        return null; 
    }

    public static HashMap<String, Object> getSegmentInfo(int SegmentID) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSegmentInfo'");
    }
}
