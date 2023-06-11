//package team.cqr.cqrepoured.world.structure.generation.generators.castleparts.rooms;
//
//import net.minecraft.world.level.block.BlockState;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.StairsBlock;
//import net.minecraft.util.Direction;
//import net.minecraft.core.BlockPos;
//import team.cqr.cqrepoured.util.BlockStateGenArray;
//import team.cqr.cqrepoured.util.DungeonGenUtils;
//import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonRandomizedCastle;
//
//import java.util.Random;
//
//public class CastleRoomStaircaseDirected extends CastleRoomBase {
//	private static final int PLATFORM_LENGTH = 2;
//	private Direction doorSide;
//	private int numRotations;
//	private int upperStairWidth;
//	private int upperStairLength;
//	private int centerStairWidth;
//	private int centerStairLength;
//
//	public CastleRoomStaircaseDirected(int sideLength, int height, Direction doorSide, int floor, Random rand) {
//		super(sideLength, height, floor, rand);
//		this.roomType = EnumRoomType.STAIRCASE_DIRECTED;
//		this.doorSide = doorSide;
//		this.numRotations = DungeonGenUtils.getCWRotationsBetween(Direction.SOUTH, this.doorSide);
//		this.defaultCeiling = false;
//
//		this.upperStairWidth = 0;
//
//		// Determine the width of the center stairs and the two upper side stairs. Find the largest possible
//		// side width such that the center width is still greater than or equal to the length of each side.
//		do {
//			this.upperStairWidth++;
//			this.centerStairWidth = (sideLength - 1) - this.upperStairWidth * 2;
//		} while ((this.centerStairWidth - 2) >= (this.upperStairWidth + 1));
//
//		// Each stair section should cover half the ascent
//		this.upperStairLength = height / 2;
//		this.centerStairLength = height + 1 - this.upperStairLength; // center section will either be same length or 1 more
//	}
//
//	@Override
//	public void generateRoom(BlockPos castleOrigin, BlockStateGenArray genArray, DungeonRandomizedCastle dungeon) {
//		// If stairs are facing to the east or west, need to flip the build lengths since we are essentially
//		// generating a room facing south and then rotating it
//		int lenX = this.doorSide.getAxis() == Direction.Axis.Z ? this.roomLengthX : this.roomLengthZ;
//		int lenZ = this.doorSide.getAxis() == Direction.Axis.Z ? this.roomLengthZ : this.roomLengthX;
//
//		for (int x = 0; x < lenX - 1; x++) {
//			for (int z = 0; z < lenZ - 1; z++) {
//				this.buildFloorBlock(x, z, genArray, dungeon);
//
//				if (z < 2) {
//					this.buildPlatform(x, z, genArray, dungeon);
//				} else if (((x < this.upperStairWidth) || (x >= this.centerStairWidth + this.upperStairWidth)) && z < this.upperStairLength + PLATFORM_LENGTH) {
//					this.buildUpperStair(x, z, genArray, dungeon);
//				} else if (((x >= this.upperStairWidth) || (x < this.centerStairWidth + this.upperStairWidth)) && z <= this.centerStairLength + PLATFORM_LENGTH) {
//					this.buildLowerStair(x, z, genArray, dungeon);
//				}
//			}
//		}
//	}
//
//	public void setDoorSide(Direction side) {
//		this.doorSide = side;
//	}
//
//	public int getUpperStairEndZ() {
//		return (this.upperStairLength);
//	}
//
//	public int getUpperStairWidth() {
//		return this.upperStairWidth;
//	}
//
//	public int getCenterStairWidth() {
//		return this.centerStairWidth;
//	}
//
//	public Direction getDoorSide() {
//		return this.doorSide;
//	}
//
//	private void buildFloorBlock(int x, int z, BlockStateGenArray genArray, DungeonRandomizedCastle dungeon) {
//		BlockState blockToBuild = dungeon.getFloorBlockState();
//		genArray.addBlockState(this.roomOrigin.add(x, 0, z), blockToBuild, BlockStateGenArray.GenerationPhase.MAIN, BlockStateGenArray.EnumPriority.MEDIUM);
//	}
//
//	private void buildUpperStair(int x, int z, BlockStateGenArray genArray, DungeonRandomizedCastle dungeon) {
//		int stairHeight = this.centerStairLength + (z - PLATFORM_LENGTH);
//		Direction stairFacing = DungeonGenUtils.rotateFacingNTimesAboutY(Direction.SOUTH, this.numRotations);
//		BlockState blockToBuild;
//		for (int y = 1; y < this.height; y++) {
//			if (y < stairHeight) {
//				blockToBuild = dungeon.getMainBlockState();
//			} else if (y == stairHeight) {
//				blockToBuild = dungeon.getStairBlockState().withProperty(StairsBlock.FACING, stairFacing);
//			} else {
//				blockToBuild = Blocks.AIR.getDefaultState();
//			}
//			genArray.addBlockState(this.getRotatedPlacement(x, y, z, this.doorSide), blockToBuild, BlockStateGenArray.GenerationPhase.MAIN, BlockStateGenArray.EnumPriority.MEDIUM);
//		}
//	}
//
//	private void buildLowerStair(int x, int z, BlockStateGenArray genArray, DungeonRandomizedCastle dungeon) {
//		int stairHeight = this.centerStairLength - (z - PLATFORM_LENGTH + 1);
//		Direction stairFacing = DungeonGenUtils.rotateFacingNTimesAboutY(Direction.NORTH, this.numRotations);
//		BlockState blockToBuild;
//		for (int y = 1; y < this.height; y++) {
//			if (y < stairHeight) {
//				blockToBuild = dungeon.getMainBlockState();
//			} else if (y == stairHeight) {
//				blockToBuild = dungeon.getStairBlockState().withProperty(StairsBlock.FACING, stairFacing);
//			} else {
//				blockToBuild = Blocks.AIR.getDefaultState();
//			}
//			genArray.addBlockState(this.getRotatedPlacement(x, y, z, this.doorSide), blockToBuild, BlockStateGenArray.GenerationPhase.MAIN, BlockStateGenArray.EnumPriority.MEDIUM);
//		}
//	}
//
//	private void buildPlatform(int x, int z, BlockStateGenArray genArray, DungeonRandomizedCastle dungeon) {
//		BlockState blockToBuild;
//		int platformHeight = this.centerStairLength; // the stair length is also the platform height
//
//		for (int y = 1; y < this.height; y++) {
//			if (y < platformHeight) {
//				blockToBuild = dungeon.getFloorBlockState();
//			} else {
//				blockToBuild = Blocks.AIR.getDefaultState();
//			}
//			genArray.addBlockState(this.getRotatedPlacement(x, y, z, this.doorSide), blockToBuild, BlockStateGenArray.GenerationPhase.MAIN, BlockStateGenArray.EnumPriority.MEDIUM);
//		}
//	}
//
//	@Override
//	public boolean canBuildDoorOnSide(Direction side) {
//		return (side == this.doorSide);
//	}
//
//	@Override
//	public boolean reachableFromSide(Direction side) {
//		return (side == this.doorSide);
//	}
//}
