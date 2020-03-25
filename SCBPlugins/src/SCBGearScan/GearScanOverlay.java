package SCBGearScan;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.util.Text;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class GearScanOverlay extends Overlay {
  private final GearScanConfig config;
  
  private List<Player> highlightedPlayers;
  
  @Inject
  private GearScanOverlay(GearScanConfig config) {
    this.config = config;
    setPosition(OverlayPosition.DYNAMIC);
  }
  
  void setHighlightedPlayers(List<Player> highlightedPlayers) {
    this.highlightedPlayers = highlightedPlayers;
  }
  
  public Dimension render(Graphics2D graphics) {
    if (this.highlightedPlayers == null || !this.config.Highlight())
      return null; 
    for (Player p : this.highlightedPlayers) {
      if (p == null)
        return null; 
      Polygon poly = p.getCanvasTilePoly();
      if (poly != null)
        OverlayUtil.renderPolygon(graphics, poly, this.config.getHighlightColor()); 
      String name = Text.sanitize(p.getName());
      Point textLocation = p.getCanvasTextLocation(graphics, name, p.getLogicalHeight() + 40);
      if (this.config.HighlightCombatLevel() && this.config.HighlightName()) {
        OverlayUtil.renderTextLocation(graphics, textLocation, name + " (" + p.getCombatLevel() + ")", this.config.getHighlightColor());
        continue;
      } 
      if (this.config.HighlightName()) {
        OverlayUtil.renderTextLocation(graphics, textLocation, name, this.config.getHighlightColor());
        continue;
      } 
      if (this.config.Highlight())
        OverlayUtil.renderTextLocation(graphics, textLocation, "(" + p.getCombatLevel() + ")", this.config.getHighlightColor()); 
    } 
    return null;
  }
}
