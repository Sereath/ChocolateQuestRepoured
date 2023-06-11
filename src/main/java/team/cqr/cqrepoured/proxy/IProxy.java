package team.cqr.cqrepoured.proxy;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.World;

public interface IProxy {

	void postInit();

	PlayerEntity getPlayer(Context ctx);

	World getWorld(Context ctx);

	/**
	 * Only works when called on server side or when the passed player is the user.
	 */
	Advancement getAdvancement(PlayerEntity player, ResourceLocation id);

	/**
	 * Only works when called on server side or when the passed player is the user.
	 */
	boolean hasAdvancement(PlayerEntity player, ResourceLocation id);

	void updateGui();

	boolean isOwnerOfIntegratedServer(PlayerEntity player);
	
	boolean isPlayerCurrentClientPlayer(PlayerEntity player);

	void openGui(int id, PlayerEntity player, World world, int... args);

}
