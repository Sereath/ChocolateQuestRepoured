package team.cqr.cqrepoured.init;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import team.cqr.cqrepoured.CQRMain;

public class CQRLoottables {

	private static final Set<ResourceLocation> LOOT_TABLES = new HashSet<>();
	private static final Set<ResourceLocation> CHEST_LOOT_TABLES = new HashSet<>();
	private static final Set<ResourceLocation> ENTITY_LOOT_TABLES = new HashSet<>();

	public static final ResourceLocation ENTITIES_BOARMAN = createEntityLootTable("boarman");
	public static final ResourceLocation ENTITIES_DWARF = createEntityLootTable("dwarf");
	public static final ResourceLocation ENTITIES_ENDERMAN = createEntityLootTable("enderman");
	public static final ResourceLocation ENTITIES_GOBLIN = createEntityLootTable("goblin");
	public static final ResourceLocation ENTITIES_GOLEM = createEntityLootTable("golem");
	public static final ResourceLocation ENTITIES_GREMLIN = createEntityLootTable("gremlin");
	public static final ResourceLocation ENTITIES_HUMAN = createEntityLootTable("human");
	public static final ResourceLocation ENTITIES_ILLAGER = createEntityLootTable("illager");
	public static final ResourceLocation ENTITIES_MANDRIL = createEntityLootTable("mandril");
	public static final ResourceLocation ENTITIES_MINOTAUR = createEntityLootTable("minotaur");
	public static final ResourceLocation ENTITIES_MUMMY = createEntityLootTable("mummy");
	public static final ResourceLocation ENTITIES_NPC = createEntityLootTable("npc");
	public static final ResourceLocation ENTITIES_OGRE = createEntityLootTable("ogre");
	public static final ResourceLocation ENTITIES_ORC = createEntityLootTable("orc");
	public static final ResourceLocation ENTITIES_PIRATE = createEntityLootTable("pirate");
	public static final ResourceLocation ENTITIES_SKELETON = createEntityLootTable("skeleton");
	public static final ResourceLocation ENTITIES_SPECTRE = createEntityLootTable("spectre");
	public static final ResourceLocation ENTITIES_TRITON = createEntityLootTable("triton");
	public static final ResourceLocation ENTITIES_WALKER = createEntityLootTable("walker");
	public static final ResourceLocation ENTITIES_ZOMBIE = createEntityLootTable("zombie");

	public static final ResourceLocation ENTITIES_BEE = createEntityLootTable("mounts/bee");
	public static final ResourceLocation ENTITIES_GIANT_ENDERMITE = createEntityLootTable("mounts/endermite");
	public static final ResourceLocation ENTITIES_POLLO = createEntityLootTable("mounts/pollo");
	public static final ResourceLocation ENTITIES_GIANT_SILVERFISH_GREEN = createEntityLootTable("mounts/silverfish_green");
	public static final ResourceLocation ENTITIES_GIANT_SILVERFISH_RED = createEntityLootTable("mounts/silverfish_red");
	public static final ResourceLocation ENTITIES_GIANT_SILVERFISH = createEntityLootTable("mounts/silverfish");

	public static final ResourceLocation ENTITIES_BOARMAGE = createEntityLootTable("bosses/boar_mage");
	public static final ResourceLocation ENTITIES_BULL_ICE = createEntityLootTable("bosses/bull_ice");
	public static final ResourceLocation ENTITIES_BULL = createEntityLootTable("bosses/bull");
	public static final ResourceLocation ENTITIES_DRAGON_LAND = createEntityLootTable("bosses/dragon_land");
	public static final ResourceLocation ENTITIES_DRAGON_NETHER = createEntityLootTable("bosses/dragon_nether");
	public static final ResourceLocation ENTITIES_DRAGON = createEntityLootTable("bosses/dragon_normal");
	public static final ResourceLocation ENTITIES_DWARF_ENGINEER = createEntityLootTable("bosses/dwarf_engineer");
	public static final ResourceLocation ENTITIES_ENDER_CALAMITY = createEntityLootTable("bosses/ender_calamity");
	public static final ResourceLocation ENTITIES_TURTLE = createEntityLootTable("bosses/giant_turtle");
	public static final ResourceLocation ENTITIES_GOBLIN_SHAMAN = createEntityLootTable("bosses/goblin_shaman");
	public static final ResourceLocation ENTITIES_EXTERMINATOR = createEntityLootTable("bosses/golem_bosses");
	public static final ResourceLocation ENTITIES_LICH = createEntityLootTable("bosses/lich");
	public static final ResourceLocation ENTITIES_MONKING = createEntityLootTable("bosses/monking");
	public static final ResourceLocation ENTITIES_NECROMANCER = createEntityLootTable("bosses/necromancer");
	public static final ResourceLocation ENTITIES_PHARAO = createEntityLootTable("bosses/pharao");
	public static final ResourceLocation ENTITIES_PIRATE_CAPTAIN = createEntityLootTable("bosses/pirate_captain");
	public static final ResourceLocation ENTITIES_SECRET = createEntityLootTable("bosses/secret_bosses");
	public static final ResourceLocation ENTITIES_SPIDER = createEntityLootTable("bosses/shelob");
	public static final ResourceLocation ENTITIES_SLIME = createEntityLootTable("bosses/slime_frog");
	public static final ResourceLocation ENTITIES_SPECTRE_LORD = createEntityLootTable("bosses/spectre_lord");
	public static final ResourceLocation ENTITIES_SPHINX = createEntityLootTable("bosses/sphinx_bosses");
	public static final ResourceLocation ENTITIES_WALKER_KING = createEntityLootTable("bosses/walker_king");
	public static final ResourceLocation ENTITIES_WALKER_QUEEN = createEntityLootTable("bosses/walker_queen");

	public static final ResourceLocation CHESTS_TREASURE = createChestLootTable("treasure");
	public static final ResourceLocation CHESTS_EQUIPMENT = createChestLootTable("equipment");
	public static final ResourceLocation CHESTS_FOOD = createChestLootTable("food");
	public static final ResourceLocation CHESTS_MATERIAL = createChestLootTable("material");
	public static final ResourceLocation CHESTS_CLUTTER = createChestLootTable("clutter");

	public static ResourceLocation createChestLootTable(String name) {
		ResourceLocation loc = createLootTable("chests/" + name);
		CHEST_LOOT_TABLES.add(loc);
		return loc;
	}

	public static ResourceLocation createEntityLootTable(String name) {
		ResourceLocation loc = createLootTable("entities/" + name);
		ENTITY_LOOT_TABLES.add(loc);
		return loc;
	}

	public static ResourceLocation createLootTable(String name) {
		ResourceLocation lootTable = new ResourceLocation(CQRMain.MODID, name);
		LOOT_TABLES.add(lootTable);
		return lootTable;
	}

	public static void registerLootTables() {
		for (ResourceLocation lootTable : LOOT_TABLES) {
			LootTableList.register(lootTable);
		}
	}

	public static Set<ResourceLocation> getLootTables() {
		return Collections.unmodifiableSet(LOOT_TABLES);
	}

	public static Set<ResourceLocation> getChestLootTables() {
		return Collections.unmodifiableSet(CHEST_LOOT_TABLES);
	}

	public static Set<ResourceLocation> getEntityLootTables() {
		return Collections.unmodifiableSet(ENTITY_LOOT_TABLES);
	}

}
