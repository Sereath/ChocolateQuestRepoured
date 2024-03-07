package team.cqr.cqrepoured.generation.world.level.levelgen.structure;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;

public interface InhabitantSelector {

	Codec<InhabitantSelector> CODEC = null;

	DungeonInhabitant get(GenerationContext context, BlockPos pos);

}
