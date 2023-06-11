package team.cqr.cqrepoured.world.structure.generation.generators.hangingcity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import net.minecraft.util.Tuple;
import net.minecraft.core.BlockPos;
import net.minecraft.world.gen.ChunkGenerator;
import team.cqr.cqrepoured.util.DungeonGenUtils;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonHangingCity;
import team.cqr.cqrepoured.world.structure.generation.generators.LegacyDungeonGenerator;
import team.cqr.cqrepoured.world.structure.generation.structurefile.CQStructure;

public class GeneratorHangingCity extends LegacyDungeonGenerator<DungeonHangingCity> {

	// DONE: Air bubble around the whole thing

	private int islandCount = 1;
	private int islandDistance = 1;
	private HangingCityBuilding[][] buildingGrid;
	private Set<HangingCityBuilding> buildings = new HashSet<>();

	// This needs to calculate async (island blocks, chain blocks, air blocks)

	public GeneratorHangingCity(ChunkGenerator chunkGenerator, BlockPos pos, DungeonHangingCity dungeon, Random rand) {
		super(chunkGenerator, pos, dungeon, rand);
	}

	@Override
	public void preProcess() {
		this.islandCount = DungeonGenUtils.randomBetween(this.dungeon.getMinBuildings(), this.dungeon.getMaxBuildings(), this.random);
		this.islandDistance = DungeonGenUtils.randomBetween(this.dungeon.getMinIslandDistance(), this.dungeon.getMaxIslandDistance(), this.random);

		if (this.islandCount % 2 == 0) {
			this.islandCount += 1;
		}

		this.buildingGrid = new HangingCityBuilding[(2 * this.islandCount) + 4][(2 * this.islandCount) + 4];

		final int offsetXY = this.islandCount + 2;

		HangingCityBuilding lastProcessed = null;
		CQStructure structure = CQStructure.createFromFile(this.dungeon.pickCentralStructure(this.random));
		// Create grid
		for (int i = 0; i < this.islandCount; i++) {
			Tuple<Integer, Integer> coords = new Tuple<>(0, 0);
			// If we are not the first (center) building we can use neighbours of existing ones
			if (lastProcessed != null) {
				// First we gotta choose a structure
				structure = CQStructure.createFromFile(this.dungeon.pickStructure(this.random));
				// Then we grab a list of buildings we already processed and shuffle the list
				List<HangingCityBuilding> buildings = new ArrayList<>(this.buildings);
				Collections.shuffle(buildings, this.random);
				// To ensure that we don't end up in a endless loop we use a queue
				Queue<HangingCityBuilding> buildingQueue = new LinkedList<>(buildings);
				while (!buildingQueue.isEmpty()) {
					// Pick a building
					HangingCityBuilding chosen = buildingQueue.remove();
					// If this building has free neighbour spots, we choose one of those spots randomly. If it has no free spots, skip to
					// the next one
					if (!chosen.hasFreeNeighbourSpots()) {
						continue;
					}
					List<Tuple<Integer, Integer>> spots = new ArrayList<>(chosen.getFreeNeighbourSpots());
					Collections.shuffle(spots, this.random);
					coords = spots.get(0);
					// We found our spot, so let's exit this loop
					break;
				}
			}

			this.buildingGrid[offsetXY + coords.getA()][offsetXY + coords.getB()] = new HangingCityBuilding(this, coords.getA(), coords.getB(), structure);
			this.buildingGrid[offsetXY + coords.getA()][offsetXY + coords.getB()].preProcess(this.level, this.dungeonBuilder, null);
			this.buildings.add(this.buildingGrid[offsetXY + coords.getA()][offsetXY + coords.getB()]);
			lastProcessed = this.buildingGrid[offsetXY + coords.getA()][offsetXY + coords.getB()];
		}
		// Calculate bridge connections
		// Needs to call building.connectTo on the first and markAsConnected on the second
		if (this.dungeon.isConstructBridges()) {
			// for each building: First try to connect to a direct (NESW) neighbour, then to a diagonal neighbour
			for (HangingCityBuilding building : this.buildings) {
				HangingCityBuilding[] neighbours = building.getNeighbours();
				/*
				 * Indexes
				 * [0, 3, 5]
				 * [1, -, 6]
				 * [2, 4, 7]
				 */
				// It will always try to connect to direct neighbours first, after that it will try to connect to diagonal neighbours.
				// Once connected it will stop and process the next building
				final int[] directNeighbours = new int[] { 3, 6, 4, 1, 0, 5, 7, 2 }; // N E S W <DIAGONALS>
				// final int[] diagonalNeighbours = new int[] {0,6,8,2};
				for (int in : directNeighbours) {
					if (neighbours[in] != null) {
						/*
						 * if(building.isConnectedToAnyBuilding() && neighbours[in].isConnectedToAnyBuilding()) {
						 * continue;
						 * }
						 */
						if (!building.isConnectedTo(neighbours[in])) {
							building.connectTo(neighbours[in]);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void buildStructure() {
		for (HangingCityBuilding building : this.buildings) {
			building.generate(this.level, this.dungeonBuilder, this.dungeonBuilder.getDefaultInhabitant());
		}
	}

	@Override
	public void postProcess() {
		// Not needed
		for (HangingCityBuilding building : this.buildings) {
			building.generatePost(this.level, this.dungeonBuilder, this.dungeonBuilder.getDefaultInhabitant());
		}
	}

	HangingCityBuilding getBuildingFromGridPos(int x, int y) {
		x += (this.islandCount + 2);
		y += (this.islandCount + 2);

		return this.buildingGrid[x][y];
	}

	final BlockPos getCenterPosForIsland(HangingCityBuilding building) {
		BlockPos centerGen = this.pos;
		int offsetX = this.islandDistance * building.getGridPosX();
		int offsetZ = this.islandDistance * building.getGridPosY();

		int offsetY = this.dungeon.getRandomHeightVariation(this.random);

		final BlockPos pos = centerGen.offset(offsetX, offsetY, offsetZ);
		return pos;
	}

	public BlockPos getPos() {
		return this.pos;
	}

}
