package team.cqr.cqrepoured.network.server.handler;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import team.cqr.cqrepoured.network.AbstractPacketHandler;
import team.cqr.cqrepoured.network.client.packet.CPacketSaveStructureRequest;
import team.cqr.cqrepoured.tileentity.TileEntityExporter;

public class SPacketHandlerSaveStructureRequest extends AbstractPacketHandler<CPacketSaveStructureRequest> {

	@Override
	protected void execHandlePacket(CPacketSaveStructureRequest packet, Supplier<Context> context, World world, PlayerEntity player) {
		TileEntity tileEntity = player.level.getBlockEntity(packet.getPos());

		if (tileEntity instanceof TileEntityExporter) {
			((TileEntityExporter) tileEntity).saveStructure(player);
		}
	}

}
