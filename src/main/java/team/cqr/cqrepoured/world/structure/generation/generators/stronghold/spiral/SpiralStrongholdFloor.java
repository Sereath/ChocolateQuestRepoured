package team.cqr.cqrepoured.world.structure.generation.generators.stronghold.spiral;

import java.io.File;
import java.util.Random;

import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonVolcano;
import team.cqr.cqrepoured.world.structure.generation.generation.GeneratableDungeon;
import team.cqr.cqrepoured.world.structure.generation.generators.AbstractDungeonGenerator;
import team.cqr.cqrepoured.world.structure.generation.generators.stronghold.EStrongholdRoomType;
import team.cqr.cqrepoured.world.structure.generation.structurefile.CQStructure;
import team.cqr.cqrepoured.world.structure.generation.structurefile.Offset;

public class SpiralStrongholdFloor {

	private final Random random;
	private AbstractDungeonGenerator<DungeonVolcano> generator;
	private GeneratableDungeon.Builder dungeonBuilder;
	private Tuple<Integer, Integer> entranceCoordinates;
	private Tuple<Integer, Integer> entranceIndex;
	private Tuple<Integer, Integer> exitCoordinates;
	private Tuple<Integer, Integer> exitIndex;
	private boolean isLastFloor = false;
	private int sideLength;
	private int roomCount;
	private EStrongholdRoomType[][] roomGrid;
	private Tuple[][] previousCoords;
	private BlockPos[][] coordinateGrid;
	private boolean isReversed;

	public SpiralStrongholdFloor(AbstractDungeonGenerator<DungeonVolcano> generator, GeneratableDungeon.Builder dungeonBuilder, Tuple<Integer, Integer> entrancePos, int entranceX, int entranceZ, boolean isLastFloor, int sideLength, int roomCount, Random rand, boolean isReversed) {
		this.generator = generator;
		this.dungeonBuilder = dungeonBuilder;
		this.entranceCoordinates = entrancePos;
		this.entranceIndex = new Tuple<>(entranceX, entranceZ);
		this.isLastFloor = isLastFloor;
		this.sideLength = sideLength;
		this.roomCount = roomCount;
		this.roomGrid = new EStrongholdRoomType[sideLength][sideLength];
		this.coordinateGrid = new BlockPos[sideLength][sideLength];
		this.previousCoords = new Tuple[sideLength][sideLength];
		this.random = rand;
		this.isReversed = isReversed;
	}

	public void calculateRoomGrid(EStrongholdRoomType entranceRoomType) {
		int x = this.entranceIndex.getFirst();
		int z = this.entranceIndex.getSecond();
		boolean isFirst = true;
		int lastX = x;
		int lastZ = z;
		while (this.roomCount > 0) {
			if (isFirst) {
				isFirst = false;
			} else {
				this.previousCoords[x][z] = new Tuple<>(lastX, lastZ);
			}
			lastX = x;
			lastZ = z;
			this.roomCount--;
			if (this.roomCount == 0) {
				this.exitIndex = new Tuple<>(x, z);
				if (this.isLastFloor) {
					this.roomGrid[x][z] = EStrongholdRoomType.BOSS;
				} else {
					this.roomGrid[x][z] = this.getExitRoomType(x, z, this.isReversed);
				}
				break;
			}
			if (x == 0 && z == 0) {
				if (this.isReversed) {
					this.roomGrid[x][z] = EStrongholdRoomType.CURVE_SE;
					x += 1;
				} else {
					this.roomGrid[x][z] = EStrongholdRoomType.CURVE_ES;
					z += 1;
				}
				continue;
			}
			if (x == (this.sideLength - 1) && z == (this.sideLength - 1)) {
				if (this.isReversed) {
					this.roomGrid[x][z] = EStrongholdRoomType.CURVE_NW;
					x -= 1;
				} else {
					this.roomGrid[x][z] = EStrongholdRoomType.CURVE_WN;
					z -= 1;
				}
				continue;
			}
			if (x == 0 && z == (this.sideLength - 1)) {
				if (this.isReversed) {
					this.roomGrid[x][z] = EStrongholdRoomType.CURVE_EN;
					z -= 1;
				} else {
					this.roomGrid[x][z] = EStrongholdRoomType.CURVE_NE;
					x += 1;
				}
				continue;
			}
			if (x == (this.sideLength - 1) && z == 0) {
				if (this.isReversed) {
					this.roomGrid[x][z] = EStrongholdRoomType.CURVE_WS;
					z += 1;
				} else {
					this.roomGrid[x][z] = EStrongholdRoomType.CURVE_SW;
					x -= 1;
				}
				continue;
			}
			if (x == 0) {
				// Left side
				if (!this.isReversed) {
					this.roomGrid[x][z] = EStrongholdRoomType.HALLWAY_NS;
					z += 1;
				} else {
					this.roomGrid[x][z] = EStrongholdRoomType.HALLWAY_SN;
					z -= 1;
				}
				continue;
			}
			if (x == (this.sideLength - 1)) {
				// Right side
				if (this.isReversed) {
					this.roomGrid[x][z] = EStrongholdRoomType.HALLWAY_NS;
					z += 1;
				} else {
					this.roomGrid[x][z] = EStrongholdRoomType.HALLWAY_SN;
					z -= 1;
				}
				continue;
			}
			if (z == 0) {
				// Bottom side
				if (this.isReversed) {
					this.roomGrid[x][z] = EStrongholdRoomType.HALLWAY_WE;
					x += 1;
				} else {
					this.roomGrid[x][z] = EStrongholdRoomType.HALLWAY_EW;
					x -= 1;
				}
				continue;
			}
			if (z == (this.sideLength - 1)) {
				// Top side
				if (!this.isReversed) {
					this.roomGrid[x][z] = EStrongholdRoomType.HALLWAY_WE;
					x += 1;
				} else {
					this.roomGrid[x][z] = EStrongholdRoomType.HALLWAY_EW;
					x -= 1;
				}
				continue;
			}
		}
		this.roomGrid[this.entranceIndex.getFirst()][this.entranceIndex.getSecond()] = entranceRoomType;
		// System.out.println("Done");
	}

	private EStrongholdRoomType getExitRoomType(int iX, int iZ, boolean rev) {
		if (iX == 0 && iZ == 0) {
			return rev ? EStrongholdRoomType.STAIR_SS : EStrongholdRoomType.STAIR_EE;
		}
		if (iX == 0 && iZ == (this.sideLength - 1)) {
			return rev ? EStrongholdRoomType.STAIR_EE : EStrongholdRoomType.STAIR_NN;
		}
		if (iX == (this.sideLength - 1) && iZ == 0) {
			return rev ? EStrongholdRoomType.STAIR_WW : EStrongholdRoomType.STAIR_SS;
		}
		if (iX == (this.sideLength - 1) && iZ == (this.sideLength - 1)) {
			return rev ? EStrongholdRoomType.STAIR_NN : EStrongholdRoomType.STAIR_WW;
		}

		// Stairs not in corners
		if (iZ == 0) {
			return rev ? EStrongholdRoomType.STAIR_WW : EStrongholdRoomType.STAIR_EE;
		}
		if (iZ == (this.sideLength - 1)) {
			return rev ? EStrongholdRoomType.STAIR_EE : EStrongholdRoomType.STAIR_WW;
		}
		if (iX == 0) {
			return rev ? EStrongholdRoomType.STAIR_SS : EStrongholdRoomType.STAIR_NN;
		}
		if (iX == (this.sideLength - 1)) {
			return rev ? EStrongholdRoomType.STAIR_NN : EStrongholdRoomType.STAIR_SS;
		}
		return EStrongholdRoomType.NONE;
	}

	public void calculateCoordinates(int y, int roomSizeX, int roomSizeZ) {
		BlockPos entrancePos = new BlockPos(this.entranceCoordinates.getFirst(), y, this.entranceCoordinates.getSecond());
		this.coordinateGrid[this.entranceIndex.getFirst()][this.entranceIndex.getSecond()] = entrancePos;
		for (int iX = 0; iX < this.sideLength; iX++) {
			for (int iZ = 0; iZ < this.sideLength; iZ++) {
				if ((iX == 0 || iX == (this.sideLength - 1)) || (iZ == 0 || iZ == (this.sideLength - 1))) {
					EStrongholdRoomType room = this.roomGrid[iX][iZ];
					if (room != null && room != EStrongholdRoomType.NONE) {
						int x = (iX - this.entranceIndex.getFirst()) * roomSizeX;
						x += entrancePos.getX();
						int z = (iZ - this.entranceIndex.getSecond()) * roomSizeZ;
						z += entrancePos.getZ();
						this.coordinateGrid[iX][iZ] = new BlockPos(x, y, z);
					}
				}
			}
		}
		this.coordinateGrid[this.entranceIndex.getFirst()][this.entranceIndex.getSecond()] = entrancePos;
		if (!this.isLastFloor) {
			int x = (this.exitIndex.getFirst() - this.entranceIndex.getFirst()) * roomSizeX;
			x += entrancePos.getX();
			int z = (this.exitIndex.getSecond() - this.entranceIndex.getSecond()) * roomSizeZ;
			z += entrancePos.getZ();
			this.coordinateGrid[this.exitIndex.getFirst()][this.exitIndex.getSecond()] = new BlockPos(x, y, z);
			this.exitCoordinates = new Tuple<>(x, z);
		}
	}

	public Tuple<Integer, Integer> getExitCoordinates() {
		return this.exitCoordinates;
	}

	public Tuple<Integer, Integer> getExitIndex() {
		return this.exitIndex;
	}

	public void overrideFirstRoomType(EStrongholdRoomType type) {
		this.roomGrid[this.entranceIndex.getFirst()][this.entranceIndex.getSecond()] = type;
	}

	public void overrideLastRoomType(EStrongholdRoomType type) {
		if (!this.isLastFloor) {
			this.roomGrid[this.exitIndex.getFirst()][this.exitIndex.getSecond()] = type;
		}
	}

	public EStrongholdRoomType[][] getRoomGrid() {
		return this.roomGrid;
	}

	public void buildRooms(DungeonVolcano dungeon, World world) {
		File[][] fileMap = new File[this.sideLength][this.sideLength];
		for (int iX = 0; iX < this.sideLength; iX++) {
			for (int iZ = 0; iZ < this.sideLength; iZ++) {
				if ((iX == 0 || iX == (this.sideLength - 1)) || (iZ == 0 || iZ == (this.sideLength - 1))) {
					EStrongholdRoomType type = this.roomGrid[iX][iZ];
					if (type != null && type != EStrongholdRoomType.NONE) {
						if (dungeon != null && world != null) {
							Tuple<Integer, Integer> prevCoords = this.previousCoords[iX][iZ];
							File previous = null;
							EStrongholdRoomType prevType = EStrongholdRoomType.NONE;
							if (prevCoords != null) {
								previous = fileMap[prevCoords.getFirst()][prevCoords.getSecond()];
								prevType = this.roomGrid[prevCoords.getFirst()][prevCoords.getSecond()];
							}
							File file = dungeon.getRoomNBTFileForType(type, this.random);
							final int roomCount = dungeon.getRoomNBTCountForType(prevType);
							if (file == previous && !prevType.equals(EStrongholdRoomType.NONE) && roomCount > 1 && type.equals(prevType)) {
								int counter = 0; 
								while (file.getPath().equalsIgnoreCase(previous.getPath()) && counter < roomCount) {
									counter++;
									file = dungeon.getRoomNBTFileForType(type, this.random);
								}
							}
							fileMap[iX][iZ] = file;
							if (file != null) {
								CQStructure room = this.generator.loadStructureFromFile(file);
								room.addAll(this.dungeonBuilder, this.coordinateGrid[iX][iZ], Offset.CENTER);
							}
						}
					}
				}
			}
		}
	}

	public EStrongholdRoomType getExitRoomType() {
		return this.roomGrid[this.exitIndex.getFirst()][this.exitIndex.getSecond()];
	}

}
