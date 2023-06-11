package team.cqr.cqrepoured.client.world.structure.protection;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.network.server.packet.SPacketAddOrResetProtectedRegionIndicator;

public class ProtectionIndicatorHelper {

	public static void addOrResetProtectedRegionIndicator(World world, UUID uuid, BlockPos start, BlockPos end, BlockPos pos, @Nullable ServerPlayerEntity player) {
		if (world.isClientSide) {
			RayTraceResult result = Minecraft.getInstance().hitResult; //Old: objectMouseOver
			if (result != null && result.getLocation() != null) {
				world.addParticle(ParticleTypes.HAPPY_VILLAGER, result.getLocation().x, result.getLocation().y, result.getLocation().z, 0.0D, 0.0D, 0.0D);
			}
		} else if (player != null) {
			CQRMain.NETWORK.sendToServer(new SPacketAddOrResetProtectedRegionIndicator(uuid, start, end, pos)); //Correct?
		}
	}

}
