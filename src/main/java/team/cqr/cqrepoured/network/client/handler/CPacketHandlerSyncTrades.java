package team.cqr.cqrepoured.network.client.handler;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;
import team.cqr.cqrepoured.entity.trade.TraderOffer;
import team.cqr.cqrepoured.inventory.ContainerMerchant;
import team.cqr.cqrepoured.network.AbstractPacketHandler;
import team.cqr.cqrepoured.network.server.packet.SPacketSyncTrades;

public class CPacketHandlerSyncTrades extends AbstractPacketHandler<SPacketSyncTrades> {

	@Override
	protected void execHandlePacket(SPacketSyncTrades message, Supplier<Context> context, World world, PlayerEntity player) {
		Entity entity = world.getEntity(message.getEntityId());

		if (entity instanceof AbstractEntityCQR) {
			TraderOffer trades = ((AbstractEntityCQR) entity).getTrades();

			trades.readFromNBT(message.getTrades());
			if (player.containerMenu instanceof ContainerMerchant) {
				((ContainerMerchant) player.containerMenu).onTradesUpdated();
			}
			CQRMain.PROXY.updateGui();
		}
	}

}
