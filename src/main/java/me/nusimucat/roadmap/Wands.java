package me.nusimucat.roadmap;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public enum Wands {
    SegmentBuilder (
        Material.DIAMOND_SWORD, 
        "Add Segment", 
        NamedTextColor.DARK_AQUA, 
        "Select two nodes to make a segment\nRight Click: Select highlighted Node"
    ), 
    NodeBuilder (
        Material.DIAMOND_SHOVEL, 
        "Add Node", 
        NamedTextColor.AQUA, 
        "Add a node at selected location\nLeft Click: Select Block to Add Node"
    ), 
    AuxNodeBuilder (
        Material.GOLDEN_SHOVEL, 
        "Add Visual Node", 
        NamedTextColor.YELLOW, 
        "Add a visual node at selected segment\nLeft Click: Select Block to Add Node\nRight Click: Select highlighted segment"), 
    Inspector (
        Material.IRON_AXE, 
        "View Highlighted", 
        NamedTextColor.GREEN, 
        "View/Edit details for highlighted node/segment\nLeft Click: Edit propereties of highlighted item\nRight Click: Print propereties in chat"
    ); 

    private final Material wandMaterial;
    private final String wandName;
    private final NamedTextColor wandDisplayColor; 
    private final String wandDescription;

    Wands (
        Material wandMaterial, 
        String wandName, 
        NamedTextColor wandDisplayColor, 
        String wandDescription
    ) {
        this.wandMaterial = wandMaterial; 
        this.wandName = wandName; 
        this.wandDisplayColor = wandDisplayColor; 
        this.wandDescription = wandDescription; 
    }

    private TextComponent getWandDisplayComponent () {
        return Component.text(this.wandName, this.wandDisplayColor); 
    }

    private List<TextComponent> getWandDisplayLore () {
        List<TextComponent> toReturn = new ArrayList<TextComponent>(); 
        toReturn.add(Component.text(this.wandDescription, NamedTextColor.GRAY)); 
        return toReturn;  
    }

    public Material getMaterial () {
        return this.wandMaterial; 
    }

    public ItemStack getItem () {
        ItemStack toReturn = new ItemStack(this.wandMaterial); 
        ItemMeta metaToReturn = toReturn.getItemMeta(); 
        metaToReturn.displayName(this.getWandDisplayComponent());
        metaToReturn.lore(this.getWandDisplayLore());
        toReturn.setItemMeta(metaToReturn); 
        return toReturn; 
    }
}
