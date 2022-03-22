package team.cqr.cqrepoured.init;

import static team.cqr.cqrepoured.util.InjectionUtil.Null;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.block.BlockBossBlock;
import team.cqr.cqrepoured.block.BlockDungeonBrick;
import team.cqr.cqrepoured.block.BlockExporter;
import team.cqr.cqrepoured.block.BlockExporterChest;
import team.cqr.cqrepoured.block.BlockExporterChestCQR;
import team.cqr.cqrepoured.block.BlockExporterChestCustom;
import team.cqr.cqrepoured.block.BlockFireCQR;
import team.cqr.cqrepoured.block.BlockForceFieldNexus;
import team.cqr.cqrepoured.block.BlockMapPlaceholder;
import team.cqr.cqrepoured.block.BlockNull;
import team.cqr.cqrepoured.block.BlockPhylactery;
import team.cqr.cqrepoured.block.BlockPillarDungeonBrick;
import team.cqr.cqrepoured.block.BlockSpawner;
import team.cqr.cqrepoured.block.BlockTNTCQR;
import team.cqr.cqrepoured.block.BlockTable;
import team.cqr.cqrepoured.block.BlockTemporaryWeb;
import team.cqr.cqrepoured.block.BlockUnlitTorch;
import team.cqr.cqrepoured.tileentity.TileEntityBoss;
import team.cqr.cqrepoured.tileentity.TileEntityExporter;
import team.cqr.cqrepoured.tileentity.TileEntityExporterChestCQR;
import team.cqr.cqrepoured.tileentity.TileEntityExporterChestCustom;
import team.cqr.cqrepoured.tileentity.TileEntityForceFieldNexus;
import team.cqr.cqrepoured.tileentity.TileEntityMap;
import team.cqr.cqrepoured.tileentity.TileEntitySpawner;
import team.cqr.cqrepoured.tileentity.TileEntityTable;

@ObjectHolder(CQRMain.MODID)
public class CQRBlocks {

	// Dungeon Blocks
	// Andesite
	public static final BlockDungeonBrick ANDESITE_CARVED = Null();
	public static final BlockPillarDungeonBrick ANDESITE_PILLAR = Null();
	public static final BlockDungeonBrick ANDESITE_CUBE = Null();
	public static final BlockDungeonBrick ANDESITE_SCALE = Null();
	public static final BlockDungeonBrick ANDESITE_SQUARE = Null();
	public static final BlockDungeonBrick ANDESITE_SMALL = Null();
	public static final BlockDungeonBrick ANDESITE_LARGE = Null();

	// Diorite
	public static final BlockDungeonBrick DIORITE_CARVED = Null();
	public static final BlockPillarDungeonBrick DIORITE_PILLAR = Null();
	public static final BlockDungeonBrick DIORITE_CUBE = Null();
	public static final BlockDungeonBrick DIORITE_SCALE = Null();
	public static final BlockDungeonBrick DIORITE_SQUARE = Null();
	public static final BlockDungeonBrick DIORITE_SMALL = Null();
	public static final BlockDungeonBrick DIORITE_LARGE = Null();

	// Granite
	public static final BlockDungeonBrick GRANITE_CARVED = Null();
	public static final BlockPillarDungeonBrick GRANITE_PILLAR = Null();
	public static final BlockDungeonBrick GRANITE_CUBE = Null();
	public static final BlockDungeonBrick GRANITE_SCALE = Null();
	public static final BlockDungeonBrick GRANITE_SQUARE = Null();
	public static final BlockDungeonBrick GRANITE_SMALL = Null();
	public static final BlockDungeonBrick GRANITE_LARGE = Null();

	// Prismarine
	public static final BlockDungeonBrick PRISMARINE_CARVED = Null();
	public static final BlockPillarDungeonBrick PRISMARINE_PILLAR = Null();
	public static final BlockDungeonBrick PRISMARINE_CUBE = Null();
	public static final BlockDungeonBrick PRISMARINE_SQUARE = Null();
	public static final BlockDungeonBrick PRISMARINE_SMALL = Null();
	public static final BlockDungeonBrick PRISMARINE_LARGE = Null();

	// Endstone
	public static final BlockDungeonBrick ENDSTONE_CARVED = Null();
	public static final BlockPillarDungeonBrick ENDSTONE_PILLAR = Null();
	public static final BlockDungeonBrick ENDSTONE_CUBE = Null();
	public static final BlockDungeonBrick ENDSTONE_SCALE = Null();
	public static final BlockDungeonBrick ENDSTONE_SQUARE = Null();
	public static final BlockDungeonBrick ENDSTONE_SMALL = Null();

	// Purpur
	public static final BlockDungeonBrick PURPUR_CARVED = Null();
	public static final BlockDungeonBrick PURPUR_CUBE = Null();
	public static final BlockDungeonBrick PURPUR_SCALE = Null();
	public static final BlockDungeonBrick PURPUR_SMALL = Null();
	public static final BlockDungeonBrick PURPUR_LARGE = Null();

	// Red Netherbrick
	public static final BlockDungeonBrick RED_NETHERBRICK_CARVED = Null();
	public static final BlockPillarDungeonBrick RED_NETHERBRICK_PILLAR = Null();
	public static final BlockDungeonBrick RED_NETHERBRICK_CUBE = Null();
	public static final BlockDungeonBrick RED_NETHERBRICK_SCALE = Null();
	public static final BlockDungeonBrick RED_NETHERBRICK_SQUARE = Null();
	public static final BlockDungeonBrick RED_NETHERBRICK_LARGE = Null();

	// Stone
	public static final BlockPillarDungeonBrick STONE_PILLAR = Null();
	public static final BlockDungeonBrick STONE_CUBE = Null();
	public static final BlockDungeonBrick STONE_SCALE = Null();
	public static final BlockDungeonBrick STONE_SQUARE = Null();
	public static final BlockDungeonBrick STONE_SMALL = Null();

	// Other
	public static final BlockTable TABLE_OAK = Null();
	public static final BlockTable TABLE_SPRUCE = Null();
	public static final BlockTable TABLE_BIRCH = Null();
	public static final BlockTable TABLE_JUNGLE = Null();
	public static final BlockTable TABLE_ACACIA = Null();
	public static final BlockTable TABLE_DARK = Null();
	public static final BlockUnlitTorch UNLIT_TORCH = Null();

	// Creative
	public static final BlockExporter EXPORTER = Null();
	public static final BlockNull NULL_BLOCK = Null();
	public static final BlockSpawner SPAWNER = Null();
	public static final BlockBossBlock BOSS_BLOCK = Null();
	public static final BlockForceFieldNexus FORCE_FIELD_NEXUS = Null();
	public static final BlockMapPlaceholder MAP_PLACEHOLDER = Null();
	public static final BlockTNTCQR TNT = Null();

	// Loot Chests
	// CQR
	public static final BlockExporterChest EXPORTER_CHEST_VALUABLE = Null();
	public static final BlockExporterChest EXPORTER_CHEST_FOOD = Null();
	public static final BlockExporterChest EXPORTER_CHEST_EQUIPMENT = Null();
	public static final BlockExporterChest EXPORTER_CHEST_UTILITY = Null();
	public static final BlockExporterChest EXPORTER_CHEST_CLUTTER = Null();

	// Custom
	public static final BlockExporterChest EXPORTER_CHEST_CUSTOM = Null();

	// Vanilla
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_BLACKSMITH = Null();
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_BONUS = Null();
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_DUNGEON = Null();
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_END_CITY = Null();
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_IGLOO = Null();
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_JUNGLE = Null();
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_JUNGLE_DISPENSER = Null();
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_MANSION = Null();
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_MINESHAFT = Null();
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_NETHER = Null();
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_PYRAMID = Null();
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_STRONGHOLD = Null();
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_STRONGHOLD_LIBRARY = Null();
	public static final BlockExporterChest EXPORTER_CHEST_VANILLA_STRONGHOLD_STOREROOM = Null();

	// Technical
	public static final BlockPhylactery PHYLACTERY = Null();
	public static final BlockTemporaryWeb TEMPORARY_WEB = Null();

	@EventBusSubscriber(modid = CQRMain.MODID)
	public static class EventHandler {

		public static final List<Block> BLOCKS = new ArrayList<>();
		public static final List<ItemBlock> ITEM_BLOCKS = new ArrayList<>();

		@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event) {
			final Block[] blocks = {
					setBlockName(new BlockDungeonBrick(), "andesite_carved"),
					setBlockName(new BlockPillarDungeonBrick(), "andesite_pillar"),
					setBlockName(new BlockDungeonBrick(), "andesite_cube"),
					setBlockName(new BlockDungeonBrick(), "andesite_scale"),
					setBlockName(new BlockDungeonBrick(), "andesite_square"),
					setBlockName(new BlockDungeonBrick(), "andesite_small"),
					setBlockName(new BlockDungeonBrick(), "andesite_large"),

					setBlockName(new BlockDungeonBrick(), "diorite_carved"),
					setBlockName(new BlockPillarDungeonBrick(), "diorite_pillar"),
					setBlockName(new BlockDungeonBrick(), "diorite_cube"),
					setBlockName(new BlockDungeonBrick(), "diorite_scale"),
					setBlockName(new BlockDungeonBrick(), "diorite_square"),
					setBlockName(new BlockDungeonBrick(), "diorite_small"),
					setBlockName(new BlockDungeonBrick(), "diorite_large"),

					setBlockName(new BlockDungeonBrick(), "granite_carved"),
					setBlockName(new BlockPillarDungeonBrick(), "granite_pillar"),
					setBlockName(new BlockDungeonBrick(), "granite_cube"),
					setBlockName(new BlockDungeonBrick(), "granite_scale"),
					setBlockName(new BlockDungeonBrick(), "granite_square"),
					setBlockName(new BlockDungeonBrick(), "granite_small"),
					setBlockName(new BlockDungeonBrick(), "granite_large"),

					setBlockName(new BlockDungeonBrick(), "prismarine_carved"),
					setBlockName(new BlockPillarDungeonBrick(), "prismarine_pillar"),
					setBlockName(new BlockDungeonBrick(), "prismarine_cube"),
					setBlockName(new BlockDungeonBrick(), "prismarine_square"),
					setBlockName(new BlockDungeonBrick(), "prismarine_small"),
					setBlockName(new BlockDungeonBrick(), "prismarine_large"),

					setBlockName(new BlockDungeonBrick(), "endstone_carved"),
					setBlockName(new BlockPillarDungeonBrick(), "endstone_pillar"),
					setBlockName(new BlockDungeonBrick(), "endstone_cube"),
					setBlockName(new BlockDungeonBrick(), "endstone_scale"),
					setBlockName(new BlockDungeonBrick(), "endstone_square"),
					setBlockName(new BlockDungeonBrick(), "endstone_small"),

					setBlockName(new BlockDungeonBrick(), "purpur_carved"),
					setBlockName(new BlockDungeonBrick(), "purpur_cube"),
					setBlockName(new BlockDungeonBrick(), "purpur_scale"),
					setBlockName(new BlockDungeonBrick(), "purpur_small"),
					setBlockName(new BlockDungeonBrick(), "purpur_large"),

					setBlockName(new BlockDungeonBrick(), "red_netherbrick_carved"),
					setBlockName(new BlockPillarDungeonBrick(), "red_netherbrick_pillar"),
					setBlockName(new BlockDungeonBrick(), "red_netherbrick_cube"),
					setBlockName(new BlockDungeonBrick(), "red_netherbrick_scale"),
					setBlockName(new BlockDungeonBrick(), "red_netherbrick_square"),
					setBlockName(new BlockDungeonBrick(), "red_netherbrick_large"),

					setBlockName(new BlockPillarDungeonBrick(), "stone_pillar"),
					setBlockName(new BlockDungeonBrick(), "stone_cube"),
					setBlockName(new BlockDungeonBrick(), "stone_scale"),
					setBlockName(new BlockDungeonBrick(), "stone_square"),
					setBlockName(new BlockDungeonBrick(), "stone_small"),

					setBlockName(new BlockTable(), "table_oak"),
					setBlockName(new BlockTable(), "table_spruce"),
					setBlockName(new BlockTable(), "table_birch"),
					setBlockName(new BlockTable(), "table_jungle"),
					setBlockName(new BlockTable(), "table_acacia"),
					setBlockName(new BlockTable(), "table_dark"),

					setBlockName(new BlockUnlitTorch(), "unlit_torch"),

					setBlockNameAndTab(new BlockExporter(), "exporter", CQRMain.CQR_CREATIVE_TOOL_TAB),
					setBlockNameAndTab(new BlockNull(), "null_block", CQRMain.CQR_CREATIVE_TOOL_TAB),
					setBlockNameAndTab(new BlockSpawner(), "spawner", CQRMain.CQR_CREATIVE_TOOL_TAB),
					setBlockNameAndTab(new BlockBossBlock(), "boss_block", CQRMain.CQR_CREATIVE_TOOL_TAB),
					setBlockNameAndTab(new BlockForceFieldNexus(), "force_field_nexus", CQRMain.CQR_CREATIVE_TOOL_TAB),
					setBlockNameAndTab(new BlockMapPlaceholder(), "map_placeholder", CQRMain.CQR_CREATIVE_TOOL_TAB),
					setBlockNameAndTab(new BlockTNTCQR(), "tnt", CQRMain.CQR_CREATIVE_TOOL_TAB),

					setBlockNameAndTab(new BlockExporterChestCQR(CQRLoottables.CHESTS_TREASURE, "textures/items/diamond.png"), "exporter_chest_valuable", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(CQRLoottables.CHESTS_FOOD, "textures/items/porkchop_raw.png"), "exporter_chest_food", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(CQRLoottables.CHESTS_EQUIPMENT, "textures/items/iron_pickaxe.png"), "exporter_chest_equipment", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(CQRLoottables.CHESTS_MATERIAL, "textures/items/iron_ingot.png"), "exporter_chest_utility", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(CQRLoottables.CHESTS_CLUTTER, "textures/items/gunpowder.png"), "exporter_chest_clutter", CQRMain.CQR_EXPORTER_CHEST_TAB),

					setBlockNameAndTab(new BlockExporterChestCustom("textures/items/blaze_rod.png"), "exporter_chest_custom", CQRMain.CQR_EXPORTER_CHEST_TAB),

					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_VILLAGE_BLACKSMITH, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_blacksmith.png"), "exporter_chest_vanilla_blacksmith", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_SPAWN_BONUS_CHEST, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_bonus.png"), "exporter_chest_vanilla_bonus", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_SIMPLE_DUNGEON, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_dungeon.png"), "exporter_chest_vanilla_dungeon", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_END_CITY_TREASURE, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_end_city.png"), "exporter_chest_vanilla_end_city", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_IGLOO_CHEST, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_igloo.png"), "exporter_chest_vanilla_igloo", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_JUNGLE_TEMPLE, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_jungle.png"), "exporter_chest_vanilla_jungle", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_jungle_dispenser.png"), "exporter_chest_vanilla_jungle_dispenser", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_WOODLAND_MANSION, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_mansion.png"), "exporter_chest_vanilla_mansion", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_ABANDONED_MINESHAFT, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_mineshaft.png"), "exporter_chest_vanilla_mineshaft", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_NETHER_BRIDGE, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_nether.png"), "exporter_chest_vanilla_nether", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_DESERT_PYRAMID, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_pyramid.png"), "exporter_chest_vanilla_pyramid", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_STRONGHOLD_CORRIDOR, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_stronghold.png"), "exporter_chest_vanilla_stronghold", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_STRONGHOLD_LIBRARY, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_stronghold_library.png"), "exporter_chest_vanilla_stronghold_library", CQRMain.CQR_EXPORTER_CHEST_TAB),
					setBlockNameAndTab(new BlockExporterChestCQR(LootTableList.CHESTS_STRONGHOLD_CROSSING, CQRMain.MODID, "textures/blocks/exporter_chest_overlays/vanilla_stronghold_storeroom.png"), "exporter_chest_vanilla_stronghold_storeroom", CQRMain.CQR_EXPORTER_CHEST_TAB),

					setBlockName(new BlockPhylactery(Material.GLASS), "phylactery"),
					setBlockNameAndTab(new BlockTemporaryWeb(), "temporary_web", null), };

			IForgeRegistry<Block> registry = event.getRegistry();

			for (Block block : blocks) {
				registry.register(block);
				BLOCKS.add(block);
			}

			registry.register(new BlockFireCQR());

			registerTileEntities();
		}

		private static void registerTileEntities() {
			GameRegistry.registerTileEntity(TileEntityExporter.class, new ResourceLocation(CQRMain.MODID, "TileEntityExporter"));
			GameRegistry.registerTileEntity(TileEntityTable.class, new ResourceLocation(CQRMain.MODID, "TileEntityTable"));
			GameRegistry.registerTileEntity(TileEntitySpawner.class, new ResourceLocation(CQRMain.MODID, "TileEntitySpawner"));
			GameRegistry.registerTileEntity(TileEntityForceFieldNexus.class, new ResourceLocation(CQRMain.MODID, "TileEntityForceFieldNexus"));
			GameRegistry.registerTileEntity(TileEntityExporterChestCQR.class, new ResourceLocation(CQRMain.MODID, "TileEntityExporterChestCQR"));
			GameRegistry.registerTileEntity(TileEntityExporterChestCustom.class, new ResourceLocation(CQRMain.MODID, "TileEntityExporterChestCustom"));
			GameRegistry.registerTileEntity(TileEntityBoss.class, new ResourceLocation(CQRMain.MODID, "TileEntityBoss"));
			GameRegistry.registerTileEntity(TileEntityMap.class, new ResourceLocation(CQRMain.MODID, "TileEntityMapPlaceholder"));
		}

		private static Block setBlockName(Block block, String name) {
			return setBlockNameAndTab(block, name, CQRMain.CQR_BLOCKS_TAB);
		}

		private static Block setBlockNameAndTab(Block block, String name, @Nullable CreativeTabs tab) {
			return block.setTranslationKey(name).setRegistryName(CQRMain.MODID, name).setCreativeTab(tab);
		}

		@SubscribeEvent
		public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
			IForgeRegistry<Item> registry = event.getRegistry();

			for (Block block : BLOCKS) {
				if (block.getCreativeTab() == null) {
					continue;
				}
				ItemBlock itemBlock = createItemBlock(block);
				registry.register(itemBlock);
				ITEM_BLOCKS.add(itemBlock);
			}
		}

		private static ItemBlock createItemBlock(Block block) {
			return (ItemBlock) new ItemBlock(block).setTranslationKey(block.getLocalizedName()).setRegistryName(block.getRegistryName());
		}

	}

}
