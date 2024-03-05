package team.cqr.cqrepoured.world.structure.generation.generators.hangingcity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.Tuple;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import team.cqr.cqrepoured.config.CQRConfig;
import team.cqr.cqrepoured.generation.world.level.levelgen.structure.CQRStructurePiece;
import team.cqr.cqrepoured.util.DungeonGenUtils;
import team.cqr.cqrepoured.world.structure.generation.GenerationUtil;
import team.cqr.cqrepoured.world.structure.generation.WorldDungeonGenerator;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonHangingCity;
import team.cqr.cqrepoured.world.structure.generation.generators.AbstractDungeonGenerationComponent;
import team.cqr.cqrepoured.world.structure.generation.generators.SuspensionBridgeHelper;
import team.cqr.cqrepoured.world.structure.generation.inhabitants.DungeonInhabitant;
import team.cqr.cqrepoured.world.structure.generation.structurefile.CQStructure;
import team.cqr.cqrepoured.world.structure.generation.structurefile.Offset;

public class HangingCityBuilding extends AbstractDungeonGenerationComponent<DungeonHangingCity, GeneratorHangingCity> {

	private final int gridPosX;
	private final int gridPosY;

	private int islandRadius;

	/* Marks the center of the island */
	private final BlockPos worldPosition;
	private Set<HangingCityBuilding> connectedIslands = new HashSet<>();
	private final CQStructure structure;

	private Set<SuspensionBridgeHelper> bridges = new HashSet<>();

	public HangingCityBuilding(GeneratorHangingCity generator, final int posX, final int posY, final CQStructure structure) {
		super(generator);
		this.gridPosX = posX;
		this.gridPosY = posY;
		this.structure = structure;

		this.islandRadius = this.structure.getSize().getX() > this.structure.getSize().getZ() ? this.structure.getSize().getX() : this.structure.getSize().getZ();

		this.worldPosition = this.generator.getCenterPosForIsland(this);
	}

	public HangingCityBuilding[] getNeighbours() {
		HangingCityBuilding[] neighbours = new HangingCityBuilding[8];
		/*
		 * Indexes
		 * [0, 3, 5]
		 * [1, -, 6]
		 * [2, 4, 7]
		 */
		int i = 0;
		for (int ix = this.gridPosX - 1; ix <= this.gridPosX + 1; ix++) {
			for (int iy = this.gridPosY - 1; iy <= this.gridPosY + 1; iy++) {
				// Avoid adding ourselves to the neighbours list, we are not our own neighbour...
				if (ix == this.gridPosX && iy == this.gridPosY) {
					continue;
				}
				neighbours[i] = this.generator.getBuildingFromGridPos(ix, iy);

				i++;
			}
		}

		return neighbours;
	}

	public boolean hasFreeNeighbourSpots() {
		return !this.getFreeNeighbourSpots().isEmpty();
	}

	public boolean isConnectedToAnyBuilding() {
		return !this.connectedIslands.isEmpty();
	}

	public boolean isConnectedTo(HangingCityBuilding building) {
		return this.connectedIslands.contains(building);
	}

	public void connectTo(HangingCityBuilding building) {
		// DONE: Build bridge (generation: postProcess())
		BlockPos bridgePosOne = this.getConnectorPointForBridgeTo(building);
		BlockPos bridgePosTwo = building.getConnectorPointForBridgeTo(this);

		this.bridges.add(new SuspensionBridgeHelper(this.dungeon, bridgePosOne, bridgePosTwo));

		this.connectedIslands.add(building);
		building.markAsConnected(this);
	}

	public Set<Tuple<Integer, Integer>> getFreeNeighbourSpots() {
		Set<Tuple<Integer, Integer>> result = new HashSet<>();
		for (int ix = this.gridPosX - 1; ix <= this.gridPosX + 1; ix++) {
			for (int iy = this.gridPosY - 1; iy <= this.gridPosY + 1; iy++) {
				// Avoid adding ourselves to the neighbours list, we are not our own neighbour...
				if (ix == this.gridPosX && iy == this.gridPosY) {
					continue;
				}
				if (this.generator.getBuildingFromGridPos(ix, iy) == null) {
					result.add(new Tuple<>(ix, iy));
				}
			}
		}
		return result;
	}

	public void markAsConnected(HangingCityBuilding connectionInitializer) {
		this.connectedIslands.add(connectionInitializer);
	}

	@Override
	public void preProcess(Level world, CQRStructurePiece.Builder dungeonBuilder, DungeonInhabitant mobType) {
		// Order: Air, Island, Chains, Building
		int rad = 2 * this.getRadius();
		int height = this.dungeon.getYFactorHeight() > this.structure.getSize().getY() ? this.dungeon.getYFactorHeight() : this.structure.getSize().getY();
		BlockPos start = this.worldPosition.offset(-rad, -this.dungeon.getYFactorHeight(), -rad);
		BlockPos end = this.worldPosition.offset(rad, height, rad);

		int wall = CQRConfig.SERVER_CONFIG.general.supportHillWallSize.get();
		GenerationUtil.makeRandomBlob2(dungeonBuilder.getLevel(), Blocks.AIR, start, end, wall, WorldDungeonGenerator.getSeed(dungeonBuilder.getLevel().getSeed(), this.generator.getPos().getX() >> 4, this.generator.getPos().getZ() >> 4), start.offset(-wall, -wall, -wall));
	}

	@Override
	public void generate(Level world, CQRStructurePiece.Builder dungeonBuilder, DungeonInhabitant mobType) {
		this.buildPlatform(world, this.worldPosition, this.islandRadius, mobType, dungeonBuilder);
		if (this.structure != null) {
			this.structure.addAll(dungeonBuilder, this.worldPosition.above(), Offset.CENTER);
		}
	}

	private void buildPlatform(Level world, BlockPos center, int radius, DungeonInhabitant mobType, CQRStructurePiece.Builder dungeonBuilder) {
		Map<BlockPos, BlockState> stateMap = new HashMap<>();
		int decrementor = 0;
		int rad = (int) (1.05D * radius);
		rad += 2;
		while (decrementor < (rad / 2)) {
			rad -= decrementor;

			for (int iX = -rad; iX <= rad; iX++) {
				for (int iZ = -rad; iZ <= rad; iZ++) {
					if (DungeonGenUtils.isInsideCircle(iX, iZ, rad)) {
						stateMap.put((center.offset(iX, -decrementor, iZ)), this.dungeon.getIslandBlock());
					}
				}
			}

			decrementor++;
		}

		if (this.dungeon.doBuildChains()) {
			this.buildChain(world, center.offset(radius * 0.75, -2, radius * 0.75), 0, stateMap);
			this.buildChain(world, center.offset(-radius * 0.75, -2, -radius * 0.75), 0, stateMap);
			this.buildChain(world, center.offset(-radius * 0.75, -2, radius * 0.75), 1, stateMap);
			this.buildChain(world, center.offset(radius * 0.75, -2, -radius * 0.75), 1, stateMap);
		}

		for (Map.Entry<BlockPos, BlockState> entry : stateMap.entrySet()) {
			dungeonBuilder.getLevel().setBlockState(entry.getKey().subtract(center), entry.getValue());
		}
	}

	private void buildChain(Level world, BlockPos pos, int iOffset, Map<BlockPos, BlockState> stateMap) {
		/*
		 * Chain from side: # # # # # # # # # # # # # # # # # # # #
		 */
		int deltaYPerChainSegment = 5;

		/*
		 * int maxY = DungeonGenUtils.getYForPos(this.world, pos.getX(), pos.getZ(), true);
		 * maxY = maxY >= 255 ? 255 : maxY;
		 */
		// Or: Change this to something like "world.getMaxBuildHeight()", if that exists.
		int maxY = world.getHeight();
		int chainCount = (maxY - pos.getY()) / deltaYPerChainSegment;
		for (int i = 0; i < chainCount; i++) {
			// Check the direction of the chain
			int yOffset = i * deltaYPerChainSegment;
			BlockPos startPos = pos.offset(0, yOffset, 0);
			if ((i + iOffset) % 2 > 0) {
				this.buildChainSegment(startPos, startPos.north(), startPos.south(), startPos.north(2).above(), startPos.south(2).above(), stateMap);
			} else {
				this.buildChainSegment(startPos, startPos.east(), startPos.west(), startPos.east(2).above(), startPos.west(2).above(), stateMap);
			}
		}
	}

	private void buildChainSegment(BlockPos lowerCenter, BlockPos lowerLeft, BlockPos lowerRight, BlockPos lowerBoundL, BlockPos lowerBoundR, Map<BlockPos, BlockState> stateMap) {
		stateMap.put(lowerCenter, this.dungeon.getChainBlock());
		stateMap.put(lowerCenter.offset(0, 6, 0), this.dungeon.getChainBlock());

		stateMap.put(lowerLeft, this.dungeon.getChainBlock());
		stateMap.put(lowerLeft.offset(0, 6, 0), this.dungeon.getChainBlock());

		stateMap.put(lowerRight, this.dungeon.getChainBlock());
		stateMap.put(lowerRight.offset(0, 6, 0), this.dungeon.getChainBlock());

		for (int i = 0; i < 5; i++) {
			stateMap.put(lowerBoundL.offset(0, i, 0), this.dungeon.getChainBlock());
			stateMap.put(lowerBoundR.offset(0, i, 0), this.dungeon.getChainBlock());
		}
	}

	@Override
	public void generatePost(Level world, CQRStructurePiece.Builder dungeonBuilder, DungeonInhabitant mobType) {
		if (this.dungeon.isConstructBridges()) {
			for (SuspensionBridgeHelper bridge : this.bridges) {
				Map<BlockPos, BlockState> stateMap = new HashMap<>();
				bridge.generate(stateMap);

				for (Map.Entry<BlockPos, BlockState> entry : stateMap.entrySet()) {
					dungeonBuilder.getLevel().setBlockState(entry.getKey().subtract(this.generator.getPos()), entry.getValue());
				}
			}
		}
	}

	BlockPos getConnectorPointForBridgeTo(final HangingCityBuilding bridgeTarget) {
		final BlockPos vIn = bridgeTarget.getWorldPosition().subtract(this.getWorldPosition());
		Vec3 bridgeVector = new Vec3(vIn.getX(), vIn.getY(), vIn.getZ());
		Vec3 horizontalVector = new Vec3(bridgeVector.x, 0, bridgeVector.z);
		horizontalVector = horizontalVector.normalize();
		horizontalVector = horizontalVector.scale((1.05D * this.islandRadius) - 2);

		final BlockPos result = this.getWorldPosition().offset(horizontalVector.x, 1, horizontalVector.z);

		return result;
	}

	int getGridPosX() {
		return this.gridPosX;
	}

	int getGridPosY() {
		return this.gridPosY;
	}

	BlockPos getWorldPosition() {
		return this.worldPosition;
	}

	int getRadius() {
		return this.islandRadius;
	}

}
