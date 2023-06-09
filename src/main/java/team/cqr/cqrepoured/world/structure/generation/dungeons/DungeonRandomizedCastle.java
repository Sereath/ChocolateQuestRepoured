//package team.cqr.cqrepoured.world.structure.generation.dungeons;
//
//import java.util.Properties;
//import java.util.Random;
//
//import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
//import net.minecraft.core.BlockPos;
//import net.minecraft.util.registry.DynamicRegistries;
//import net.minecraft.world.gen.ChunkGenerator;
//import net.minecraft.world.gen.feature.structure.StructurePiece;
//import net.minecraft.world.gen.feature.template.TemplateManager;
//import team.cqr.cqrepoured.util.CQRWeightedRandom;
//import team.cqr.cqrepoured.util.DungeonGenUtils;
//import team.cqr.cqrepoured.util.EnumMCWoodType;
//import team.cqr.cqrepoured.util.PropertyFileHelper;
//import team.cqr.cqrepoured.world.structure.generation.generators.GeneratorRandomizedCastle;
//import team.cqr.cqrepoured.world.structure.generation.generators.castleparts.RandomCastleConfigOptions;
//import team.cqr.cqrepoured.world.structure.generation.generators.castleparts.rooms.EnumRoomType;
//
///**
// * Copyright (c) 20.04.2020 Developed by KalgogSmash GitHub: https://github.com/KalgogSmash
// */
//public class DungeonRandomizedCastle extends DungeonBase {
//
//	private int maxSize;
//	private int roomSize;
//	private int floorHeight;
//	private BlockState mainBlock;
//	private BlockState fancyBlock;
//	private BlockState slabBlock;
//	private BlockState stairBlock;
//	private BlockState roofBlock;
//	private BlockState fenceBlock;
//	private BlockState floorBlock;
//	private BlockState woodStairBlock;
//	private BlockState woodSlabBlock;
//	private BlockState plankBlock;
//	private BlockState doorBlock;
//
//	private CQRWeightedRandom<RandomCastleConfigOptions.RoofType> roofTypeRandomizer;
//	private CQRWeightedRandom<RandomCastleConfigOptions.RoofType> towerRoofTypeRandomizer;
//	private CQRWeightedRandom<RandomCastleConfigOptions.WindowType> windowTypeRandomizer;
//	private CQRWeightedRandom<EnumRoomType> roomRandomizer;
//
//	private int minSpawnerRolls;
//	private int maxSpawnerRolls;
//	private int spawnerRollChance;
//
//	private int minBridgeLength;
//	private int maxBridgeLength;
//	private int bridgeChance;
//
//	private int paintingChance;
//
//	public DungeonRandomizedCastle(String name, Properties prop) {
//		super(name, prop);
//
//		this.maxSize = PropertyFileHelper.getIntProperty(prop, "maxSize", 60);
//		this.roomSize = PropertyFileHelper.getIntProperty(prop, "roomSize", 10);
//		this.floorHeight = PropertyFileHelper.getIntProperty(prop, "floorHeight", 8);
//
//		EnumMCWoodType woodType = PropertyFileHelper.getWoodTypeProperty(prop, "woodType", EnumMCWoodType.OAK);
//		this.mainBlock = PropertyFileHelper.getBlockStateProperty(prop, "mainBlock", Blocks.STONE_BRICKS.defaultBlockState());
//		this.stairBlock = PropertyFileHelper.getBlockStateProperty(prop, "stairBlock", Blocks.STONE_BRICK_STAIRS.defaultBlockState());
//		this.slabBlock = PropertyFileHelper.getBlockStateProperty(prop, "slabBlock", Blocks.STONE_SLAB.defaultBlockState());
//		this.fancyBlock = PropertyFileHelper.getBlockStateProperty(prop, "fancyBlock", Blocks.STONE_BRICKS.defaultBlockState());
//		this.floorBlock = PropertyFileHelper.getBlockStateProperty(prop, "floorBlock", woodType.getPlankBlockState());
//		this.roofBlock = PropertyFileHelper.getBlockStateProperty(prop, "roofBlock", woodType.getStairBlockState());
//		this.fenceBlock = PropertyFileHelper.getBlockStateProperty(prop, "fenceBlock", woodType.getFenceBlockState());
//		this.woodStairBlock = PropertyFileHelper.getBlockStateProperty(prop, "woodStairBlock", woodType.getStairBlockState());
//		this.woodSlabBlock = PropertyFileHelper.getBlockStateProperty(prop, "woodSlabBlock", woodType.getSlabBlockState());
//		this.plankBlock = PropertyFileHelper.getBlockStateProperty(prop, "plankBlock", woodType.getPlankBlockState());
//		this.doorBlock = PropertyFileHelper.getBlockStateProperty(prop, "doorBlock", woodType.getDoorBlockState());
//
//		this.roomRandomizer = new CQRWeightedRandom<>();
//		int weight = PropertyFileHelper.getIntProperty(prop, "roomWeightAlchemyLab", 1);
//		this.roomRandomizer.add(EnumRoomType.ALCHEMY_LAB, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "roomWeightArmory", 1);
//		this.roomRandomizer.add(EnumRoomType.ARMORY, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "roomWeightBedroomBasic", 1);
//		this.roomRandomizer.add(EnumRoomType.BEDROOM_BASIC, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "roomWeightBedroomFancy", 1);
//		this.roomRandomizer.add(EnumRoomType.BEDROOM_FANCY, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "roomWeightKitchen", 1);
//		this.roomRandomizer.add(EnumRoomType.KITCHEN, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "roomWeightLibrary", 1);
//		this.roomRandomizer.add(EnumRoomType.LIBRARY, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "roomWeightPool", 1);
//		this.roomRandomizer.add(EnumRoomType.POOL, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "roomWeightPortal", 1);
//		this.roomRandomizer.add(EnumRoomType.PORTAL, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "roomWeightJailCell", 1);
//		this.roomRandomizer.add(EnumRoomType.JAIL, weight);
//
//		this.roofTypeRandomizer = new CQRWeightedRandom<>();
//		weight = PropertyFileHelper.getIntProperty(prop, "roofWeightTwoSided", 1);
//		this.roofTypeRandomizer.add(RandomCastleConfigOptions.RoofType.TWO_SIDED, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "roofWeightFourSided", 1);
//		this.roofTypeRandomizer.add(RandomCastleConfigOptions.RoofType.FOUR_SIDED, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "roofWeightSpire", 0);
//		this.roofTypeRandomizer.add(RandomCastleConfigOptions.RoofType.SPIRE, weight);
//
//		this.towerRoofTypeRandomizer = new CQRWeightedRandom<>();
//		weight = PropertyFileHelper.getIntProperty(prop, "towerRoofWeightTwoSided", 1);
//		this.towerRoofTypeRandomizer.add(RandomCastleConfigOptions.RoofType.TWO_SIDED, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "towerRoofWeightFourSided", 1);
//		this.towerRoofTypeRandomizer.add(RandomCastleConfigOptions.RoofType.FOUR_SIDED, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "towerRoofWeightSpire", 2);
//		this.towerRoofTypeRandomizer.add(RandomCastleConfigOptions.RoofType.SPIRE, weight);
//
//		this.windowTypeRandomizer = new CQRWeightedRandom<>();
//		weight = PropertyFileHelper.getIntProperty(prop, "windowWeightBasicGlass", 1);
//		this.windowTypeRandomizer.add(RandomCastleConfigOptions.WindowType.BASIC_GLASS, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "windowWeightCrossGlass", 1);
//		this.windowTypeRandomizer.add(RandomCastleConfigOptions.WindowType.CROSS_GLASS, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "windowWeightSquareBars", 1);
//		this.windowTypeRandomizer.add(RandomCastleConfigOptions.WindowType.SQUARE_BARS, weight);
//		weight = PropertyFileHelper.getIntProperty(prop, "windowWeightOpenSlit", 1);
//		this.windowTypeRandomizer.add(RandomCastleConfigOptions.WindowType.OPEN_SLIT, weight);
//
//		this.minSpawnerRolls = PropertyFileHelper.getIntProperty(prop, "minSpawnerRolls", 1);
//		this.maxSpawnerRolls = PropertyFileHelper.getIntProperty(prop, "maxSpawnerRolls", 3);
//		this.spawnerRollChance = PropertyFileHelper.getIntProperty(prop, "spawnerRollChance", 100);
//
//		this.minBridgeLength = PropertyFileHelper.getIntProperty(prop, "minBridgeLength", 2);
//		this.maxBridgeLength = PropertyFileHelper.getIntProperty(prop, "maxBridgeLength", 4);
//		this.bridgeChance = PropertyFileHelper.getIntProperty(prop, "bridgeChance", 25);
//
//		this.paintingChance = PropertyFileHelper.getIntProperty(prop, "paintingChance", 0);
//	}
//
//	@Override
//	public StructurePiece runGenerator(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGenerator, TemplateManager templateManager, BlockPos pos, Random random) {
//		return new GeneratorRandomizedCastle(chunkGenerator, pos, this, random).prepare();
//	}
//
//	public BlockState getMainBlockState() {
//		return this.mainBlock;
//	}
//
//	public BlockState getFancyBlockState() {
//		return this.fancyBlock;
//	}
//
//	public BlockState getSlabBlockState() {
//		return this.slabBlock;
//	}
//
//	public BlockState getStairBlockState() {
//		return this.stairBlock;
//	}
//
//	public BlockState getFloorBlockState() {
//		return this.floorBlock;
//	}
//
//	public BlockState getRoofBlockState() {
//		return this.roofBlock;
//	}
//
//	public BlockState getFenceBlockState() {
//		return this.fenceBlock;
//	}
//
//	public BlockState getWoodStairBlockState() {
//		return this.woodStairBlock;
//	}
//
//	public BlockState getWoodSlabBlockState() {
//		return this.woodSlabBlock;
//	}
//
//	public BlockState getPlankBlockState() {
//		return this.plankBlock;
//	}
//
//	public BlockState getDoorBlockState() {
//		return this.doorBlock;
//	}
//
//	public int getMaxSize() {
//		return this.maxSize;
//	}
//
//	public int getRoomSize() {
//		return this.roomSize;
//	}
//
//	public int getFloorHeight() {
//		return this.floorHeight;
//	}
//
//	public EnumRoomType getRandomRoom(Random rand) {
//		return this.roomRandomizer.next(rand);
//	}
//
//	public RandomCastleConfigOptions.RoofType getRandomRoofType(Random rand) {
//		return this.roofTypeRandomizer.next(rand);
//	}
//
//	public RandomCastleConfigOptions.RoofType getRandomTowerRoofType(Random rand) {
//		return this.towerRoofTypeRandomizer.next(rand);
//	}
//
//	public RandomCastleConfigOptions.WindowType getRandomWindowType(Random rand) {
//		return this.windowTypeRandomizer.next(rand);
//	}
//
//	public int getMinBridgeLength() {
//		return this.minBridgeLength;
//	}
//
//	public int getMaxBridgeLength() {
//		return this.maxBridgeLength;
//	}
//
//	public int getBridgeChance() {
//		return this.bridgeChance;
//	}
//
//	public int getPaintingChance() {
//		return this.paintingChance;
//	}
//
//	public int randomizeRoomSpawnerCount(Random rand) {
//		int numRolls;
//		int result = 0;
//		if (this.minSpawnerRolls >= this.maxSpawnerRolls) {
//			numRolls = this.minSpawnerRolls;
//		} else {
//			numRolls = DungeonGenUtils.randomBetween(this.minSpawnerRolls, this.maxSpawnerRolls, rand);
//		}
//
//		for (int i = 0; i < numRolls; i++) {
//			if (DungeonGenUtils.percentageRandom(this.spawnerRollChance, rand)) {
//				result++;
//			}
//		}
//
//		return result;
//	}
//}
