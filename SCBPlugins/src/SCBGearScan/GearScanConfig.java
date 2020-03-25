package SCBGearScan;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("gearscan")
public interface GearScanConfig extends Config {
  @Range(min = 1, max = 4)
  @ConfigItem(keyName = "ProtectedItems", name = "Protected items", description = "Limit 4", position = 1)
  default int protecteditems() {
    return 1;
  }
  
  @ConfigItem(keyName = "ExactValue", name = "Show exact value", description = "shows the excact gp value", position = 2)
  default boolean ExactValue() {
    return false;
  }
  
  @ConfigItem(keyName = "MinLevel", name = "Min Level", description = "Lowest level to scan", position = 3)
  default int MinLevel() {
    return 70;
  }
  
  @ConfigItem(keyName = "MaxLevel", name = "Max Level", description = "Highest level to scan", position = 4)
  default int MaxLevel() {
    return 110;
  }
  
  @ConfigItem(keyName = "MinValue", name = "Min Value", description = "Lowest risked value to scan", position = 5)
  default int MinValue() {
    return 50000000;
  }
  
  @ConfigItem(keyName = "Highlight", name = "Highlight players", description = "Highlight players which meet requirements", position = 6)
  default boolean Highlight() {
    return true;
  }
  
  @ConfigItem(keyName = "HighlightColor", name = "Highlight Color", description = "Select highlight color", position = 7)
  default Color getHighlightColor() {
    return Color.CYAN;
  }
  
  @ConfigItem(keyName = "HighlightName", name = "Highlight names", description = "Highlight player names", position = 8)
  default boolean HighlightName() {
    return true;
  }
  
  @ConfigItem(keyName = "HighlightCombatLevel", name = "Highlight combat level", description = "Highlight combat level next to name", position = 9)
  default boolean HighlightCombatLevel() {
    return true;
  }
}
