package net.runelite.client.plugins.nightmare;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.awt.Polygon;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.Timer;
import net.runelite.client.util.Text;

@PluginDescriptor(
	name = "<html><font color=#86C43F>[RB]</font> Nightmare</html>",
	enabledByDefault = false,
	description = "Show what prayer to use and which tiles to avoid.",
	tags = {"bosses", "combat", "nm", "overlay", "nightmare", "pve", "pvm", "ashihama", "ported","RB"}
)

@Slf4j
@Singleton
public class NightmarePlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private NightmareConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private InfoBoxManager infoBoxManager;
	@Inject
	private SpriteManager spriteManager;
	@Inject
	private ItemManager itemManager;
	@Inject
	private NightmareOverlay overlay;
	@Inject
	private NightmarePrayerOverlay prayerOverlay;
	@Inject
	private NightmarePrayerInfoBox prayerInfoBox;
	@Inject
	private SanfewInfoBox sanfewInfoBox;

	// Nightmare's attack animations
	private static final int NIGHTMARE_HUSK_SPAWN = 8565;
	private static final int NIGHTMARE_PARASITE_TOSS = 8606;
	private static final int NIGHTMARE_CHARGE = 8609;
	private static final int NIGHTMARE_MELEE_ATTACK = 8594;
	private static final int NIGHTMARE_RANGE_ATTACK = 8596;
	private static final int NIGHTMARE_MAGIC_ATTACK = 8595;
	private static final int NIGHTMARE_PRE_MUSHROOM = 37738;
	private static final int NIGHTMARE_MUSHROOM = 37739;
	private static final int NIGHTMARE_SHADOW = 1767;   // graphics object
	private static final int NIGHTMARE_REGION_ID = 15515;

	private static final LocalPoint MIDDLE_LOCATION = new LocalPoint(6208, 8128);
	private static final Set<LocalPoint> PHOSANIS_MIDDLE_LOCATIONS = ImmutableSet.of(new LocalPoint(6208, 7104), new LocalPoint(7232, 7104));
	private static final List<Integer> INACTIVE_TOTEMS = Arrays.asList(9435, 9438, 9441, 9444);
	private static final List<Integer> ACTIVE_TOTEMS = Arrays.asList(9436, 9439, 9442, 9445);
	@Getter(AccessLevel.PACKAGE)
	private final Map<Integer, MemorizedTotem> totems = new HashMap<>();
	@Getter(AccessLevel.PACKAGE)
	private final Map<LocalPoint, GameObject> spores = new HashMap<>();
	@Getter(AccessLevel.PACKAGE)
	private final Map<Polygon, Player> huskTarget = new HashMap<>();
	@Getter(AccessLevel.PACKAGE)
	private final Map<Integer, Player> parasiteTargets = new HashMap<>();
	@Getter(AccessLevel.PACKAGE)
	private final Set<GraphicsObject> shadows = new HashSet<>();
	private final Set<NPC> husks = new HashSet<>();
	private final Set<NPC> parasites = new HashSet<>();
	private final Set<NPC> sleepwalkers = new HashSet<>();

	@Nullable
	@Getter(AccessLevel.PACKAGE)
	private NightmareAttack pendingNightmareAttack;
	@Nullable
	@Getter(AccessLevel.PACKAGE)
	private NPC nm;
	@Getter(AccessLevel.PACKAGE)
	private boolean inFight;
	
	private boolean inRegion;

	private boolean cursed;

	@Getter(AccessLevel.PACKAGE)
	private int ticksUntilNextAttack = 0;

	@Getter(AccessLevel.PACKAGE)
	private boolean parasite;
	@Getter(AccessLevel.PACKAGE)
	private int ticksUntilParasite = 0;

	@Getter(AccessLevel.PACKAGE)
	private boolean nightmareCharging = false;

	@Getter(AccessLevel.PACKAGE)
	private boolean shadowsSpawning = false;
	@Getter(AccessLevel.PACKAGE)
	private int shadowsTicks;

	private int totemsAlive = 0;

	@Getter(AccessLevel.PACKAGE)
	@Setter
	private boolean flash = false;

	private ArrayList<Projectile> nightmareProjectiles = new ArrayList<>();

	public NightmarePlugin()
	{
		inFight = false;
	}

	@Provides
	NightmareConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NightmareConfig.class);
	}

	@Override
	protected void startUp()
	{
		if (client.getGameState() != GameState.LOGGED_IN || !inNightmareRegion())
		{
			return;
		}
		
		init();
	}
	
	private void init()
	{
		inRegion = true;
		overlayManager.add(overlay);
		overlayManager.add(prayerOverlay);
		overlayManager.add(prayerInfoBox);
		overlayManager.add(sanfewInfoBox);
		reset();
	}

	@Override
	protected void shutDown()
	{
		inRegion = false;
		overlayManager.remove(overlay);
		overlayManager.remove(prayerOverlay);
		overlayManager.remove(prayerInfoBox);
		overlayManager.remove(sanfewInfoBox);
		reset();
	}

	private void reset()
	{
		inFight = false;
		nm = null;
		pendingNightmareAttack = null;
		nightmareCharging = false;
		shadowsSpawning = false;
		cursed = false;
		flash = false;
		parasite = false;
		ticksUntilNextAttack = 0;
		ticksUntilParasite = 0;
		shadowsTicks = 0;
		totemsAlive = 0;
		totems.clear();
		spores.clear();
		shadows.clear();
		husks.clear();
		huskTarget.clear();
		parasites.clear();
		parasiteTargets.clear();
		sleepwalkers.clear();
	}
	
	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		final GameState gamestate = event.getGameState();
		
		switch (gamestate)
		{
			case LOGGED_IN:
				if (inNightmareRegion())
				{
					if (!inRegion)
					{
						init();
					}
				}
				else
				{
					if (inRegion)
					{
						shutDown();
					}
				}
				break;
			case HOPPING:
			case LOGIN_SCREEN:
				if (inRegion)
				{
					shutDown();
				}
				break;
			default:
				break;
		}
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event)
	{
		if (!inRegion || !inFight)
		{
			return;
		}

		GameObject gameObj = event.getGameObject();
		int id = gameObj.getId();
		if (id == NIGHTMARE_MUSHROOM || id == NIGHTMARE_PRE_MUSHROOM)
		{
			spores.put(gameObj.getLocalLocation(), gameObj);
		}
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event)
	{
		if (!inRegion || !inFight)
		{
			return;
		}

		GameObject gameObj = event.getGameObject();
		int id = gameObj.getId();
		if (id == NIGHTMARE_MUSHROOM || id == NIGHTMARE_PRE_MUSHROOM)
		{
			spores.remove(gameObj.getLocalLocation());
		}
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated event)
	{
		if (!inRegion || !inFight)
		{
			return;
		}

		if (event.getGraphicsObject().getId() == NIGHTMARE_SHADOW)
		{
			shadows.add(event.getGraphicsObject());
			shadowsSpawning = true;
			shadowsTicks = 5;
			ticksUntilNextAttack = 5;
		}
	}

	@Subscribe
	private void onProjectileMoved(ProjectileMoved event)
	{
		if (!inRegion || !inFight)
		{
			return;
		}

		var projectile = event.getProjectile();

		if (nightmareProjectiles.contains(projectile))
		{
			return;
		}

		nightmareProjectiles.add(projectile);

		Player targetPlayer;
		switch (projectile.getId())
		{
			case 1770:
				targetPlayer = (Player) projectile.getInteracting();
				parasiteTargets.putIfAbsent(targetPlayer.getId(), targetPlayer);
				break;
			case 1781:
				targetPlayer = (Player) projectile.getInteracting();
				huskTarget.putIfAbsent(targetPlayer.getCanvasTilePoly(), targetPlayer);
				break;
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!inRegion)
		{
			return;
		}
		
		Actor actor = event.getActor();
		if (!(actor instanceof NPC))
		{
			return;
		}

		NPC npc = (NPC) actor;

		// this will trigger once when the fight begins
		if (nm == null && npc.getName() != null && (npc.getName().equalsIgnoreCase("The Nightmare") || npc.getName().equalsIgnoreCase("Phosani's Nightmare")))
		{
			//reset everything
			reset();
			nm = npc;
			inFight = true;
		}
		
		if (!inFight || !npc.equals(nm))
		{
			return;
		}
		
		int animationId = npc.getAnimation();
		
		if (animationId == NIGHTMARE_MAGIC_ATTACK)
		{
			ticksUntilNextAttack = 7;
			pendingNightmareAttack = cursed ? NightmareAttack.CURSE_MAGIC : NightmareAttack.MAGIC;
		}
		else if (animationId == NIGHTMARE_MELEE_ATTACK)
		{
			ticksUntilNextAttack = 7;
			pendingNightmareAttack = cursed ? NightmareAttack.CURSE_MELEE : NightmareAttack.MELEE;
		}
		else if (animationId == NIGHTMARE_RANGE_ATTACK)
		{
			ticksUntilNextAttack = 7;
			pendingNightmareAttack = cursed ? NightmareAttack.CURSE_RANGE : NightmareAttack.RANGE;
		}
		// check if phosanis because the middle locations may be used in the others charge locations
		else if (animationId == NIGHTMARE_CHARGE && ((!isPhosanis(npc.getId()) && !MIDDLE_LOCATION.equals(npc.getLocalLocation())) || (isPhosanis(npc.getId()) && !PHOSANIS_MIDDLE_LOCATIONS.contains(npc.getLocalLocation()))))
		{
			nightmareCharging = true;
			ticksUntilNextAttack = 5;
		}

		if (nightmareCharging && animationId != -1 && animationId != NIGHTMARE_CHARGE)
		{
			nightmareCharging = false;
		}

		if (animationId != NIGHTMARE_HUSK_SPAWN && !huskTarget.isEmpty())
		{
			huskTarget.clear();
		}

		if (animationId == NIGHTMARE_PARASITE_TOSS)
		{
			ticksUntilParasite = 27;

			if (config.parasitesInfoBox())
			{
				Timer parasiteInfoBox = new Timer(16200L, ChronoUnit.MILLIS, itemManager.getImage(ItemID.PARASITIC_EGG), this);
				parasiteInfoBox.setTooltip("Parasites");
				infoBoxManager.addInfoBox(parasiteInfoBox);
			}
		}
	}

	@Subscribe
	public void onNpcChanged(NpcChanged event)
	{
		if (!inRegion)
		{
			return;
		}
		
		final NPC npc = event.getNpc();

		//if npc is in the totems map, update its phase
		if (totems.containsKey(npc.getIndex()))
		{
			totems.get(npc.getIndex()).updateCurrentPhase(npc.getId());
		}
		if (INACTIVE_TOTEMS.contains(npc.getId()))
		{
			//else if the totem is not in the totem array and it is an inactive totem, add it to the totem map.
			totems.putIfAbsent(npc.getIndex(), new MemorizedTotem(npc));
			totemsAlive++;
		}
		if (ACTIVE_TOTEMS.contains(npc.getId()))
		{
			totemsAlive--;
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (!inRegion)
		{
			return;
		}
		
		final NPC npc = event.getNpc();

		if (npc.getName() != null && npc.getName().equalsIgnoreCase("parasite"))
		{
			parasites.add(npc);
		}

		if (npc.getName() != null && npc.getName().equalsIgnoreCase("husk"))
		{
			husks.add(npc);
		}

		if (npc.getName() != null && npc.getName().equalsIgnoreCase("sleepwalker"))
		{
			sleepwalkers.add(npc);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (!inRegion)
		{
			return;
		}
		
		final NPC npc = event.getNpc();

		if (npc.getName() != null && npc.getName().equalsIgnoreCase("sleepwalker"))
		{
			sleepwalkers.remove(npc);
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath event)
	{
		if (!inRegion)
		{
			return;
		}
		
		if (event.getActor() instanceof NPC && event.getActor().getName() != null)
		{
			final NPC npc = (NPC)event.getActor();

			if (npc.getName() != null && npc.getName().equalsIgnoreCase("parasite"))
			{
				parasites.remove(npc);
			}

			if (npc.getName() != null && npc.getName().equalsIgnoreCase("husk"))
			{
				husks.remove(npc);
			}
		}
	}

	@Subscribe
	private void onChatMessage(ChatMessage event)
	{
		if (!inRegion || !inFight || nm == null || event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		if (event.getMessage().contains("The Nightmare has impregnated you with a deadly parasite!"))
		{
			Player localPlayer = client.getLocalPlayer();
			if (localPlayer != null)
			{
				parasiteTargets.putIfAbsent(localPlayer.getId(), localPlayer);
			}

			flash = true;
			parasite = true;
			ticksUntilParasite = 22;
		}

		if (event.getMessage().toLowerCase().contains("the parasite within you has been weakened") || event.getMessage().toLowerCase().contains("the parasite bursts out of you, fully grown"))
		{
			parasite = false;
		}

		if (event.getMessage().toLowerCase().contains("the nightmare has cursed you, shuffling your prayers!"))
		{
			cursed = true;
		}

		if (event.getMessage().toLowerCase().contains("you feel the effects of the nightmare's curse wear off."))
		{
			cursed = false;
		}

		if (config.yawnInfoBox() && event.getMessage().toLowerCase().contains("the nightmare's spores have infected you, making you feel drowsy!"))
		{
			Timer yawnInfoBox = new Timer(15600L, ChronoUnit.MILLIS, spriteManager.getSprite(SpriteID.SPELL_DREAM, 0), this);
			yawnInfoBox.setTooltip("Yawning");
			infoBoxManager.addInfoBox(yawnInfoBox);
		}
	}

	@Subscribe
	private void onGameTick(final GameTick event)
	{
		if (!inRegion || !inFight || nm == null)
		{
			return;
		}

		//the fight has ended and everything should be reset
		if (nm.getId() == 378 || nm.getId() == 377)
		{
			reset();
		}

		ticksUntilNextAttack--;

		if (ticksUntilParasite > 0)
		{
			ticksUntilParasite--;
			if (ticksUntilParasite == 0)
			{
				parasiteTargets.clear();
			}
		}

		if (pendingNightmareAttack != null && ticksUntilNextAttack <= 3)
		{
			pendingNightmareAttack = null;
		}

		if (shadowsTicks > 0)
		{
			shadowsTicks--;
			if (shadowsTicks == 0)
			{
				shadowsSpawning = false;
				shadows.clear();
			}
		}

		nightmareProjectiles.removeIf(p -> p.getRemainingCycles() <= 0);
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (!inRegion || !inFight || nm == null || event.getMenuEntry().getType() != MenuAction.NPC_SECOND_OPTION)
		{
			return;
		}

		String target = Text.removeTags(event.getTarget()).toLowerCase();

		if ((target.contains("the nightmare") || target.contains("phosani's nightmare"))
			&& ((config.hideAttackNightmareTotems() && totemsAlive > 0)
			|| (config.hideAttackNightmareParasites() && parasites.size() > 0)
			|| (config.hideAttackNightmareHusk() && husks.size() > 0)
			|| (config.hideAttackNightmareSleepwalkers() && nm.getId() != 11154 && sleepwalkers.size() > 0))
			|| (config.hideAttackSleepwalkers() && nm.getId() == 11154 && target.contains("sleepwalker")))
		{
			removeNPCMenuEntry(target);
		}
	}

	private void removeNPCMenuEntry(String target)
	{
		MenuEntry[] menuEntries = client.getMenuEntries();
		MenuEntry[] newEntries = Arrays.stream(menuEntries).filter(e -> e.getNpc() == null || e.getNpc().getName().equalsIgnoreCase(target)).toArray(MenuEntry[]::new);
		if (menuEntries.length != newEntries.length)
		{
			client.setMenuEntries(newEntries);
		}
	}

	private boolean isPhosanis(int id)
	{
		return (id >= 9416 && id <= 9424) || (id >= 11153 && id <= 11155);
	}
	
	private boolean inNightmareRegion()
	{
		return Arrays.stream(client.getMapRegions()).anyMatch(r -> r == NIGHTMARE_REGION_ID);
	}
}
