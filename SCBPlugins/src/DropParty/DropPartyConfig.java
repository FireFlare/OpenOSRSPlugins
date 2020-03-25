package DropParty;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("drop")
public interface DropPartyConfig extends Config {
  @ConfigItem(keyName = "playerName", name = "Dropping player", description = "selects what players name to mark tiles", position = 0)
  default String playerName() {
    return "";
  }
  
  @Range(min = 1, max = 40)
  @ConfigItem(keyName = "showAmmount", name = "Trail length", description = "Shows the legnth of the droppers trail", position = 1)
  default int showAmmount() {
    return 10;
  }
  
  @ConfigItem(position = 2, keyName = "overlayColor", name = "Overlay Color", description = "Configures the color of the overlay")
  default Color overlayColor() {
    return new Color(0, 150, 200);
  }
  
  @ConfigItem(position = 3, keyName = "fontStyle", name = "Font Style", description = "Bold/Italics/Plain")
  default FontStyle fontStyle() {
    return FontStyle.BOLD;
  }
  
  @Range(min = 10, max = 40)
  @ConfigItem(position = 4, keyName = "textSize", name = "Text Size", description = "Text Size for Timers.")
  default int textSize() {
    return 18;
  }
  
  public enum FontStyle {
    BOLD("Bold", 1),
    ITALIC("Italic", 2),
    PLAIN("Plain", 0);
    
    FontStyle(String name, int font) {
      this.name = name;
      this.font = font;
    }
    
    private String name;
    
    private int font;
    
    public String getName() {
      return this.name;
    }
    
    public int getFont() {
      return this.font;
    }
    
    public String toString() {
      return getName();
    }
  }
}
