package team.cqr.cqrepoured.proxy;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import team.cqr.cqrepoured.client.gui.IUpdatableGui;
import team.cqr.cqrepoured.client.render.entity.layer.LayerCrownRenderer;
import team.cqr.cqrepoured.client.render.entity.layer.LayerElectrocute;

public class ClientProxy implements IProxy {

	static final String KEY_CATEGORY_MAIN = "Chocolate Quest Repoured";

	//public static KeyBinding keybindReputationGUI = new KeyBinding("Reputation GUI", Keyboard.KEY_F4, KEY_CATEGORY_MAIN);

	@SuppressWarnings({ "unchecked", "resource", "rawtypes" })
	@Override
	public void postInit() {
		// Add electrocute layer to all entities
		for (EntityRenderer<? extends Entity> renderer : Minecraft.getInstance().getEntityRenderDispatcher().renderers.values()) {
			try {
                EntityRenderer<Entity> render = (EntityRenderer<Entity>) renderer;
				if (render instanceof LivingEntityRenderer) {
					((LivingEntityRenderer<?, ?>) render).addLayer(new LayerElectrocute((LivingEntityRenderer<?,?>) render));
					((LivingEntityRenderer<?, ?>) render).addLayer(new LayerCrownRenderer((LivingEntityRenderer<?,?>) render));
				}
			} catch (ClassCastException ccex) {
				// Ignore
			}
		}
		// Since for whatever reason the player renderer is not in the entityRenderMap we need to add it manually...
		Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap().values().forEach(t -> {
			if (t instanceof PlayerRenderer pr) {
				pr.addLayer(new LayerElectrocute<>(pr));
				pr.addLayer(new LayerCrownRenderer<>pr));
			}
			}
		);
	}

	@Override
	public Player getPlayer(Context context) {
		return DistExecutor.safeRunForDist(() -> () -> Minecraft.getInstance().player, () -> () -> context.getSender());
	}

	@Override
	public Level getWorld(Context context) {
		return DistExecutor.safeRunForDist(() -> () -> Minecraft.getInstance().level, () -> () -> context.getSender().level());
	}

	@Override
	public Advancement getAdvancement(Player player, ResourceLocation id) {
		if (player instanceof LocalPlayer) {
			ClientAdvancements manager = ((LocalPlayer) player).connection.getAdvancements();
			return manager.getAdvancements().get(id);
		}
		return null;
	}

	@Override
	public boolean hasAdvancement(Player player, ResourceLocation id) {
		if (player instanceof LocalPlayer) {
			ClientAdvancements manager = ((LocalPlayer) player).connection.getAdvancements();
			Advancement advancement = manager.getAdvancements().get(id);
			if (advancement != null) {
				AdvancementProgress prog = manager.progress.get(advancement);
				return prog != null && prog.isDone();
			}
		}
		return false;
	}

	@SuppressWarnings("resource")
	@Override
	public void updateGui() {
		Screen gui = Minecraft.getInstance().screen;
		if (gui instanceof IUpdatableGui) {
			((IUpdatableGui) gui).update();
		}
	}

	@Override
	public boolean isOwnerOfIntegratedServer(Player player) {
		IntegratedServer integratedServer = Minecraft.getInstance().getSingleplayerServer();
		return integratedServer != null && player.getName().equals(integratedServer.getSingleplayerName()) && integratedServer.isSingleplayerOwner(player.getGameProfile());
	}

	@Override
	public void openGui(int id, Player player, Level world, int... args) {
		/*Minecraft mc = Minecraft.getInstance();

		if (id == GuiHandler.ADD_PATH_NODE_GUI_ID) {
			mc.setScreen(new GuiAddPathNode(Hand.values()[args[0]], args[1], new BlockPos(args[2], args[3], args[4])));
		}*/
		//TODO: Re-implement
	}

	@SuppressWarnings("resource")
	@Override
	public boolean isPlayerCurrentClientPlayer(Player player) {
		if(player != null) {
			return ((Player)Minecraft.getInstance().player).equals(player);
		}
		return false;
	}

}
