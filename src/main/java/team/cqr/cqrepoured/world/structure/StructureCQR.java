package team.cqr.cqrepoured.world.structure;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraftforge.fml.ModList;
import team.cqr.cqrepoured.generation.init.CQRStructureTypes;
import team.cqr.cqrepoured.protection.ProtectionSettings;
import team.cqr.cqrepoured.world.structure.generation.DungeonDataManager;
import team.cqr.cqrepoured.world.structure.generation.WorldDungeonGenerator;
import team.cqr.cqrepoured.world.structure.generation.dungeons.DungeonInhabitantMap;
import team.cqr.cqrepoured.world.structure.generation.dungeons.PlacementSettings;
import team.cqr.cqrepoured.world.structure.generation.generators.StructurePieceGenerator;

public class StructureCQR extends Structure {

	public static final Codec<StructureCQR> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
        		settingsCodec(instance),
				Codec.BOOL.fieldOf("enabled").forGetter(StructureCQR::enabled),
				Codec.INT.fieldOf("icon").forGetter(StructureCQR::icon),
				Codec.list(Codec.STRING).fieldOf("mod_dependencies").forGetter(StructureCQR::modDependencies),
				PlacementSettings.CODEC.fieldOf("placement_settings").forGetter(StructureCQR::placementSettings),
				DungeonInhabitantMap.CODEC.fieldOf("inhabitants").forGetter(StructureCQR::inhabitants),
				Codec.INT.fieldOf("ground_level_delta").forGetter(StructureCQR::groundLevelDelta),
				ProtectionSettings.CODEC.optionalFieldOf("protection_settings").forGetter(StructureCQR::protectionSettings),
				StructurePieceGenerator.CODEC.fieldOf("generator").forGetter(StructureCQR::generator))
        		.apply(instance, StructureCQR::new);
     });

	private final boolean enabled;
	private final int icon;
	private final List<String> modDependencies;
	private final PlacementSettings placementSettings;
	private final DungeonInhabitantMap inhabitants;
	private final int groundLevelDelta;
	private final Optional<ProtectionSettings> protectionSettings;
	private final StructurePieceGenerator generator;

	public StructureCQR(StructureSettings structureSettings, boolean enabled, int icon, List<String> modDependencies, PlacementSettings placementSettings,
			DungeonInhabitantMap inhabitants, int groundLevelDelta, Optional<ProtectionSettings> protectionSettings, StructurePieceGenerator generator) {
		super(structureSettings);
		this.enabled = enabled;
		this.icon = icon;
		this.modDependencies = modDependencies;
		this.placementSettings = placementSettings;
		this.inhabitants = inhabitants;
		this.groundLevelDelta = groundLevelDelta;
		this.protectionSettings = protectionSettings;
		this.generator = generator;
	}

	@Override
	public StructureType<?> type() {
		return CQRStructureTypes.CQR_STRUCTURE_TYPE;
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		if (!this.enabled) {
			return Optional.empty();
		}
		if (!this.modDependencies.stream().allMatch(ModList.get()::isLoaded)) {
			return Optional.empty();
		}
		ServerLevel level = WorldDungeonGenerator.getLevel(context.chunkGenerator());
		ResourceLocation structureName = context.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(this);
		if (DungeonDataManager.getDungeonGenerationCount(level, structureName) >= this.placementSettings.spawnLimit()) {
			return Optional.empty();
		}
		if (!DungeonDataManager.getSpawnedDungeonNames(level).containsAll(this.placementSettings.dungeonDependencies())) {
			return Optional.empty();
		}
		if (!this.placementSettings.positionValidator().validatePosition(context.chunkPos())) {
			return Optional.empty();
		}
		// TODO check for nearby non-cqr structures
		BlockPos pos = this.placementSettings.positionFinder().findPosition(context, context.chunkPos());
		return Optional.of(new GenerationStub(pos, this.createGenerator(context, pos)));
	}

	private Consumer<StructurePiecesBuilder> createGenerator(GenerationContext context, BlockPos pos) {
		return structurePiecesBuilder -> structurePiecesBuilder.addPiece(
				this.generator.createStructurePiece(context, pos, this.inhabitants.get(context, pos), this.groundLevelDelta, this.protectionSettings));
	}

	public StructureStart createStructureStart(ServerLevel level, BlockPos pos) {
		ChunkPos chunkPos = new ChunkPos(pos);
		GenerationContext context = new GenerationContext(
				level.registryAccess(),
				level.getChunkSource().getGenerator(),
				level.getChunkSource().getGenerator().getBiomeSource(),
				level.getChunkSource().randomState(),
				level.getStructureManager(),
				level.getSeed(),
				chunkPos,
				level,
				biome -> true);
		BlockPos generationPos = this.placementSettings.positionFinder().applyOffsets(context, pos);
		GenerationStub generationStub = new GenerationStub(generationPos, this.createGenerator(context, generationPos));
		StructureStart structureStart = new StructureStart(this, chunkPos, 0, generationStub.getPiecesBuilder().build());
		if (structureStart.isValid()) {
			return structureStart;
		}
		return StructureStart.INVALID_START;
	}

	public boolean enabled() {
		return enabled;
	}

	public int icon() {
		return icon;
	}

	public List<String> modDependencies() {
		return modDependencies;
	}

	public PlacementSettings placementSettings() {
		return placementSettings;
	}

	public DungeonInhabitantMap inhabitants() {
		return inhabitants;
	}

	public int groundLevelDelta() {
		return groundLevelDelta;
	}

	public Optional<ProtectionSettings> protectionSettings() {
		return protectionSettings;
	}

	public StructurePieceGenerator generator() {
		return generator;
	}

}
