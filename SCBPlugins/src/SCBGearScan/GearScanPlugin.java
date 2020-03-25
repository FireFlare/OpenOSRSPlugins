package net.runelite.client.plugins.gearscan;

import com.google.inject.Provides;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemDefinition;
import net.runelite.api.MenuEntry;
import net.runelite.api.MenuOpcode;
import net.runelite.api.Player;
import net.runelite.api.WorldType;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.kit.KitType;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name = "Visne's Gear Scan")
public class GearScanPlugin extends Plugin {
  @Inject
  private Client client;
  
  @Inject
  private EventBus eventBus;
  
  @Inject
  private GearScanConfig config;
  
  @Inject
  private ItemManager itemManager;
  
  @Inject
  private ChatMessageManager chatMessageManager;
  
  @Inject
  private OverlayManager overlayManager;
  
  @Inject
  private GearScanOverlay overlay;
  
  private static final String GEAR_SCAN = "Gear scan";
  
  private List<Player> highlightedPlayers = new ArrayList<>();
  
  @Provides
  GearScanConfig provideConfig(ConfigManager configManager) {
    return (GearScanConfig)configManager.getConfig(GearScanConfig.class);
  }
  
  protected void startUp() throws Exception {
    addSubscriptions();
    this.overlayManager.add(this.overlay);
  }
  
  protected void shutDown() throws Exception {
    this.overlayManager.remove(this.overlay);
    this.eventBus.unregister(this);
  }
  
  public void onMenuEntryAdded(MenuEntryAdded event) {
    if (event.getOption().equals("Walk here")) {
      MenuEntry[] menuEntries = this.client.getMenuEntries();
      int lastIndex = menuEntries.length;
      menuEntries = Arrays.<MenuEntry>copyOf(menuEntries, lastIndex + 1);
      MenuEntry menuEntry = menuEntries[lastIndex] = new MenuEntry();
      menuEntry.setOption("Gear scan");
      menuEntry.setTarget(event.getTarget());
      menuEntry.setOpcode(MenuOpcode.CANCEL.getId());
      this.client.setMenuEntries(menuEntries);
    } 
  }
  
  private void addSubscriptions() {
    this.eventBus.subscribe(MenuEntryAdded.class, this, this::onMenuEntryAdded);
    this.eventBus.subscribe(MenuOptionClicked.class, this, this::onMenuOptionClicked);
  }
  
  public void onMenuOptionClicked(MenuOptionClicked event) {
    if (!event.getOption().contains("Gear scan"))
      return; 
    List<Player> players = this.client.getPlayers();
    ChatMessageBuilder builder = new ChatMessageBuilder();
    builder
      .append(ChatColorType.HIGHLIGHT).append("Players in World ")
      .append(ChatColorType.NORMAL).append(Integer.toString(this.client.getWorld())).append(" (").append(this.client.getWorldType().contains(WorldType.MEMBERS) ? "Members" : "Free").append(") ")
      .append(ChatColorType.HIGHLIGHT).append("with value over ")
      .append(ChatColorType.NORMAL).append(NumberFormat.getIntegerInstance().format((this.config.MinValue() / 1000))).append("K ")
      .append(ChatColorType.HIGHLIGHT).append("and lvl between ")
      .append(ChatColorType.NORMAL).append(Integer.toString(this.config.MinLevel()))
      .append(ChatColorType.HIGHLIGHT).append(" and ")
      .append(ChatColorType.NORMAL).append(Integer.toString(this.config.MaxLevel()))
      .append(ChatColorType.HIGHLIGHT).append(":\n");
    this.highlightedPlayers.clear();
    for (Player p : players) {
      int totalPrice = 0;
      int prot1 = 0;
      int prot2 = 0;
      int prot3 = 0;
      int prot4 = 0;
      Map<KitType, ItemDefinition> playerEquipment = new HashMap<>();
      for (KitType kitType : KitType.values()) {
        if (kitType != KitType.RING)
          if (kitType != KitType.AMMUNITION) {
            int itemId = p.getPlayerAppearance().getEquipmentId(kitType);
            if (itemId != -1) {
              ItemDefinition itemComposition = this.client.getItemDefinition(itemId);
              playerEquipment.put(kitType, itemComposition);
              int ItemPrice = this.itemManager.getItemPrice(itemId);
              totalPrice += ItemPrice;
              if (ItemPrice > prot1) {
                prot4 = prot3;
                prot3 = prot2;
                prot2 = prot1;
                prot1 = ItemPrice;
              } else if (ItemPrice > prot2) {
                prot4 = prot3;
                prot3 = prot2;
                prot2 = ItemPrice;
              } else if (ItemPrice > prot3) {
                prot4 = prot3;
                prot3 = ItemPrice;
              } else if (ItemPrice > prot4) {
                prot4 = ItemPrice;
              } 
            } 
          }  
      } 
      int IgnoredItems = this.config.protecteditems();
      if (IgnoredItems != 0 && IgnoredItems != 1 && IgnoredItems != 2 && IgnoredItems != 3)
        IgnoredItems = 4; 
      switch (IgnoredItems) {
        case 1:
          totalPrice -= prot1;
          break;
        case 2:
          totalPrice = totalPrice - prot1 - prot2;
          break;
        case 3:
          totalPrice = totalPrice - prot1 - prot2 - prot3;
          break;
        case 4:
          totalPrice = totalPrice - prot1 - prot2 - prot3 - prot4;
          break;
      } 
      int CombatLevel = p.getCombatLevel();
      if (totalPrice > this.config.MinValue() && CombatLevel >= this.config.MinLevel() && CombatLevel <= this.config.MaxLevel()) {
        if (!this.highlightedPlayers.contains(p))
          this.highlightedPlayers.add(p); 
        String StringPrice = "";
        if (!this.config.ExactValue()) {
          totalPrice /= 1000;
          StringPrice = NumberFormat.getIntegerInstance().format(totalPrice);
          StringPrice = StringPrice + 'K';
        } 
        if (this.config.ExactValue())
          StringPrice = NumberFormat.getIntegerInstance().format(totalPrice); 
        builder
          .append(ChatColorType.NORMAL).append(p.getName())
          .append(ChatColorType.HIGHLIGHT).append(" Risked value: ")
          .append(ChatColorType.NORMAL).append(StringPrice)
          .append(ChatColorType.HIGHLIGHT).append(", combat level: ")
          .append(ChatColorType.NORMAL).append(Integer.toString(CombatLevel)).append("\n")
          .build();
      } 
    } 
    if (this.config.Highlight()) {
      this.overlay.setHighlightedPlayers(this.highlightedPlayers);
    } else {
      this.overlay.setHighlightedPlayers(null);
    } 
    this.chatMessageManager.queue(QueuedMessage.builder()
        .type(ChatMessageType.FRIENDSCHATNOTIFICATION)
        .runeLiteFormattedMessage(builder.build())
        .build());
  }
}
