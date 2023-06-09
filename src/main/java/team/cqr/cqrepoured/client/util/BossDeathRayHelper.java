package team.cqr.cqrepoured.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Mth;
import com.mojang.math.Matrix4f;

import java.util.Random;

public class BossDeathRayHelper {

	private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);
	private final int red;
	private final int green;
	private final int blue;
	private final float raySize;
	private final int maxRays;

	public BossDeathRayHelper(int red, int green, int blue, float raySize) {
		this(red, green, blue, raySize, 60);
	}

	public BossDeathRayHelper(int red, int green, int blue, float raySize, int maxRays) {
		this.red = red;
		this.maxRays = maxRays;
		this.green = green;
		this.blue = blue;
		this.raySize = raySize;
	}

	/**
	 * Copied from {@link EnderDragonRenderer#render(EnderDragonEntity, float, float, PoseStack, MultiBufferSource, int)}
	 */
	public void renderRays(PoseStack matrixStack, VertexConsumer vertexBuilder, int ticks, float partialTicks) {
		float f = (ticks + partialTicks) / 200.0F;
		float f1 = Math.min(f > 0.8F ? (f - 0.8F) / 0.2F : 0.0F, 1.0F);
		Random random = new Random(432L);
		matrixStack.pushPose();
		matrixStack.translate(0.0D, -1.0D, -2.0D);

		int rayCount = Math.min(Mth.ceil((f + f * f) / 2.0F * 60.0F), this.maxRays);
		for (int i = 0; i < rayCount; ++i) {
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0F));
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0F));
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0F));
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0F));
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0F));
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0F + f * 90.0F));
			float y = random.nextFloat() * this.raySize + (this.raySize / 4.0F) + f1 * (this.raySize / 2.0F);
			float xz = random.nextFloat() * (this.raySize / 10.0F) + 1.0F + f1 * (this.raySize / 10.0F);
			Matrix4f matrix = matrixStack.last().pose();
			int alpha = (int) (255.0F * (1.0F - f1));
			vertex01(vertexBuilder, matrix, alpha);
			vertex2(vertexBuilder, matrix, y, xz);
			vertex3(vertexBuilder, matrix, y, xz);
			vertex01(vertexBuilder, matrix, alpha);
			vertex3(vertexBuilder, matrix, y, xz);
			vertex4(vertexBuilder, matrix, y, xz);
			vertex01(vertexBuilder, matrix, alpha);
			vertex4(vertexBuilder, matrix, y, xz);
			vertex2(vertexBuilder, matrix, y, xz);
		}

		matrixStack.popPose();
	}

	private void vertex01(VertexConsumer vertexBuilder, Matrix4f matrix, int alpha) {
		vertexBuilder.vertex(matrix, 0.0F, 0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
		vertexBuilder.vertex(matrix, 0.0F, 0.0F, 0.0F).color(255, 255, 255, alpha).endVertex();
	}

	private void vertex2(VertexConsumer vertexBuilder, Matrix4f matrix, float y, float xz) {
		vertexBuilder.vertex(matrix, -HALF_SQRT_3 * xz, y, -0.5F * xz).color(red, green, blue, 0).endVertex();
	}

	private void vertex3(VertexConsumer vertexBuilder, Matrix4f matrix, float y, float xz) {
		vertexBuilder.vertex(matrix, HALF_SQRT_3 * xz, y, -0.5F * xz).color(red, green, blue, 0).endVertex();
	}

	private void vertex4(VertexConsumer vertexBuilder, Matrix4f matrix, float y, float xz) {
		vertexBuilder.vertex(matrix, 0.0F, y, 1.0F * xz).color(red, green, blue, 0).endVertex();
	}

}
