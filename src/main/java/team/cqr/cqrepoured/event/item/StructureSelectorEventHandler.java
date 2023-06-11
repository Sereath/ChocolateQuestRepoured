package team.cqr.cqrepoured.event.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.item.ItemStructureSelector;
import team.cqr.cqrepoured.network.client.packet.CPacketStructureSelector;

@EventBusSubscriber(modid = CQRMain.MODID)
public class StructureSelectorEventHandler {

	@SubscribeEvent
	public static void onLeftClickBlockEvent(PlayerInteractEvent.LeftClickBlock event) {
		PlayerEntity player = event.getPlayer();
		ItemStack stack = player.getItemInHand(event.getHand());

		if (stack.getItem() instanceof ItemStructureSelector) {
			if (!player.level.isClientSide()) {
				((ItemStructureSelector) stack.getItem()).setFirstPos(stack, player.isCrouching() ? player.blockPosition() : event.getPos(), player);
			}

			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onLeftClickEmptyEvent(PlayerInteractEvent.LeftClickEmpty event) {
		PlayerEntity player = event.getPlayer();
		ItemStack stack = player.getItemInHand(event.getHand());

		if (stack.getItem() instanceof ItemStructureSelector && player.isCrouching()) {
			CQRMain.NETWORK.sendToServer(new CPacketStructureSelector(event.getHand()));
		}
	}

}
