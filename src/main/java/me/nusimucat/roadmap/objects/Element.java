package me.nusimucat.roadmap.objects;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.bukkit.entity.Player;

import me.nusimucat.roadmap.util.Result;

public abstract class Element {
    protected int id; 
    private String name; 
    private Timestamp timeCreated;
    private Timestamp timeUpdated; 
    private UUID playerUpdated; 

    protected boolean isInDatabase = false; 
    protected boolean isSyncToDatabase = false; 

    protected Element (String name, Timestamp timeCreated, Timestamp timeUpdated, UUID playerUpdated) {
        this.name = name; 
        this.timeCreated = timeCreated; 
        this.timeUpdated = timeUpdated; 
        this.playerUpdated = playerUpdated; 
    }

    /** */
    public int getId () {return this.id;}; 

    /** */
    public String getName () {return this.name;}; 

    /**
     * @param name
     * @param editor null for console
     */
    public void setName (String name) {
        this.name = name; 
    };

    /** */
    public LocalDateTime getTimeCreated () {return this.timeCreated.toLocalDateTime();}; 

    /** */
    public LocalDateTime getTimeUpdated () {return this.timeUpdated.toLocalDateTime();}; 

    /**@return uuid of player - null for console*/
    public UUID getPlayerUpdated () {return this.playerUpdated;}; 

    /**
     * Render engine
     * @param player player to show
     */
    public abstract void renderTo (Player player); 

    /**
     * Render engine
     * @param editor player to show
     */
    public abstract void renderTo (Editor editor); 

    public void updateHistory () {
        this.isSyncToDatabase = false; 
        this.timeUpdated = Timestamp.valueOf(LocalDateTime.now()); 
    }
    // if (editor == null) {
    //     this.playerUpdated = null; 
    // } else {
    //     this.playerUpdated = editor.getPlayer().getUniqueId(); 
    // }
    /** */
    public abstract Result<Boolean,String> updateToDatabase (Editor editor); 
}
