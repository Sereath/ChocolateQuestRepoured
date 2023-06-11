package team.cqr.cqrepoured.world.structure.generation.generation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import team.cqr.cqrepoured.util.NBTCollectors;

public class CQRLevel implements ICQRLevel {

	private static final ICQRSection EMPTY = new ICQRSection() {

		@Override
		public BlockState getBlockState(BlockPos pos) {
			return null;
		}

		@Override
		public void setBlockState(BlockPos pos, BlockState state, Consumer<BlockEntity> blockEntityCallback) {

		}

		@Override
		public FluidState getFluidState(BlockPos pos) {
			return null;
		}

		@Override
		@Nullable
		public BlockEntity getBlockEntity(BlockPos pos) {
			return null;
		}

		@Override
		public void addEntity(Entity entity) {

		}

	};

	private final SectionPos center;
	private final long seed;
	private final Int2ObjectMap<CQRSection> sections;
	private final BlockGetter blockReader = new BlockGetter() {
		@Override
		public FluidState getFluidState(BlockPos pos) {
			FluidState fluidState = this.getFluidState(pos);
			return fluidState != null ? fluidState : Fluids.EMPTY.defaultFluidState();
		}

		@Override
		public BlockState getBlockState(BlockPos pos) {
			BlockState blockState = this.getBlockState(pos);
			return blockState != null ? blockState : Blocks.AIR.defaultBlockState();
		}

		@Override
		public BlockEntity getBlockEntity(BlockPos pos) {
			return this.getBlockEntity(pos);
		}
	};

	public CQRLevel(SectionPos center, long seed) {
		this.center = center;
		this.seed = seed;
		this.sections = new Int2ObjectOpenHashMap<>();
	}

	public CQRLevel(CompoundTag nbt) {
		this.center = SectionPos.of(nbt.getInt("CenterX"), nbt.getInt("CenterY"), nbt.getInt("CenterZ"));
		this.seed = nbt.getLong("Seed");
		this.sections = NBTCollectors.toInt2ObjectMap(nbt.getCompound("Sections"), (CompoundTag sectionNbt) -> new CQRSection(this, sectionNbt));
	}

	public CompoundTag save() {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("CenterX", this.center.x());
		nbt.putInt("CenterY", this.center.y());
		nbt.putInt("CenterZ", this.center.z());
		nbt.putLong("Seed", this.seed);
		nbt.put("Sections", NBTCollectors.collect(this.sections, CQRSection::save));
		return nbt;
	}

	public void generate(ISeedReader level, MutableBoundingBox box, IEntityFactory entityFactory, @Nonnull List<StructureProcessor> processors) {
		SectionPos.betweenClosedStream(box.x0 >> 4, box.y0 >> 4, box.z0 >> 4, box.x1 >> 4, box.y1 >> 4, box.z1 >> 4).forEach(sectionPos -> {
			CQRSection section = this.sections.get(this.index(sectionPos));
			if (section != null) {
				section.generate(level, entityFactory, processors);
			}
		});
	}

	@Override
	public long getSeed() {
		return this.seed;
	}

	private int index(SectionPos pos) {
		return this.index(pos.x(), pos.y(), pos.z());
	}

	private int index(BlockPos pos) {
		return this.indexFromBlock(pos.getX(), pos.getY(), pos.getZ());
	}

	private int index(int sectionX, int sectionY, int sectionZ) {
		sectionX += 512 - this.center.x();
		sectionY += 512 - this.center.y();
		sectionZ += 512 - this.center.z();
		return sectionX << 20 | sectionY << 10 | sectionZ;
	}

	private int indexFromBlock(int blockX, int blockY, int blockZ) {
		return this.index(blockX >> 4, blockY >> 4, blockZ >> 4);
	}

	public ICQRSection getSection(SectionPos pos) {
		ICQRSection section = this.sections.get(this.index(pos));
		return section != null ? section : EMPTY;
	}

	public ICQRSection getSection(BlockPos pos) {
		ICQRSection section = this.sections.get(this.index(pos));
		return section != null ? section : EMPTY;
	}

	public ICQRSection getOrCreateSection(SectionPos pos) {
		return this.sections.computeIfAbsent(this.index(pos), k -> new CQRSection(this, pos));
	}

	public ICQRSection getOrCreateSection(BlockPos pos) {
		return this.sections.computeIfAbsent(this.index(pos), k -> new CQRSection(this, SectionPos.of(pos)));
	}

	@Override
	@Nullable
	public BlockState getBlockState(BlockPos pos) {
		return this.getSection(pos).getBlockState(pos);
	}

	@Override
	public void setBlockState(BlockPos pos, @Nullable BlockState state, @Nullable Consumer<BlockEntity> blockEntityCallback) {
		this.getOrCreateSection(pos).setBlockState(pos, state, blockEntityCallback);
	}

	@Override
	@Nullable
	public FluidState getFluidState(BlockPos pos) {
		return this.getSection(pos).getFluidState(pos);
	}

	@Override
	@Nullable
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.getSection(pos).getBlockEntity(pos);
	}

	@Override
	public void addEntity(Entity entity) {
		this.getOrCreateSection(entity.blockPosition()).addEntity(entity);
	}

	public Collection<CQRSection> getSections() {
		return Collections.unmodifiableCollection(this.sections.values());
	}

	public BlockGetter asBlockReader() {
		return this.blockReader;
	}

}
