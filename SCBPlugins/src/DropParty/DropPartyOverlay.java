package DropParty;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ColorUtil;

@Singleton
public class DropPartyOverlay extends Overlay {
  private static final int FILL_START_ALPHA = 25;
  
  private static final int OUTLINE_START_ALPHA = 255;
  
  private final Client client;
  
  private final DropPartyPlugin plugin;
  
  @Inject
  public DropPartyOverlay(Client client, DropPartyPlugin plugin) {
    setPosition(OverlayPosition.DYNAMIC);
    setLayer(OverlayLayer.UNDER_WIDGETS);
    this.client = client;
    this.plugin = plugin;
  }
  
  public Dimension render(Graphics2D graphics) {
    int tiles = this.plugin.getShowAmmount();
    if (tiles == 0)
      return null; 
    List<WorldPoint> path = this.plugin.getPlayerPath();
    List<WorldPoint> markedTiles = new ArrayList<>();
    for (int i = 0; i < path.size(); i++) {
      if (i > this.plugin.getMAXPATHSIZE() || i > this.plugin.getShowAmmount() - 1)
        break; 
      if (path.get(i) != null) {
        LocalPoint local = LocalPoint.fromWorld(this.client, path.get(i));
        Polygon tilePoly = null;
        if (local != null)
          tilePoly = Perspective.getCanvasTileAreaPoly(this.client, local, 1); 
        if (tilePoly != null) {
          if (!markedTiles.contains(path.get(i))) {
            graphics.setColor(new Color(ColorUtil.setAlphaComponent(this.plugin.getOverlayColor().getRGB(), 255), true));
            graphics.drawPolygon(tilePoly);
            OverlayUtil.renderTextLocation(graphics, Integer.toString(i + 1), this.plugin.getTextSize(), this.plugin
                .getFontStyle(), Color.WHITE, centerPoint(tilePoly.getBounds()), true, 0);
          } 
          markedTiles.add(path.get(i));
        } 
      } 
    } 
    return null;
  }
  
  private Point centerPoint(Rectangle rect) {
    int x = (int)(rect.getX() + rect.getWidth() / 2.0D);
    int y = (int)(rect.getY() + rect.getHeight() / 2.0D);
    return new Point(x, y);
  }
}
