package DropParty;

import com.google.inject.Provides;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.util.Text;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name = "Drop Party", description = "Marks where a user ran, for drop partys", tags = {"Drop", "Party", "marker", "player"}, type = PluginType.UTILITY, enabledByDefault = false)
@Singleton
public class DropPartyPlugin extends Plugin {
  @Inject
  private DropPartyConfig config;
  
  private List<WorldPoint> playerPath = new ArrayList<>();
  
  List<WorldPoint> getPlayerPath() {
    return this.playerPath;
  }
  
  private String playerName = "";
  
  String getPlayerName() {
    return this.playerName;
  }
  
  private int showAmmount = 0;
  
  int getShowAmmount() {
    return this.showAmmount;
  }
  
  private int MAXPATHSIZE = 100;
  
  private Player runningPlayer;
  
  private Color overlayColor;
  
  @Inject
  private Notifier notifier;
  
  @Inject
  private OverlayManager overlayManager;
  
  @Inject
  private DropPartyOverlay coreOverlay;
  
  @Inject
  private EventBus eventbus;
  
  @Inject
  private Client client;
  
  private int fontStyle;
  
  private int textSize;
  
  int getMAXPATHSIZE() {
    return this.MAXPATHSIZE;
  }
  
  Color getOverlayColor() {
    return this.overlayColor;
  }
  
  int getFontStyle() {
    return this.fontStyle;
  }
  
  int getTextSize() {
    return this.textSize;
  }
  
  @Provides
  DropPartyConfig getConfig(ConfigManager configManager) {
    return (DropPartyConfig)configManager.getConfig(DropPartyConfig.class);
  }
  
  protected void startUp() {
    updateConfig();
    addSubscriptions();
    this.overlayManager.add(this.coreOverlay);
    reset();
  }
  
  protected void shutDown() {
    this.overlayManager.remove(this.coreOverlay);
    reset();
    this.eventbus.unregister(this);
  }
  
  private void addSubscriptions() {
    this.eventbus.subscribe(ConfigChanged.class, this, this::onConfigChanged);
    this.eventbus.subscribe(GameTick.class, this, this::onGameTick);
  }
  
  private void onGameTick(GameTick event) {
    shuffleList();
    if (this.playerName.equalsIgnoreCase(""))
      return; 
    this.runningPlayer = null;
    for (Player player : this.client.getPlayers()) {
      if (player.getName() == null)
        continue; 
      if (Text.standardize(player.getName()).equalsIgnoreCase(this.playerName)) {
        this.runningPlayer = player;
        break;
      } 
    } 
    if (this.runningPlayer == null) {
      cordsError();
      return;
    } 
    addCords();
  }
  
  private void cordsError() {
    this.playerPath.add(null);
  }
  
  private void shuffleList() {
    if (this.playerPath.size() > this.MAXPATHSIZE - 1)
      this.playerPath.remove(0); 
  }
  
  private void addCords() {
    while (true) {
      if (this.playerPath.size() >= this.MAXPATHSIZE) {
        this.playerPath.add(this.runningPlayer.getWorldLocation());
        break;
      } 
      this.playerPath.add(null);
    } 
  }
  
  private void onConfigChanged(ConfigChanged event) {
    if (!event.getGroup().equals("drop"))
      return; 
    updateConfig();
  }
  
  private void reset() {
    this.playerPath.clear();
  }
  
  private void updateConfig() {
    this.playerName = this.config.playerName();
    this.showAmmount = this.config.showAmmount();
    this.overlayColor = this.config.overlayColor();
    this.fontStyle = this.config.fontStyle().getFont();
    this.textSize = this.config.textSize();
  }
}
