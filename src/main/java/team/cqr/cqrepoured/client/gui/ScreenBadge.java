package team.cqr.cqrepoured.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.cqr.cqrepoured.inventory.ContainerBadge;

@OnlyIn(Dist.CLIENT)
public class ScreenBadge extends ContainerScreen<ContainerBadge> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/dispenser.png");

	public ScreenBadge(ContainerBadge container, PlayerInventory playerInv, ITextComponent title) {
		super(container, playerInv, title);
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
		this.renderBackground(pMatrixStack);
		super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		this.renderTooltip(pMatrixStack, pMouseX, pMouseY);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack pMatrixStack, float pPartialTicks, int pX, int pY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(TEXTURE);
		this.blit(pMatrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
	}

}
