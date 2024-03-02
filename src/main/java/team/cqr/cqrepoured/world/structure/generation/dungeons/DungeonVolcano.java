package team.cqr.cqrepoured.world.structure.generation.dungeons;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Random;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import team.cqr.cqrepoured.common.io.FileIOUtil;
import team.cqr.cqrepoured.init.CQRBlocks;
import team.cqr.cqrepoured.init.CQRLoottables;
import team.cqr.cqrepoured.util.CQRWeightedRandom;
import team.cqr.cqrepoured.util.PropertyFileHelper;
import team.cqr.cqrepoured.world.structure.generation.generators.stronghold.EStrongholdRoomType;
import team.cqr.cqrepoured.world.structure.generation.generators.volcano.GeneratorVolcano;

/**
 * Copyright (c) 29.04.2019 Developed by DerToaster98 GitHub: https://github.com/DerToaster98
 */
public class DungeonVolcano extends DungeonBase {

	// For smoke:
	// https://github.com/Tropicraft/Tropicraft/blob/1.12.2/src/main/java/net/tropicraft/core/common/block/tileentity/TileEntityVolcano.java
	private ResourceLocation rampMob = new ResourceLocation("minecraft", "zombie");

	private int minHeight = 80;
	private int maxHeight = 100;
	private int innerRadius = 10;
	private double steepness = 0.0000125D;
	private boolean damagedVolcano = true;
	private int maxHoleSize = 8;

	private CQRWeightedRandom<BlockState> volcanoBlocks = new CQRWeightedRandom<>(new CQRWeightedRandom.WeightedObject<>(Blocks.STONE.defaultBlockState(), 1));
	private BlockState lavaBlock = Blocks.LAVA.defaultBlockState();
	private int lavaWeight = 10;
	private BlockState rampBlock = Blocks.NETHERRACK.defaultBlockState();
	private BlockState pillarBlock = CQRBlocks.GRANITE_LARGE.get().defaultBlockState();

	private boolean buildStairwell = true;
	private boolean digEntranceTunnel = true;
	private double chestChance = 0.002D;
	private ResourceLocation[] chestIDs = { CQRLoottables.CHESTS_FOOD, CQRLoottables.CHESTS_MATERIAL, CQRLoottables.CHESTS_EQUIPMENT };

	// Stronghold
	private boolean buildStronghold = true;
	private int minStrongholdFloors = 2;
	private int maxStrongholdFloors = 3;
	private int minStrongholdRadius = 1;
	private int maxStrongholdRadius = 2;
	private int minStrongholdRooms = 15;
	private int maxStrongholdRooms = 46;
	private int roomSizeX = 15;
	private int roomSizeY = 10;
	private int roomSizeZ = 15;
	private File curveENFolder;
	private File curveNEFolder;
	private File curveSEFolder;
	private File curveESFolder;
	private File curveWSFolder;
	private File curveSWFolder;
	private File curveNWFolder;
	private File curveWNFolder;
	private File hallSNFolder;
	private File hallNSFolder;
	private File hallWEFolder;
	private File hallEWFolder;
	private File stairNFolder;
	private File stairEFolder;
	private File stairSFolder;
	private File stairWFolder;
	private File bossFolder;

	public DungeonVolcano(String name, Properties prop) {
		super(name, prop);

		this.rampMob = PropertyFileHelper.getResourceLocationProperty(prop, "rampMob", this.rampMob);

		this.minHeight = PropertyFileHelper.getIntProperty(prop, "minHeight", this.minHeight);
		this.maxHeight = PropertyFileHelper.getIntProperty(prop, "maxHeight", this.maxHeight);
		this.innerRadius = PropertyFileHelper.getIntProperty(prop, "innerRadius", this.innerRadius);
		this.steepness = PropertyFileHelper.getDoubleProperty(prop, "steepness", this.steepness);
		this.damagedVolcano = PropertyFileHelper.getBooleanProperty(prop, "damagedVolcano", this.damagedVolcano);
		this.maxHoleSize = Math.max(PropertyFileHelper.getIntProperty(prop, "maxHoleSize", this.maxHoleSize), 2);

		this.volcanoBlocks = PropertyFileHelper.getWeightedBlockStateList(prop, "volcanoBlocks", this.volcanoBlocks, false);
		this.lavaBlock = PropertyFileHelper.getBlockStateProperty(prop, "lavaBlock", this.lavaBlock);
		this.lavaWeight = PropertyFileHelper.getIntProperty(prop, "lavaWeight", this.lavaWeight);
		this.rampBlock = PropertyFileHelper.getBlockStateProperty(prop, "rampBlock", this.rampBlock);
		this.pillarBlock = PropertyFileHelper.getBlockStateProperty(prop, "pillarBlock", this.pillarBlock);

		this.buildStairwell = PropertyFileHelper.getBooleanProperty(prop, "buildStairwell", this.buildStairwell);
		this.digEntranceTunnel = PropertyFileHelper.getBooleanProperty(prop, "digEntranceTunnel", this.digEntranceTunnel);
		this.chestChance = PropertyFileHelper.getDoubleProperty(prop, "chestChance", this.chestChance);
		this.chestIDs = PropertyFileHelper.getResourceLocationArrayProperty(prop, "chestIDs", this.chestIDs, false);

		// Stronghold
		this.buildStronghold = PropertyFileHelper.getBooleanProperty(prop, "buildStronghold", this.buildStronghold);
		this.minStrongholdFloors = PropertyFileHelper.getIntProperty(prop, "minStrongholdFloors", this.minStrongholdFloors);
		this.maxStrongholdFloors = PropertyFileHelper.getIntProperty(prop, "maxStrongholdFloors", this.maxStrongholdFloors);
		this.minStrongholdRadius = PropertyFileHelper.getIntProperty(prop, "minStrongholdRadius", this.minStrongholdRadius);
		this.maxStrongholdRadius = PropertyFileHelper.getIntProperty(prop, "maxStrongholdRadius", this.maxStrongholdRadius);
		this.minStrongholdRooms = PropertyFileHelper.getIntProperty(prop, "minStrongholdRooms", this.minStrongholdRooms);
		this.maxStrongholdRooms = PropertyFileHelper.getIntProperty(prop, "maxStrongholdRooms", this.maxStrongholdRooms);
		this.roomSizeX = PropertyFileHelper.getIntProperty(prop, "roomSizeX", this.roomSizeX);
		this.roomSizeY = PropertyFileHelper.getIntProperty(prop, "roomSizeY", this.roomSizeY);
		this.roomSizeZ = PropertyFileHelper.getIntProperty(prop, "roomSizeZ", this.roomSizeZ);
		this.curveENFolder = PropertyFileHelper.getStructureFolderProperty(prop, "curveENFolder", "volcano/rooms/curves/EN");
		this.curveESFolder = PropertyFileHelper.getStructureFolderProperty(prop, "curveESFolder", "volcano/rooms/curves/ES");
		this.curveNEFolder = PropertyFileHelper.getStructureFolderProperty(prop, "curveNEFolder", "volcano/rooms/curves/NE");
		this.curveNWFolder = PropertyFileHelper.getStructureFolderProperty(prop, "curveNWFolder", "volcano/rooms/curves/NW");
		this.curveSEFolder = PropertyFileHelper.getStructureFolderProperty(prop, "curveSEFolder", "volcano/rooms/curves/SE");
		this.curveSWFolder = PropertyFileHelper.getStructureFolderProperty(prop, "curveSWFolder", "volcano/rooms/curves/SW");
		this.curveWNFolder = PropertyFileHelper.getStructureFolderProperty(prop, "curveWNFolder", "volcano/rooms/curves/WN");
		this.curveWSFolder = PropertyFileHelper.getStructureFolderProperty(prop, "curveWSFolder", "volcano/rooms/curves/WS");
		this.hallEWFolder = PropertyFileHelper.getStructureFolderProperty(prop, "hallwayEWFolder", "volcano/rooms/hallway/EW");
		this.hallNSFolder = PropertyFileHelper.getStructureFolderProperty(prop, "hallwayNSFolder", "volcano/rooms/hallway/NS");
		this.hallSNFolder = PropertyFileHelper.getStructureFolderProperty(prop, "hallwaySNFolder", "volcano/rooms/hallway/SN");
		this.hallWEFolder = PropertyFileHelper.getStructureFolderProperty(prop, "hallwayWEFolder", "volcano/rooms/hallway/WE");
		this.stairEFolder = PropertyFileHelper.getStructureFolderProperty(prop, "stairEFolder", "volcano/stairs/E");
		this.stairNFolder = PropertyFileHelper.getStructureFolderProperty(prop, "stairNFolder", "volcano/stairs/N");
		this.stairSFolder = PropertyFileHelper.getStructureFolderProperty(prop, "stairSFolder", "volcano/stairs/S");
		this.stairWFolder = PropertyFileHelper.getStructureFolderProperty(prop, "stairWFolder", "volcano/stairs/W");
		this.bossFolder = PropertyFileHelper.getStructureFolderProperty(prop, "bossRoomFolder", "volcano/rooms/boss/");
	}

	@Override
	public Collection<StructurePiece> runGenerator(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGenerator, TemplateManager templateManager, BlockPos pos, Random random) {
		Collection<StructurePiece> pieces = new ArrayList<>();
		pieces.add(new GeneratorVolcano(chunkGenerator, pos, this, random).prepare());
		return pieces;
	}

	public File getRoomNBTFileForType(EStrongholdRoomType type, Random rand) {
		File dir = null;
		switch (type) {
		case BOSS:
			dir = this.bossFolder;
			break;
		case CURVE_EN:
			dir = this.curveENFolder;
			break;
		case CURVE_ES:
			dir = this.curveESFolder;
			break;
		case CURVE_NE:
			dir = this.curveNEFolder;
			break;
		case CURVE_NW:
			dir = this.curveNWFolder;
			break;
		case CURVE_SE:
			dir = this.curveSEFolder;
			break;
		case CURVE_SW:
			dir = this.curveSWFolder;
			break;
		case CURVE_WN:
			dir = this.curveWNFolder;
			break;
		case CURVE_WS:
			dir = this.curveWSFolder;
			break;
		case HALLWAY_EW:
			dir = this.hallEWFolder;
			break;
		case HALLWAY_NS:
			dir = this.hallNSFolder;
			break;
		case HALLWAY_SN:
			dir = this.hallSNFolder;
			break;
		case HALLWAY_WE:
			dir = this.hallWEFolder;
			break;
		case STAIR_EE:
			dir = this.stairEFolder;
			break;
		case STAIR_NN:
			dir = this.stairNFolder;
			break;
		case STAIR_SS:
			dir = this.stairSFolder;
			break;
		case STAIR_WW:
			dir = this.stairWFolder;
			break;
		default:
			break;
		}
		if (dir != null && dir.isDirectory() && dir.list(FileIOUtil.getNBTFileFilter()).length > 0) {
			return this.getStructureFileFromDirectory(dir, rand);
		}
		return null;
	}

	public ResourceLocation getRampMob() {
		return this.rampMob;
	}

	public int getMinHeight() {
		return this.minHeight;
	}

	public int getMaxHeight() {
		return this.maxHeight;
	}

	public int getInnerRadius() {
		return this.innerRadius;
	}

	public double getSteepness() {
		return this.steepness;
	}

	public boolean isVolcanoDamaged() {
		return this.damagedVolcano;
	}

	public int getMaxHoleSize() {
		return this.maxHoleSize;
	}

	public CQRWeightedRandom<BlockState> getVolcanoBlocks() {
		return this.volcanoBlocks;
	}

	public BlockState getLavaBlock() {
		return this.lavaBlock;
	}

	public int getLavaWeight() {
		return this.lavaWeight;
	}

	public BlockState getRampBlock() {
		return this.rampBlock;
	}

	public BlockState getPillarBlock() {
		return this.pillarBlock;
	}

	public boolean doBuildStairs() {
		return this.buildStairwell;
	}

	public double getChestChance() {
		return this.chestChance;
	}

	public ResourceLocation[] getChestIDs() {
		return this.chestIDs;
	}

	public boolean doBuildStronghold() {
		return this.buildStronghold;
	}

	public int getMinStrongholdFloors() {
		return this.minStrongholdFloors;
	}

	public int getMaxStrongholdFloors() {
		return this.maxStrongholdFloors;
	}

	public int getMinStrongholdRadius() {
		return this.minStrongholdRadius;
	}

	public int getMaxStrongholdRadius() {
		return this.maxStrongholdRadius;
	}

	public int getMinStrongholdRooms() {
		return this.minStrongholdRooms;
	}

	public int getMaxStrongholdRooms() {
		return this.maxStrongholdRooms;
	}

	public int getRoomSizeX() {
		return this.roomSizeX;
	}

	public int getRoomSizeY() {
		return this.roomSizeY;
	}

	public int getRoomSizeZ() {
		return this.roomSizeZ;
	}

	public boolean constructEntranceTunnel() {
		return this.digEntranceTunnel;
	}

}
