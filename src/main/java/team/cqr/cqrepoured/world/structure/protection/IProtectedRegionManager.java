package team.cqr.cqrepoured.world.structure.protection;

import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;

public interface IProtectedRegionManager {

	@Nullable
	ProtectedRegion getProtectedRegion(UUID uuid);

	void addProtectedRegion(ProtectedRegion protectedRegion);

	default void removeProtectedRegion(ProtectedRegion protectedRegion) {
		this.removeProtectedRegion(protectedRegion.uuid());
	}

	void removeProtectedRegion(UUID uuid);

	Stream<ProtectedRegion> getProtectedRegions();

	Stream<ProtectedRegion> getProtectedRegionsAt(BlockPos pos);

	void clearProtectedRegions();

}
