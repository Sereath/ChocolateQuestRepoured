package team.cqr.cqrepoured.world.structure.generation.generators.stronghold.linear;

import java.io.File;
import java.util.Random;

import net.minecraft.util.Tuple;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import team.cqr.cqrepoured.util.ESkyDirection;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonStrongholdLinear;
import team.cqr.cqrepoured.world.structure.generation.generation.GeneratableDungeon;
import team.cqr.cqrepoured.world.structure.generation.generators.AbstractDungeonGenerationComponent;
import team.cqr.cqrepoured.world.structure.generation.generators.stronghold.EStrongholdRoomType;
import team.cqr.cqrepoured.world.structure.generation.generators.stronghold.GeneratorStronghold;
import team.cqr.cqrepoured.world.structure.generation.inhabitants.DungeonInhabitant;
import team.cqr.cqrepoured.world.structure.generation.structurefile.CQStructure;
import team.cqr.cqrepoured.world.structure.generation.structurefile.Offset;

public class StrongholdFloor extends AbstractDungeonGenerationComponent<DungeonStrongholdLinear, GeneratorStronghold> {

	private final Random random;
	private int sideLength;
	private EStrongholdRoomType[][] roomPattern;
	// Where do we face currently? its the direction we face after exiting the last part we were in
	private ESkyDirection currentDirection;
	private boolean lastFloor;
	private int lastX, lastZ;

	public StrongholdFloor(int size, GeneratorStronghold generator, boolean isLastFloor, Random rand) {
		super(generator);
		this.sideLength = size;
		this.lastFloor = isLastFloor;
		this.roomPattern = new EStrongholdRoomType[size][size];
		this.random = rand;
	}

	public void generateRoomPattern(int gridPosX, int gridPosZ, ESkyDirection prevFloorExitDir) {
		this.setRoomType(gridPosX, gridPosZ, EStrongholdRoomType.NONE);
		boolean curve = gridPosX == 0 && gridPosZ == 0;
		Tuple<Integer, Integer> roomCoord = new Tuple<>(gridPosX, gridPosZ);
		// System.out.println("X: " + gridPosX + " Z: " + gridPosZ + " Room: 0");
		// System.out.println("X: " + roomCoord.getFirst() + " Z: " + roomCoord.getSecond() + " Room: 1");

		boolean reversed = !curve;
		int sideLengthTemp = reversed ? this.sideLength : 1;
		int slCounter = reversed ? (sideLengthTemp * 4) - 4 - 1 : 1;
		if (reversed && sideLengthTemp <= 3) {
			slCounter--;
		}

		this.currentDirection = prevFloorExitDir;

		int roomCount = this.sideLength * this.sideLength;
		// System.out.println("Room Count: " + (roomCount));
		roomCount--;

		// System.out.println("Beginning gen...");
		while (roomCount > 0) {
			roomCoord = this.getNextRoomCoordinates(roomCoord.getA(), roomCoord.getB(), this.currentDirection);
			// System.out.println("X: " + roomCoord.getFirst() + " Z: " + roomCoord.getSecond() + " Room: " + ((this.sideLength *
			// this.sideLength) - roomCount));
			roomCount--;
			slCounter--;
			// System.out.println("sl: " + slCounter);

			if (roomCount == 0) {
				// DONE: Handle stair or boss room
				if (this.lastFloor) {
					this.setRoomType(roomCoord.getA(), roomCoord.getB(), EStrongholdRoomType.BOSS);
				} else {
					// Handle stair
					this.setRoomType(roomCoord.getA(), roomCoord.getB(), this.getStair(this.currentDirection));
					this.currentDirection = this.getRoomExitDirection(this.getStair(this.currentDirection));
				}
				break;
			}
			if (slCounter <= 0 || (!reversed && slCounter > 1 && this.isCurveRoom(roomCoord.getA(), roomCoord.getB())) || (reversed && slCounter > 1 && slCounter < ((sideLengthTemp * 4) - 4 - 2) && this.isCurveRoom(roomCoord.getA(), roomCoord.getB()))) {
				if (slCounter <= 0) {
					sideLengthTemp += reversed ? -2 : 2;
					slCounter = (sideLengthTemp * 4) - 4;
					// System.out.println("sidelength: " + sideLengthTemp);
					if (reversed) {
						slCounter--;
					}
				}

				this.setRoomType(roomCoord.getA(), roomCoord.getB(), this.getCurve(this.currentDirection, reversed));
				this.currentDirection = this.getRoomExitDirection(this.getCurve(this.currentDirection, reversed));
			} else {
				this.setRoomType(roomCoord.getA(), roomCoord.getB(), this.getHallway(this.currentDirection));
			}

		}
	}

	private EStrongholdRoomType getHallway(ESkyDirection dir) {
		switch (dir) {
		case EAST:
			return EStrongholdRoomType.HALLWAY_WE;
		case NORTH:
			return EStrongholdRoomType.HALLWAY_SN;
		case SOUTH:
			return EStrongholdRoomType.HALLWAY_NS;
		case WEST:
			return EStrongholdRoomType.HALLWAY_EW;
		default:
			return null;
		}
	}

	private EStrongholdRoomType getStair(ESkyDirection dir) {
		switch (dir) {
		case EAST:
			return EStrongholdRoomType.STAIR_WW;
		case NORTH:
			return EStrongholdRoomType.STAIR_SS;
		case SOUTH:
			return EStrongholdRoomType.STAIR_NN;
		case WEST:
			return EStrongholdRoomType.STAIR_EE;
		default:
			return null;
		}
	}

	private EStrongholdRoomType getCurve(ESkyDirection dir, boolean reversed) {
		// When you enter the curve: you face the OPPOSITE of FIRST_LETTER, when you leave you face SECOND_LETTER
		switch (dir) {
		case EAST:
			return reversed ? EStrongholdRoomType.CURVE_WS : EStrongholdRoomType.CURVE_WN;
		case NORTH:
			return reversed ? EStrongholdRoomType.CURVE_SE : EStrongholdRoomType.CURVE_SW;
		case SOUTH:
			return reversed ? EStrongholdRoomType.CURVE_NW : EStrongholdRoomType.CURVE_NE;
		case WEST:
			return reversed ? EStrongholdRoomType.CURVE_EN : EStrongholdRoomType.CURVE_ES;
		default:
			return null;
		}
	}

	public void generateRooms(int centerX, int centerZ, int y, PlacementSettings settings, GeneratableDungeon.Builder dungeonBuilder, Level world, DungeonInhabitant mobType) {
		for (int iX = 0; iX < this.sideLength; iX++) {
			for (int iZ = 0; iZ < this.sideLength; iZ++) {
				EStrongholdRoomType room = this.roomPattern[iX][iZ];
				if (room != null && room != EStrongholdRoomType.NONE) {
					Tuple<Integer, Integer> gridPos = this.arrayIndiciesToGridPos(new Tuple<>(iX, iZ));
					int x = centerX + (gridPos.getA() * this.generator.getDungeon().getRoomSizeX());
					int z = centerZ + (gridPos.getB() * this.generator.getDungeon().getRoomSizeZ());
					int y1 = y;
					if (room.toString().startsWith("STAIR_")) {
						y1 -= this.generator.getDungeon().getRoomSizeY();
					}
					BlockPos pos = new BlockPos(x, y1, z);
					File struct = this.generator.getDungeon().getRoom(room, this.random);
					if (struct != null) {
						CQStructure structure = CQStructure.createFromFile(struct);
						structure.addAll(dungeonBuilder, pos, Offset.CENTER);
					}
				}
			}
		}
	}

	private Tuple<Integer, Integer> getNextRoomCoordinates(int oldX, int oldZ, ESkyDirection direction) {
		switch (direction) {
		case EAST:
			return new Tuple<>(oldX + 1, oldZ);
		case NORTH:
			return new Tuple<>(oldX, oldZ - 1);
		case SOUTH:
			return new Tuple<>(oldX, oldZ + 1);
		case WEST:
			return new Tuple<>(oldX - 1, oldZ);
		}
		return new Tuple<>(oldX, oldZ);
	}

	public Tuple<Integer, Integer> getLastRoomGridPos() {
		return new Tuple<>(this.lastX, this.lastZ);
	}

	private Tuple<Integer, Integer> gridPosToArrayIndices(Tuple<Integer, Integer> gridPosIn) {
		int x = (int) Math.floor(this.sideLength / 2D);
		return new Tuple<>(gridPosIn.getA() + x, gridPosIn.getB() + x);
	}

	private Tuple<Integer, Integer> arrayIndiciesToGridPos(Tuple<Integer, Integer> arrayIndiciesIn) {
		int x = (int) Math.floor(this.sideLength / 2D);
		return new Tuple<>(arrayIndiciesIn.getA() - x, arrayIndiciesIn.getB() - x);
	}

	public ESkyDirection getExitDirection() {
		// When you enter the stair, you face the opposite direction
		return this.currentDirection;
	}

	private boolean isCurveRoom(int gpX, int gpZ) {
		return Math.abs(gpX) == Math.abs(gpZ);
	}

	private void setRoomType(int gpX, int gpZ, EStrongholdRoomType type) {
		Tuple<Integer, Integer> coords = this.gridPosToArrayIndices(new Tuple<>(gpX, gpZ));
		this.lastX = gpX;
		this.lastZ = gpZ;
		// System.out.println("X: " + gpX + " Z: " + gpZ + " Room: " + type.toString());
		this.roomPattern[coords.getA()][coords.getB()] = type;
	}

	private ESkyDirection getRoomExitDirection(EStrongholdRoomType room) {
		switch (room) {
		case CURVE_EN:
		case CURVE_WN:
		case HALLWAY_SN:
		case STAIR_NN:
			return ESkyDirection.NORTH;
		case CURVE_ES:
		case CURVE_WS:
		case HALLWAY_NS:
		case STAIR_SS:
			return ESkyDirection.SOUTH;
		case CURVE_NE:
		case CURVE_SE:
		case HALLWAY_WE:
		case STAIR_EE:
			return ESkyDirection.EAST;
		case CURVE_NW:
		case CURVE_SW:
		case HALLWAY_EW:
		case STAIR_WW:
			return ESkyDirection.WEST;
		default:
			return null;
		}
	}

	@Override
	public void preProcess(Level world, GeneratableDungeon.Builder dungeonBuilder, DungeonInhabitant mobType) {

	}

	@Override
	public void generate(Level world, GeneratableDungeon.Builder dungeonBuilder, DungeonInhabitant mobType) {

	}

	@Override
	public void generatePost(Level world, GeneratableDungeon.Builder dungeonBuilder, DungeonInhabitant mobType) {

	}

}
