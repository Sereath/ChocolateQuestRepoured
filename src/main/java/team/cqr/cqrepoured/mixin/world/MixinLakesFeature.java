package team.cqr.cqrepoured.mixin.world;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.registries.RegistryObject;
import team.cqr.cqrepoured.init.CQRStructures;

@Mixin(LakesFeature.class)
public class MixinLakesFeature {
	
	@Inject(
            method = "place(Lnet/minecraft/world/ISeedReader;Lnet/minecraft/world/gen/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/BlockStateFeatureConfig;)Z",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/BlockPos;below(I)Lnet/minecraft/util/math/BlockPos;"),
            cancellable = true
    )
	private void cqr_noLakesInStructures(ISeedReader serverWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BlockStateFeatureConfig singleStateFeatureConfig, CallbackInfoReturnable<Boolean> cir) {
		SectionPos sectionPos = SectionPos.of(blockPos);
		for(RegistryObject<Structure<?>> regObj : CQRStructures.STRUCTURES.getEntries()) {
			Structure<?> structure = regObj.get();
			if (serverWorldAccess.startsForFeature(sectionPos, structure).findAny().isPresent()) {
				cir.setReturnValue(false);
				return;
			}
		}
	}

}
