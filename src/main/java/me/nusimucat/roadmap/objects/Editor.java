package me.nusimucat.roadmap.objects;

import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.nusimucat.roadmap.Wands;
import me.nusimucat.roadmap.database.DBMethods;

public class Editor {
    enum PlayerMode {
        INSPECTOR, EDITOR
    }
    private static final HashMap<Player, Editor> activeEditors = new HashMap<Player, Editor>();
    
    private final Player player; 
    private ItemStack[] playerInventory; 
    private PlayerMode playerMode;

    public Editor (Player pl) {
        this.player = pl; 
        this.playerInventory = pl.getInventory().getContents(); 
        activeEditors.put(this.player, this); 
    }

    public static Editor getEditor (Player pl) {
        if (activeEditors.containsKey(pl)) {
            return activeEditors.get(pl); 
        }
        return null; 
    }
    public Player getPlayer () {
        return this.player; 
    }

    // public static boolean playerIsInsp


    public void enableEditor (PlayerMode mode) {
        if (mode == null) throw new IllegalArgumentException("mode cannot be null");

        // case: EDITOR -> INSPECTOR: reset inventory
        if (this.playerMode == PlayerMode.EDITOR && mode == PlayerMode.INSPECTOR) {
            this.player.getInventory().setContents(playerInventory);
        }
        this.playerMode = mode; 
        
        // If editor, place tools in inventory
        if (this.playerMode == PlayerMode.EDITOR) {
            this.playerInventory = this.player.getInventory().getContents(); 
            this.player.getInventory().clear(); 
        }
    }

    public void disableInspector () {   
        // Reset Inventory if editor
        if (this.playerMode == PlayerMode.EDITOR)
            this.player.getInventory().setContents(playerInventory);
        this.playerMode = null; 
    }

    public void editorSetWands () {
        this.player.getInventory().setItem(0, Wands.Inspector.getItem()); 
        this.player.getInventory().setItem(3, Wands.NodeBuilder.getItem()); 
        this.player.getInventory().setItem(4, Wands.SegmentBuilder.getItem()); 
        this.player.getInventory().setItem(6, Wands.AuxNodeBuilder.getItem()); 
        this.player.getInventory().setItem(8, Wands.StylePainter.getItem()); 
    }
    
    // public Node getNodeInfo () {
    //     // return this.getNearestNode(xcoords, zcoords).getinfo()
    // }

    // public Segment getSegmentInfo () {
    //     // return this.getNearestSegment(xcoords, zcoords).getinfo()
    // }

    // public void createMainNode ( // add coordinate
    //     String name, Boolean hasStopSign, Boolean hasTrafficLight
    // ) throws SQLException {
    //     int xcoords = this.player.getLocation().getBlockX(); // no
    //     int zcoords = this.player.getLocation().getBlockZ();
    //     if (name == null) name = ""; 
    //     if (hasStopSign == null) hasStopSign = false; 
    //     if (hasTrafficLight == null) hasTrafficLight = false; 
    //     DBMethods.createMainNode(xcoords, zcoords, name, hasStopSign, hasTrafficLight, this.player.getUniqueId()); 
    // }

    public Node createMainNode (
        String name, Boolean hasStopSign, Boolean hasTrafficLight
        , int xcoords, int zcoords // temp
    ) {
        Node toReturn = Node.simpleConstruct(
            xcoords, zcoords, false, this); 
        toReturn.setName(name, this);
        toReturn.hasStopSign(hasStopSign, this);
        toReturn.hasTrafficLight(hasTrafficLight, this);

        toReturn.updateToDatabase();
        return toReturn; 
    }

    public void createAuxNode (String name) throws SQLException {
        int xcoords = this.player.getLocation().getBlockX(); // no
        int zcoords = this.player.getLocation().getBlockZ();

        if (name == null) name = ""; 
        // DBMethods.createAuxNode(xcoords, zcoords, name, this.player.getUniqueId()); 
    }

    public void createSegment () {
        // args: String name, String startNode, String endNode, Boolean isOneWay = False, int laneCount = 2, int speedLimit = 50, String roadType = "Road"
        // throws SQLException
        // DBMethods.createSegment(name, startNode, endNode, isOneWay, laneCount, laneCount, speedLimit, roadType, this.player)
    }
}
