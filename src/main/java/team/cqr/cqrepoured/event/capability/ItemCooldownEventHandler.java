package team.cqr.cqrepoured.event.capability;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.capability.armor.CapabilityCooldownHandler;
import team.cqr.cqrepoured.capability.armor.CapabilityCooldownHandlerProvider;
import team.cqr.cqrepoured.network.server.packet.SPacketArmorCooldownSync;

@EventBusSubscriber(modid = CQRMain.MODID)
public class ItemCooldownEventHandler {

	@SubscribeEvent
	public static void onLivingUpdateEvent(LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		LazyOptional<CapabilityCooldownHandler> lOpCap = entity.getCapability(CapabilityCooldownHandlerProvider.CAPABILITY_ITEM_COOLDOWN_CQR, null);
		lOpCap.ifPresent(capa -> capa.tick());
	}

	@SubscribeEvent
	public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
		if (!event.getEntity().level().isClientSide()) {
			LazyOptional<CapabilityCooldownHandler> lOpCap = event.getEntity().getCapability(CapabilityCooldownHandlerProvider.CAPABILITY_ITEM_COOLDOWN_CQR, null);
			
			if(event.getEntity() instanceof ServerPlayer) {
				lOpCap.ifPresent(capa -> {
					CQRMain.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)event.getEntity()), new SPacketArmorCooldownSync(capa.getItemCooldownMap()));
				});
			}
		}
	}

}
