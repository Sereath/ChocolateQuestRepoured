package team.cqr.cqrepoured.capability.protectedregions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.NonNullSupplier;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.capability.SerializableCapabilityProvider;

public class CapabilityProtectedRegionDataProvider extends SerializableCapabilityProvider<CapabilityProtectedRegionData> {

	public static final ResourceLocation LOCATION = new ResourceLocation(CQRMain.MODID, "protected_region_data");

	@CapabilityInject(CapabilityProtectedRegionData.class)
	public static final Capability<CapabilityProtectedRegionData> PROTECTED_REGION_DATA = null;

	public CapabilityProtectedRegionDataProvider(Capability<CapabilityProtectedRegionData> capability, NonNullSupplier<CapabilityProtectedRegionData> instanceSupplier) {
		super(capability, instanceSupplier);
	}

	public static void register() {
		CapabilityManager.INSTANCE.register(CapabilityProtectedRegionData.class, new CapabilityProtectedRegionDataStorage(), () -> new CapabilityProtectedRegionData(null));
	}

	public static CapabilityProtectedRegionDataProvider createProvider(LevelChunk chunk) {
		return new CapabilityProtectedRegionDataProvider(CapabilityProtectedRegionDataProvider.PROTECTED_REGION_DATA, () -> new CapabilityProtectedRegionData(chunk));
	}

	public static CapabilityProtectedRegionData get(LevelChunk chunk) {
		return chunk.getCapability(PROTECTED_REGION_DATA)
				.orElseThrow(NullPointerException::new);
	}

}
