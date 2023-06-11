package team.cqr.cqrepoured.network.client.handler.endercalamity;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import team.cqr.cqrepoured.entity.boss.endercalamity.EntityCQREnderCalamity;
import team.cqr.cqrepoured.network.AbstractPacketHandler;
import team.cqr.cqrepoured.network.server.packet.endercalamity.SPacketSyncCalamityRotation;

public class CPacketHandlerSyncCalamityRotation extends AbstractPacketHandler<SPacketSyncCalamityRotation> {

	@Override
	protected void execHandlePacket(SPacketSyncCalamityRotation packet, Supplier<Context> context, World world, PlayerEntity player) {
		Entity entity = world.getEntity(packet.getEntityId());
		
		if(entity instanceof EntityCQREnderCalamity) {
			((EntityCQREnderCalamity)entity).serverRotationPitchCQR = packet.getPitch();
		}
	}

}
