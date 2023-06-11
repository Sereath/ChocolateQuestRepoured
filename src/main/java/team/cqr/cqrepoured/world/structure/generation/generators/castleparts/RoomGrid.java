//package team.cqr.cqrepoured.world.structure.generation.generators.castleparts;
//
//import net.minecraft.util.Direction;
//import net.minecraft.core.BlockPos;
//import team.cqr.cqrepoured.CQRMain;
//import team.cqr.cqrepoured.util.DungeonGenUtils;
//import team.cqr.cqrepoured.world.structure.generation.generators.castleparts.rooms.CastleRoomReplacedRoof;
//import team.cqr.cqrepoured.world.structure.generation.generators.castleparts.rooms.CastleRoomWalkableRoof;
//import team.cqr.cqrepoured.world.structure.generation.generators.castleparts.rooms.EnumRoomType;
//import team.cqr.cqrepoured.world.structure.generation.generators.castleparts.rooms.segments.CastleMainStructWall;
//
//import javax.annotation.Nullable;
//import java.util.*;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//public class RoomGrid {
//	public static class Area2D {
//		public RoomGridPosition start;
//		public int sizeX;
//		public int sizeZ;
//
//		public Area2D(RoomGridPosition start, int sizeX, int sizeZ) {
//			this.start = start;
//			this.sizeX = sizeX;
//			this.sizeZ = sizeZ;
//		}
//
//		public Area2D(Area2D area) {
//			this.start = area.start;
//			this.sizeX = area.sizeX;
//			this.sizeZ = area.sizeZ;
//		}
//
//		public Area2D addFloors(int numFloors) {
//			return new Area2D(this.start.move(Direction.UP, numFloors), this.sizeX, this.sizeZ);
//		}
//
//		public int getStartX() {
//			return this.start.getX();
//		}
//
//		public int getStartZ() {
//			return this.start.getZ();
//		}
//
//		public int getEndX() {
//			return this.start.getX() + this.sizeX - 1;
//		}
//
//		public int getEndZ() {
//			return this.start.getZ() + this.sizeZ - 1;
//		}
//
//		public RoomGridPosition getTopRight() {
//			return this.start.move(Direction.EAST, this.sizeX - 1);
//		}
//
//		public RoomGridPosition getBottomLeft() {
//			return this.start.move(Direction.SOUTH, this.sizeZ - 1);
//		}
//
//		public boolean dimensionsAre(int dim1, int dim2) {
//			return (this.sizeX == dim1 && this.sizeZ == dim2) || (this.sizeX == dim2 && this.sizeZ == 1);
//		}
//
//		public boolean dimensionsAreAtLeast(int dim1, int dim2) {
//			int larger = Math.max(dim1, dim2);
//			int smaller = Math.min(dim1, dim2);
//			return (Math.min(this.sizeX, this.sizeZ) >= smaller) && (Math.max(this.sizeX, this.sizeZ) >= larger);
//		}
//
//		public int countXCellsToPosition(RoomGridPosition gridPos) {
//			return gridPos.getX() - this.start.getX();
//		}
//
//		public int countZCellsToPosition(RoomGridPosition gridPos) {
//			return gridPos.getZ() - this.start.getZ();
//		}
//
//		public List<RoomGridPosition> getPositionList() {
//			List<RoomGridPosition> positions = new ArrayList<>();
//
//			for (int x = 0; x < this.sizeX; x++) {
//				for (int z = 0; z < this.sizeZ; z++) {
//					positions.add(this.start.move(Direction.EAST, x).move(Direction.SOUTH, z));
//				}
//			}
//
//			return positions;
//		}
//
//		public void removeFromList(List<RoomGridPosition> positions) {
//			List<RoomGridPosition> myPositions = this.getPositionList();
//			positions.removeAll(myPositions);
//		}
//
//		public Area2D getRandomSubArea(Random random, int minDim1, int minDim2, boolean mustBeSmaller) {
//			// Make sure this area has the space to fit the sub dimensions
//			if (this.dimensionsAreAtLeast(minDim1, minDim2)) {
//				int resultX;
//				int resultZ;
//				int shrink = mustBeSmaller ? 1 : 0;
//
//				// Figure out which dimension is larger (so parameter order doesn't matter)
//				int larger = Math.max(minDim1, minDim2);
//				int smaller = Math.min(minDim1, minDim2);
//
//				// Determine which directions (X and Z) have the room to fit the longer of the two dimensions
//				boolean fitsX = this.sizeX >= larger;
//				boolean fitsZ = this.sizeZ >= larger;
//
//				// If either dimension could be the long side, then pick at random
//				if (fitsX && fitsZ) {
//					if (random.nextBoolean()) {
//						resultX = DungeonGenUtils.randomBetweenGaussian(larger, this.sizeX - shrink, random);
//						resultZ = DungeonGenUtils.randomBetweenGaussian(smaller, this.sizeZ - shrink, random);
//					} else {
//						resultZ = DungeonGenUtils.randomBetweenGaussian(larger, this.sizeZ - shrink, random);
//						resultX = DungeonGenUtils.randomBetweenGaussian(smaller, this.sizeX - shrink, random);
//					}
//				}
//				// Otherwise use the side with more room as the long side
//				else if (fitsX) {
//					resultX = DungeonGenUtils.randomBetweenGaussian(larger, this.sizeX - shrink, random);
//					resultZ = DungeonGenUtils.randomBetweenGaussian(smaller, this.sizeZ - shrink, random);
//				} else {
//					resultZ = DungeonGenUtils.randomBetweenGaussian(larger, this.sizeZ - shrink, random);
//					resultX = DungeonGenUtils.randomBetweenGaussian(smaller, this.sizeX - shrink, random);
//				}
//
//				return this.randomlyPositionSubArea(random, resultX, resultZ);
//			} else {
//				// Impossible to meet dimension constraints so just stay the same
//				return this;
//			}
//		}
//
//		public Area2D getExactSubArea(Random random, int minDim1, int minDim2) {
//			// Make sure this area has the space to fit the sub dimensions
//			if (this.dimensionsAreAtLeast(minDim1, minDim2)) {
//				int resultX;
//				int resultZ;
//				boolean xIsLonger;
//
//				// Figure out which dimension is larger (so parameter order doesn't matter)
//				int larger = Math.max(minDim1, minDim2);
//				int smaller = Math.min(minDim1, minDim2);
//
//				// Determine which directions (X and Z) have the room to fit the longer of the two dimensions
//				boolean fitsX = this.sizeX >= larger;
//				boolean fitsZ = this.sizeZ >= larger;
//
//				// If either dimension could be the long side, then pick at random, otherwise force long side
//				xIsLonger = (fitsX && fitsZ) ? (random.nextBoolean()) : fitsX;
//
//				resultX = xIsLonger ? larger : smaller;
//				resultZ = xIsLonger ? smaller : larger;
//
//				return this.randomlyPositionSubArea(random, resultX, resultZ);
//			} else {
//				// Impossible to meet dimension constraints so just stay the same
//				return this;
//			}
//		}
//
//		private Area2D randomlyPositionSubArea(Random random, int resultX, int resultZ) {
//			RoomGridPosition subStart = this.start;
//			int maxMoveX = this.sizeX - resultX;
//			int maxMoveZ = this.sizeZ - resultZ;
//			if (maxMoveX > 0) {
//				subStart = subStart.move(Direction.EAST, random.nextInt(this.sizeX - resultX));
//			}
//			if (maxMoveZ > 0) {
//				subStart = subStart.move(Direction.SOUTH, random.nextInt(this.sizeZ - resultZ));
//			}
//
//			return new Area2D(subStart, resultX, resultZ);
//		}
//
//		@Nullable
//		public Area2D sliceToSideOfArea(Area2D mask, Direction side) {
//			if (mask != null) {
//				RoomGridPosition resultStart;
//				int resultSizeX;
//				int resultSizeZ;
//
//				if (side == Direction.NORTH) {
//					resultStart = this.start;
//					resultSizeX = this.sizeX;
//					resultSizeZ = mask.getStartZ() - this.getStartZ();
//				} else if (side == Direction.SOUTH) {
//					resultStart = new RoomGridPosition(this.start.getFloor(), this.start.getX(), mask.getEndZ() + 1);
//					resultSizeX = this.sizeX;
//					resultSizeZ = this.getEndZ() - mask.getEndZ();
//				} else if (side == Direction.WEST) {
//					resultStart = this.start;
//					resultSizeX = mask.getStartX() - this.getStartX();
//					resultSizeZ = this.sizeZ;
//				} else // East
//				{
//					resultStart = new RoomGridPosition(this.start.getFloor(), mask.getEndX() + 1, this.start.getZ());
//					resultSizeX = this.getEndX() - mask.getEndX();
//					resultSizeZ = this.sizeZ;
//				}
//
//				List<RoomGridPosition> boundary = this.getPositionList();
//				if (boundary.contains(resultStart) && resultSizeX > 0 && resultSizeZ > 0) {
//					return new Area2D(resultStart, resultSizeX, resultSizeZ);
//				} else {
//					return null;
//				}
//			}
//
//			return null;
//		}
//
//		public void alignToSide(Random random, Area2D targetArea, Direction side, Area2D boundary) {
//			int distance; // distance to move toward target area so we are adjacent
//
//			// slide position is position perpendicular to the side to are least 1 square is touching
//			int minSlide;
//			int maxSlide;
//			int slideDest;
//
//			if (side == Direction.NORTH) {
//				this.start.setZ(targetArea.getStartZ() - this.sizeZ);
//
//				minSlide = Math.max((targetArea.getStartX() - (this.sizeX - 1)), boundary.start.getX());
//				maxSlide = Math.min((targetArea.getEndX()), boundary.getEndX() - (this.sizeX - 1));
//				slideDest = DungeonGenUtils.randomBetweenGaussian(minSlide, maxSlide, random);
//
//				this.start.setX(slideDest);
//			} else if (side == Direction.SOUTH) {
//				this.start.setZ(targetArea.getEndZ() + 1);
//
//				minSlide = Math.max((targetArea.getStartX() - (this.sizeX - 1)), boundary.start.getX());
//				maxSlide = Math.min((targetArea.getEndX()), boundary.getEndX() - (this.sizeX - 1));
//				slideDest = DungeonGenUtils.randomBetweenGaussian(minSlide, maxSlide, random);
//
//				this.start.setX(slideDest);
//			} else if (side == Direction.WEST) {
//				this.start.setX(targetArea.getStartX() - this.sizeX);
//
//				minSlide = Math.max((targetArea.getStartZ() - (this.sizeZ - 1)), boundary.start.getZ());
//				maxSlide = Math.min((targetArea.getEndZ()), boundary.getEndZ() - (this.sizeZ - 1));
//				slideDest = DungeonGenUtils.randomBetweenGaussian(minSlide, maxSlide, random);
//
//				this.start.setZ(slideDest);
//			} else // East
//			{
//				this.start.setX(targetArea.getEndX() + 1);
//
//				minSlide = Math.max((targetArea.getStartZ() - (this.sizeZ - 1)), boundary.start.getZ());
//				maxSlide = Math.min((targetArea.getEndZ()), boundary.getEndZ() - (this.sizeZ - 1));
//				slideDest = DungeonGenUtils.randomBetweenGaussian(minSlide, maxSlide, random);
//
//				this.start.setZ(slideDest);
//			}
//		}
//
//		@Override
//		public String toString() {
//			return String.format("RoomGrid.Area2D{start=%s, sizeX=%d, sizeZ=%d}", this.start, this.sizeX, this.sizeZ);
//		}
//	}
//
//	private int floors;
//	private int roomsX;
//	private int roomsZ;
//	private RoomGridCell[][][] cellArray;
//	private List<RoomGridCell> cellList;
//	private List<CastleMainStructWall> wallList;
//	private Area2D bossArea = null;
//
//	public RoomGrid(int floors, int roomsX, int roomsZ, int roomWidth, int floorHeight, Random random) {
//		this.floors = floors;
//		this.roomsX = roomsX;
//		this.roomsZ = roomsZ;
//		this.cellArray = new RoomGridCell[floors][roomsX][roomsZ];
//		this.cellList = new ArrayList<>();
//		this.wallList = new ArrayList<>();
//
//		// initialize the room grid
//		for (int floor = 0; floor < floors; floor++) {
//			for (int x = 0; x < roomsX; x++) {
//				for (int z = 0; z < roomsZ; z++) {
//					RoomGridCell cell = new RoomGridCell(floor, x, z, roomWidth, floorHeight);
//					this.cellArray[floor][x][z] = cell;
//					this.cellList.add(cell);
//				}
//			}
//		}
//
//		this.initializeCellLinks();
//		this.initializeWalls(roomWidth, floorHeight);
//	}
//
//	private void initializeCellLinks() {
//		for (int floor = 0; floor < this.floors; floor++) {
//			for (int x = 0; x < this.roomsX; x++) {
//				for (int z = 0; z < this.roomsZ; z++) {
//					RoomGridCell cell = this.getCellAt(floor, x, z);
//
//					for (Direction direction : Direction.VALUES) {
//						RoomGridCell adjacent = this.getAdjacentCell(cell, direction);
//						if (adjacent != null) {
//							cell.registerAdjacentCell(adjacent, direction);
//							adjacent.registerAdjacentCell(cell, direction.getOpposite());
//						}
//					}
//				}
//			}
//		}
//	}
//
//	private void initializeWalls(int roomWidth, int floorHeight) {
//		final int wallWidth = roomWidth + 2; // extends 1 block past each edge of the room
//
//		// vertical walls
//		for (int floor = 0; floor < this.floors; floor++) {
//			for (int x = 0; x < this.roomsX + 1; x++) {
//				for (int z = 0; z < this.roomsZ; z++) {
//					int xOffset = x * (roomWidth + 1);
//					int yOffset = floor * floorHeight;
//					int zOffset = z * (roomWidth + 1);
//					BlockPos wallOrigin = new BlockPos(xOffset, yOffset, zOffset);
//					CastleMainStructWall wall = new CastleMainStructWall(wallOrigin, CastleMainStructWall.WallOrientation.VERTICAL, wallWidth, floorHeight);
//					this.wallList.add(wall);
//
//					RoomGridCell westCell = this.getCellAt(floor, x - 1, z);
//					if (westCell != null) {
//						wall.registerAdjacentCell(westCell, Direction.WEST);
//						westCell.registerAdjacentWall(wall, Direction.EAST);
//					}
//
//					RoomGridCell eastCell = this.getCellAt(floor, x, z);
//					if (eastCell != null) {
//						wall.registerAdjacentCell(eastCell, Direction.EAST);
//						eastCell.registerAdjacentWall(wall, Direction.WEST);
//					}
//				}
//			}
//		}
//
//		// horizontal walls
//		for (int floor = 0; floor < this.floors; floor++) {
//			for (int x = 0; x < this.roomsX; x++) {
//				for (int z = 0; z < this.roomsZ + 1; z++) {
//					int xOffset = x * (roomWidth + 1);
//					int yOffset = floor * floorHeight;
//					int zOffset = z * (roomWidth + 1);
//					BlockPos wallOrigin = new BlockPos(xOffset, yOffset, zOffset);
//					CastleMainStructWall wall = new CastleMainStructWall(wallOrigin, CastleMainStructWall.WallOrientation.HORIZONTAL, wallWidth, floorHeight);
//					this.wallList.add(wall);
//
//					RoomGridCell northCell = this.getCellAt(floor, x, z - 1);
//					if (northCell != null) {
//						wall.registerAdjacentCell(northCell, Direction.NORTH);
//						northCell.registerAdjacentWall(wall, Direction.SOUTH);
//					}
//
//					RoomGridCell southCell = this.getCellAt(floor, x, z);
//					if (southCell != null) {
//						wall.registerAdjacentCell(southCell, Direction.SOUTH);
//						southCell.registerAdjacentWall(wall, Direction.NORTH);
//					}
//				}
//			}
//		}
//	}
//
//	public void setRoomReachable(int floor, int x, int z) {
//		this.cellArray[floor][x][z].setReachable();
//	}
//
//	public List<RoomGridCell> getCellListCopy() {
//		return new ArrayList<>(this.cellList);
//	}
//
//	public List<RoomGridCell> getAllCellsWhere(Predicate<RoomGridCell> p) {
//		return this.getCellListCopy().stream().filter(p).collect(Collectors.toList());
//	}
//
//	public List<RoomGridCell> getSelectedCellsInColumn(int floor, int columnIndex) {
//		List<RoomGridCell> result = this.getCellListCopy();
//		result.removeIf(r -> r.getFloor() != floor);
//		result.removeIf(r -> r.getGridX() != columnIndex);
//		result.removeIf(r -> !r.isSelectedForBuilding());
//		result.removeIf(r -> !r.isPopulated());
//		return result;
//	}
//
//	public List<RoomGridCell> getSelectedCellsInRow(int floor, int rowIndex) {
//		List<RoomGridCell> result = this.getCellListCopy();
//		result.removeIf(r -> r.getFloor() != floor);
//		result.removeIf(r -> r.getGridZ() != rowIndex);
//		result.removeIf(r -> !r.isSelectedForBuilding());
//		result.removeIf(r -> !r.isPopulated());
//		return result;
//	}
//
//	public List<RoomGridCell> getSelectedMainStructCells(int floor) {
//		List<RoomGridCell> result = this.getCellListCopy();
//		result.removeIf(r -> r.getFloor() != floor);
//		result.removeIf(r -> !r.isSelectedForBuilding());
//		result.removeIf(r -> !r.isMainStruct());
//		return result;
//	}
//
//	public List<RoomGridCell> getCellsWithoutAType() {
//		List<RoomGridCell> result = this.getCellListCopy();
//		result.removeIf(r -> !r.needsRoomType());
//		return result;
//	}
//
//	/*
//	 * Returns a list (from largest area to smallest area) of contiguous 2d grid areas that are on a given floor and satisfy
//	 * a given condition.
//	 *
//	 * Note: Areas 2x2 or larger will always evaluate as larger than anything 1xN or Nx1. This is done on purpose because
//	 * 2x2+ areas are more useful for building.
//	 */
//	public List<Area2D> getAllGridAreasWhere(int floor, Predicate<RoomGridCell> condition, int minDimension1, int minDimension2) {
//		List<RoomGridPosition> floorPositions = new ArrayList<>();
//		condition = condition.and(c -> c.getFloor() == floor);
//
//		this.getAllCellsWhere(condition).forEach(c -> floorPositions.add(c.getGridPosition()));
//
//		List<Area2D> areas = new ArrayList<>();
//
//		Area2D largest = this.getLargestAreaWhere(floorPositions, condition);
//
//		while (largest != null && !floorPositions.isEmpty() && largest.dimensionsAreAtLeast(minDimension1, minDimension2)) {
//			areas.add(largest);
//			largest.removeFromList(floorPositions);
//
//			largest = this.getLargestAreaWhere(floorPositions, condition);
//		}
//
//		return areas;
//	}
//
//	public Area2D getPotentialRoomBuildArea(RoomGridPosition rootPosition) {
//		int x = 1;
//		int z = 1;
//		boolean incX = true;
//		boolean incZ = true;
//
//		do {
//			if (incX) {
//				++x;
//			}
//			for (int i = 0; i < z; i++) {
//				RoomGridPosition checkPos = rootPosition.move(Direction.EAST, (x - 1)).move(Direction.SOUTH, i);
//				if ((this.getCellAt(checkPos) == null) || (!this.getCellAt(checkPos).needsRoomType())) {
//					incX = false;
//					--x;
//					break;
//				}
//			}
//
//			if (incZ) {
//				++z;
//			}
//			for (int i = 0; i < x; i++) {
//				RoomGridPosition checkPos = rootPosition.move(Direction.EAST, i).move(Direction.SOUTH, (z - 1));
//				if ((this.getCellAt(checkPos) == null) || (!this.getCellAt(checkPos).needsRoomType())) {
//					incZ = false;
//					--z;
//					break;
//				}
//			}
//		} while (incX || incZ);
//
//		return new Area2D(rootPosition, x, z);
//	}
//
//	@Nullable
//	public Area2D getLargestAreaWhere(List<RoomGridPosition> floorPositions, Predicate<RoomGridCell> condition) {
//		int largestArea = 0;
//		int largestX = 0;
//		int largestZ = 0;
//		RoomGridPosition largestStart = null;
//
//		if (!floorPositions.isEmpty()) {
//			for (RoomGridPosition startPos : floorPositions) {
//				int x = 1;
//				int z = 1;
//				boolean incX = true;
//				boolean incZ = true;
//
//				do {
//					if (incX) {
//						++x;
//					}
//					for (int i = 0; i < z; i++) {
//						RoomGridPosition checkPos = startPos.move(Direction.EAST, (x - 1)).move(Direction.SOUTH, i);
//						if (!floorPositions.contains(checkPos) || !this.withinGridBounds(checkPos) || !condition.test(this.getCellAt(checkPos))) {
//							incX = false;
//							--x;
//							break;
//						}
//					}
//
//					if (incZ) {
//						++z;
//					}
//					for (int i = 0; i < x; i++) {
//						RoomGridPosition checkPos = startPos.move(Direction.EAST, i).move(Direction.SOUTH, (z - 1));
//						if (!floorPositions.contains(checkPos) || !this.withinGridBounds(checkPos) || !condition.test(this.getCellAt(checkPos))) {
//							incZ = false;
//							--z;
//							break;
//						}
//					}
//				} while (incX || incZ);
//
//				final int area = x * z;
//
//				// Anything > 2x2 should always evaluate as larger than anything 1xN or Nx1.
//				if ((area > largestArea) || (x > 1 && z > 1 && (largestX == 1 || largestZ == 1))) {
//					largestArea = x * z;
//					largestX = x;
//					largestZ = z;
//					largestStart = startPos;
//				}
//			}
//
//			return new Area2D(largestStart, largestX, largestZ);
//		} else {
//			return null;
//		}
//	}
//
//	public int getContiguousUntypedRoomsX(RoomGridPosition start) {
//		RoomGridPosition pos = start;
//		RoomGridCell cell = this.getCellAt(pos);
//		int result = 0;
//
//		while (cell != null && cell.needsRoomType()) {
//			++result;
//			pos = pos.move(Direction.EAST);
//			cell = this.getCellAt(pos);
//		}
//
//		return result;
//	}
//
//	public int getContiguousUntypedRoomsZ(RoomGridPosition start) {
//		RoomGridPosition pos = start;
//		RoomGridCell cell = this.getCellAt(pos);
//		int result = 0;
//
//		while (cell != null && cell.needsRoomType()) {
//			++result;
//			pos = pos.move(Direction.SOUTH);
//			cell = this.getCellAt(pos);
//		}
//
//		return result;
//	}
//
//	public RoomGridCell getCellAt(int floor, int x, int z) {
//		if (this.withinGridBounds(floor, x, z)) {
//			return this.cellArray[floor][x][z];
//		} else {
//			return null;
//		}
//	}
//
//	@Nullable
//	public RoomGridCell getCellAt(RoomGridPosition position) {
//		if (this.withinGridBounds(position.getFloor(), position.getX(), position.getZ())) {
//			return this.cellArray[position.getFloor()][position.getX()][position.getZ()];
//		} else {
//			return null;
//		}
//	}
//
//	public void selectBlockOfCellsForBuilding(Area2D area, int numFloors) {
//		List<RoomGridPosition> positions = area.getPositionList();
//		for (RoomGridPosition areaPos : positions) {
//			int floor = 0;
//			RoomGridPosition gridPos;
//			RoomGridCell cell;
//
//			// First go through each floor and select the cells for building
//			for (; floor < numFloors; floor++) {
//				gridPos = areaPos.move(Direction.UP, floor);
//				cell = this.getCellAt(gridPos);
//				if (cell != null) {
//					cell.selectForBuilding();
//				}
//			}
//
//			// Then set the one cell above floors to buildable to begin the next layer
//			gridPos = areaPos.move(Direction.UP, floor);
//			cell = this.getCellAt(gridPos);
//			if (cell != null) {
//				cell.setBuildable();
//			}
//		}
//
//		this.initPathingForCellArea(area, numFloors);
//	}
//
//	public void initPathingForCellArea(Area2D baseArea, int floors) {
//		for (int floor = 0; floor < floors; floor++) {
//			Area2D currentFloorArea;
//			if (floor == 0) {
//				currentFloorArea = baseArea;
//			} else {
//				currentFloorArea = baseArea.addFloors(floor);
//			}
//
//			Set<RoomGridCell> cellsInArea = new HashSet<>();
//			currentFloorArea.getPositionList().forEach(p -> cellsInArea.add(this.getCellAt(p)));
//
//			for (RoomGridCell cell : cellsInArea) {
//				if (cell == null) {
//					CQRMain.logger.error("How did this happen?");
//					return;
//				}
//				cell.addPathableCells(cellsInArea);
//			}
//			this.setPathingForCellSet(cellsInArea);
//		}
//	}
//
//	public void initPathingForSingleCell(RoomGridPosition gridPos) {
//		if (this.withinGridBounds(gridPos) && this.getCellAt(gridPos) != null) {
//			Set<RoomGridCell> cellsInArea = new HashSet<>();
//			cellsInArea.add(this.getCellAt(gridPos));
//
//			this.setPathingForCellSet(cellsInArea);
//		}
//	}
//
//	private void setPathingForCellSet(Set<RoomGridCell> cellsInArea) {
//		Set<RoomGridCell> masterPathList = new HashSet<>();
//
//		// For each cell in this area
//		for (RoomGridCell cell : cellsInArea) {
//			masterPathList.add(cell); // make sure to include this cell in pathing
//
//			// Check each horizontal (EnumFacing order is S W N E)
//			for (Direction direction : Direction.HORIZONTALS) {
//				// If adjacent cell isn't part of my area and is selected
//				RoomGridCell adjacent = this.getAdjacentCell(cell, direction);
//				if (adjacent != null && // adjacent cell exists
//						!cellsInArea.contains(adjacent) && // not part of my area already
//						adjacent.isSelectedForBuilding() && // adjacent cell is selected
//						!cell.getPathableCellsCopy().contains(adjacent)) // I haven't already been pathed to it
//				{
//					// maintain a "superset" of all pathable sets of cells we see around us
//					masterPathList.addAll(adjacent.getPathableCellsCopy());
//				}
//			}
//		}
//
//		// Reflexively copy out the new master list so all connected sections path to each other
//		// This MUST be done last, since we don't know who is around the new cell area until we check all of them
//		for (RoomGridCell cell : masterPathList) {
//			cell.addPathableCells(masterPathList);
//		}
//	}
//
//	public boolean adjacentCellIsPopulated(RoomGridCell startCell, Direction direction) {
//		RoomGridCell adjacent = this.getAdjacentCell(startCell, direction);
//		return (adjacent != null && adjacent.isPopulated());
//	}
//
//	public boolean adjacentCellIsFullRoom(RoomGridCell startCell, Direction direction) {
//		RoomGridCell adjacent = this.getAdjacentCell(startCell, direction);
//		return (adjacent != null && adjacent.isPopulated() && !(adjacent.getRoom() instanceof CastleRoomWalkableRoof));
//	}
//
//	public boolean adjacentCellIsSelected(RoomGridCell startCell, Direction direction) {
//		RoomGridCell adjacent = this.getAdjacentCell(startCell, direction);
//		return (adjacent != null && adjacent.isSelectedForBuilding());
//	}
//
//	public boolean adjacentCellIsWalkableRoof(RoomGridCell startCell, Direction direction) {
//		RoomGridCell adjacent = this.getAdjacentCell(startCell, direction);
//		return (adjacent != null && adjacent.isPopulated() && adjacent.getRoom() instanceof CastleRoomWalkableRoof);
//	}
//
//	public boolean cellIsValidForRoof(RoomGridCell cell) {
//		RoomGridCell below = this.getAdjacentCell(cell, Direction.DOWN);
//
//		return (below != null && !cell.isSelectedForBuilding() && below.isPopulated() && (below.getFloor() != this.bossArea.start.getFloor())); // Don't want
//																																				// to build
//																																				// roofs
//																																				// over the
//																																				// boss
//																																				// floor
//																																				// rooms
//	}
//
//	public boolean cellIsOuterEdge(RoomGridCell cell, Direction direction) {
//		RoomGridPosition coords = cell.getGridPosition();
//
//		coords = coords.move(direction);
//		while (this.withinGridBounds(coords)) {
//			if (this.getCellAt(coords).isPopulated()) {
//				return false;
//			}
//			coords = coords.move(direction);
//		}
//
//		return true;
//	}
//
//	public List<RoomGridCell> getBridgeCells(RoomGridCell cell, Direction direction) {
//		List<RoomGridCell> result = new ArrayList<>();
//
//		Optional<RoomGridCell> next = cell.getAdjacentCell(direction);
//		while (next.isPresent() && next.get().isValidForBridge()) {
//			result.add(next.get());
//			next = next.get().getAdjacentCell(direction);
//		}
//
//		if (!next.isPresent()) {
//			// If we hit a null cell that means we hit the edge of the castle grid
//			result.clear(); // Clear the bridge cell array - can't build a bridge here
//		} else if (!next.get().isPopulated()) {
//			// Have to end on a populated room, otherwise it's a bridge to nowhere
//			result.clear();
//		} else if (next.get().isPopulated() && !next.get().reachableFromSide(direction.getOpposite())) {
//			// If we hit another room, make sure that room can exit to the bridge
//			result.clear();
//		} else if (next.get().isPopulated() && next.get().getRoom() instanceof CastleRoomReplacedRoof) {
//			// Don't want to path to replaced roofs either
//			result.clear();
//		}
//
//		return result;
//	}
//
//	public List<RoomGridCell> getAdjacentSelectedCellsInRow(RoomGridPosition position) {
//		List<RoomGridCell> result = new ArrayList<>();
//
//		while (this.getCellAt(position.move(Direction.WEST)) != null && this.getCellAt(position.move(Direction.WEST)).isSelectedForBuilding()) {
//			position = position.move(Direction.WEST);
//		}
//
//		result.add(this.getCellAt(position));
//
//		while (this.getCellAt(position.move(Direction.EAST)) != null && this.getCellAt(position.move(Direction.EAST)).isSelectedForBuilding()) {
//			position = position.move(Direction.EAST);
//			result.add(this.getCellAt(position));
//		}
//
//		return result;
//	}
//
//	public List<RoomGridCell> getAdjacentSelectedCellsInColumn(RoomGridPosition position) {
//		List<RoomGridCell> result = new ArrayList<>();
//
//		while (this.getCellAt(position.move(Direction.NORTH)) != null && this.getCellAt(position.move(Direction.NORTH)).isSelectedForBuilding()) {
//			position = position.move(Direction.NORTH);
//		}
//
//		result.add(this.getCellAt(position));
//
//		while (this.getCellAt(position.move(Direction.SOUTH)) != null && this.getCellAt(position.move(Direction.SOUTH)).isSelectedForBuilding()) {
//			position = position.move(Direction.SOUTH);
//			result.add(this.getCellAt(position));
//		}
//
//		return result;
//	}
//
//	/*
//	 * Determine if a tower can be attached next to the given cell
//	 */
//	public boolean canAttachTower(RoomGridCell cell, Direction side) {
//		RoomGridCell adjacent = this.getAdjacentCell(cell, side);
//
//		return (!cell.getRoom().isTower() && adjacent != null && (!adjacent.isPopulated() || cell.getRoom().isStairsOrLanding()));
//	}
//
//	public double distanceBetweenCells2D(RoomGridCell c1, RoomGridCell c2) {
//		int distX = Math.abs(c1.getGridX() - c2.getGridX());
//		int distZ = Math.abs(c1.getGridZ() - c2.getGridZ());
//		return (Math.hypot(distX, distZ));
//	}
//
//	public boolean cellBordersRoomType(RoomGridCell cell, EnumRoomType type) {
//		for (Direction side : Direction.HORIZONTALS) {
//			RoomGridCell adjacent = this.getAdjacentCell(cell, side);
//			if (adjacent != null && adjacent.isPopulated() && adjacent.getRoom().getRoomType() == type) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	@Nullable
//	public RoomGridCell getAdjacentCell(RoomGridCell startCell, Direction direction) {
//		RoomGridPosition position = startCell.getGridPosition().move(direction);
//
//		if (this.withinGridBounds(position)) {
//			return this.cellArray[position.getFloor()][position.getX()][position.getZ()];
//		} else {
//			return null;
//		}
//	}
//
//	public void setBossArea(Area2D area) {
//		this.bossArea = new Area2D(area);
//	}
//
//	public List<CastleMainStructWall> getWallListCopy() {
//		return new ArrayList<>(this.wallList);
//	}
//
//	@Nullable
//	public Area2D getBossArea() {
//		return this.bossArea;
//	}
//
//	public boolean bossAreaSet() {
//		return this.bossArea != null;
//	}
//
//	public boolean withinGridBounds(int floor, int x, int z) {
//		return (floor >= 0 && floor < this.floors && this.withinFloorBounds(x, z));
//	}
//
//	public boolean withinGridBounds(RoomGridPosition position) {
//		return (position.getFloor() >= 0 && position.getFloor() < this.floors && this.withinFloorBounds(position.getX(), position.getZ()));
//	}
//
//	public boolean withinFloorBounds(int x, int z) {
//		return (x >= 0 && x < this.roomsX && z >= 0 && z < this.roomsZ);
//	}
//
//}
