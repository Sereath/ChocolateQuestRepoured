package team.cqr.cqrepoured.world.structure.generation.generators.stronghold;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import team.cqr.cqrepoured.util.DungeonGenUtils;
import team.cqr.cqrepoured.util.ESkyDirection;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonStrongholdLinear;
import team.cqr.cqrepoured.world.structure.generation.generators.LegacyDungeonGenerator;
import team.cqr.cqrepoured.world.structure.generation.generators.stronghold.linear.StrongholdFloor;
import team.cqr.cqrepoured.world.structure.generation.inhabitants.DungeonInhabitant;
import team.cqr.cqrepoured.world.structure.generation.inhabitants.DungeonInhabitantManager;
import team.cqr.cqrepoured.world.structure.generation.structurefile.CQStructure;
import team.cqr.cqrepoured.world.structure.generation.structurefile.Offset;

/**
 * Copyright (c) 29.04.2019 Developed by DerToaster98 GitHub: https://github.com/DerToaster98
 */
public class GeneratorStronghold extends LegacyDungeonGenerator<DungeonStrongholdLinear> {

	private int dunX;
	private int dunZ;

	private StrongholdFloor[] floors;

	public GeneratorStronghold(ChunkGenerator world, BlockPos pos, DungeonStrongholdLinear dungeon, Random rand) {
		super(world, pos, dungeon, rand);
	}

	@Override
	public void preProcess() {
		// calculates the positions for rooms, stairs, bossroom, entrance, entrance stairs
		int count = DungeonGenUtils.randomBetween(this.dungeon.getMinFloors(), this.dungeon.getMaxFloors(), this.random);
		int floorSize = this.dungeon.getFloorSize(this.random);
		this.floors = new StrongholdFloor[count];
		this.dunX = this.pos.getX();
		this.dunZ = this.pos.getZ();

		int sX = 0;
		int sZ = 0;
		ESkyDirection exitDir = ESkyDirection.values()[this.random.nextInt(ESkyDirection.values().length)];
		for (int i = 0; i < this.floors.length; i++) {
			// System.out.println("Calculating floor" + (i+1));
			StrongholdFloor floor = new StrongholdFloor(floorSize, this, i == (this.floors.length - 1), this.random);
			floor.generateRoomPattern(sX, sZ, exitDir);
			this.floors[i] = floor;
			exitDir = floor.getExitDirection();
			sX = floor.getLastRoomGridPos().getA();
			sZ = floor.getLastRoomGridPos().getB();
		}
	}

	@Override
	public void buildStructure() {
		// places the structures
		// CQStructure entranceStair = new CQStructure(dungeon.getEntranceStairRoom(), dungeon, dunX, dunZ,
		// dungeon.isProtectedFromModifications());
		// initPos = initPos.subtract(new Vec3i(0,entranceStair.getSizeY(),0));

		int y = this.pos.getY();
		DungeonInhabitant mobType = DungeonInhabitantManager.instance().getInhabitantByDistanceIfDefault(this.dungeon.getDungeonMob(), this.level, this.pos.getX(), this.pos.getZ());
		PlacementSettings settings = new PlacementSettings();
		CQStructure structureStair = CQStructure.createFromFile(this.dungeon.getEntranceStairRoom(this.random));
		CQStructure structureEntrance = CQStructure.createFromFile(this.dungeon.getEntranceBuilding(this.random));

		int segCount = 0;
		CQStructure stairSeg = null;
		if (this.dungeon.useStairSegments()) {
			int ySurface = y;

			int yTmp = 3;
			yTmp += (this.floors.length - 1) * this.dungeon.getRoomSizeY();
			yTmp += structureStair.getSize().getY();

			if (yTmp < ySurface) {
				y = yTmp;
				stairSeg = CQStructure.createFromFile(this.dungeon.getEntranceStairSegment(this.random));
				while (y < ySurface) {
					segCount++;
					y += stairSeg.getSize().getY();
				}
			}
		}

		if (this.dungeon.doBuildSupportPlatform()) {
			//TODO: To be remade
			/*PlateauDungeonPart.Builder partBuilder = new PlateauDungeonPart.Builder(
					this.pos.getX() + 4 + structureEntrance.getSize().getX() / 2,
					this.pos.getZ() + 4 + structureEntrance.getSize().getZ() / 2,
					this.pos.getX() - 4 - structureEntrance.getSize().getX() / 2,
					y + this.dungeon.getUnderGroundOffset() - 1,
					this.pos.getZ() - 4 - structureEntrance.getSize().getZ() / 2,
					CQRConfig.general.supportHillWallSize);
			partBuilder.setSupportHillBlock(this.dungeon.getSupportBlock());
			partBuilder.setSupportHillTopBlock(this.dungeon.getSupportTopBlock());
			this.dungeonBuilder.add(partBuilder);*/
		}
		structureEntrance.addAll(this.dungeonBuilder, new BlockPos(this.pos.getX(), y, this.pos.getZ()), Offset.CENTER);

		if (segCount > 0) {
			while (segCount > 0) {
				segCount--;
				y -= stairSeg.getSize().getY();
				stairSeg.addAll(this.dungeonBuilder, new BlockPos(this.pos.getX(), y, this.pos.getZ()), Offset.CENTER);
			}
		}

		int yFloor = y;
		yFloor -= structureStair.getSize().getY();
		structureStair.addAll(this.dungeonBuilder, new BlockPos(this.pos.getX(), yFloor, this.pos.getZ()), Offset.CENTER);

		for (int i = 0; i < this.floors.length; i++) {
			StrongholdFloor floor = this.floors[i];

			floor.generateRooms(this.pos.getX(), this.pos.getZ(), yFloor, settings, this.dungeonBuilder, this.level, mobType);
			yFloor -= this.dungeon.getRoomSizeY();
			// initPos = floor.getLastRoomPastePos(initPos, this.dungeon).add(0, this.dungeon.getRoomSizeY(), 0);
		}
	}

	@Override
	public void postProcess() {
		// Constructs walls around the rooms ? #TODO
	}

	public int getDunX() {
		return this.dunX;
	}

	public int getDunZ() {
		return this.dunZ;
	}

}
