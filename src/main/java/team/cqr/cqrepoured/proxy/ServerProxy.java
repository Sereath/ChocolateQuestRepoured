package team.cqr.cqrepoured.proxy;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.World;

public class ServerProxy implements IProxy {

	@Override
	public void postInit() {

	}

	//Correct?
	@Override
	public PlayerEntity getPlayer(Context context) {
		return context.getSender();
	}

	@Override
	public World getWorld(Context context) {
		return context.getSender().level;
	}

	@Override
	public Advancement getAdvancement(PlayerEntity player, ResourceLocation id) {
		if (player instanceof ServerPlayerEntity) {
			return ((ServerPlayerEntity) player).getLevel().getServer().getAdvancements().getAdvancement(id);
		}
		return null;
	}

	@Override
	public boolean hasAdvancement(PlayerEntity player, ResourceLocation id) {
		if (player instanceof ServerPlayerEntity) {
			Advancement advancement = this.getAdvancement(player, id);
			if (advancement != null) {
				return ((ServerPlayerEntity) player).getAdvancements().getOrStartProgress(advancement).isDone();
			}
		}
		return false;
	}

	@Override
	public void updateGui() {

	}

	@Override
	public boolean isOwnerOfIntegratedServer(PlayerEntity player) {
		return false;
	}

	@Override
	public void openGui(int id, PlayerEntity player, World world, int... args) {

	}

	@Override
	public boolean isPlayerCurrentClientPlayer(PlayerEntity player) {
		return false;
	}

}
