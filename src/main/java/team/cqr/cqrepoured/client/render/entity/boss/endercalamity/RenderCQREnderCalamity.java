package team.cqr.cqrepoured.client.render.entity.boss.endercalamity;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.client.init.CQRRenderTypes;
import team.cqr.cqrepoured.client.model.geo.entity.boss.ModelEnderCalamityGeo;
import team.cqr.cqrepoured.client.render.entity.RenderCQREntityGeo;
import team.cqr.cqrepoured.client.render.entity.StandardBipedBones;
import team.cqr.cqrepoured.client.render.entity.layer.geo.LayerGlowingAreasGeo;
import team.cqr.cqrepoured.client.util.SphereRenderer;
import team.cqr.cqrepoured.client.util.SphereRenderer.Triangle;
import team.cqr.cqrepoured.client.util.SphereRenderer.Vertex;
import team.cqr.cqrepoured.entity.boss.endercalamity.EntityCQREnderCalamity;
import team.cqr.cqrepoured.entity.boss.endercalamity.EntityCQREnderCalamity.E_CALAMITY_HAND;
import team.cqr.cqrepoured.util.ArrayUtil;

public class RenderCQREnderCalamity extends RenderCQREntityGeo<EntityCQREnderCalamity> {

	private static final VertexBuffer SPHERE_VBO = new VertexBuffer(DefaultVertexFormats.POSITION);
	static {
		BufferBuilder buffer = Tessellator.getInstance().getBuilder();
		buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);

		AtomicInteger index = new AtomicInteger();
		SphereRenderer.getIcoSphere().flatMap(SphereRenderer.splitter(3, true)).forEach((Triangle triangle) -> {
			int ind = index.getAndIncrement();
			Vertex[] outer = triangle.vertices().toArray(Vertex[]::new);
			Vertex[] outerDir = ArrayUtil.createArray(outer.length, Vertex[]::new, (int i) -> ArrayUtil.next(outer, i).subtract(outer[i]).normalize());
			Vertex[] inner = ArrayUtil.createArray(outer.length, Vertex[]::new, (int i) -> {
				Vertex v = outerDir[i].subtract(ArrayUtil.prev(outerDir, i)).normalize();
				double d = outerDir[i].dot(v);
				v = v.scale(0.01D / Math.sqrt(1.0D - d * d));
				outer[i] = outer[i].subtract(v).normalize();
				return outer[i].add(v.scale(2.0D)).normalize();
			});

			if (ind % 16 < 12) {
				if (ind % 4 == 0) {
					buffer.vertex(inner[0].x, inner[0].y, inner[0].z).endVertex();
					buffer.vertex(outer[0].x, outer[0].y, outer[0].z).endVertex();
					buffer.vertex(outer[1].x, outer[1].y, outer[1].z).endVertex();
				}
				if (ind % 4 == 2) {
					buffer.vertex(inner[0].x, inner[0].y, inner[0].z).endVertex();
					buffer.vertex(outer[1].x, outer[1].y, outer[1].z).endVertex();
					buffer.vertex(inner[1].x, inner[1].y, inner[1].z).endVertex();
				}
				if (ind % 4 == 3) {
					buffer.vertex(outer[0].x, outer[0].y, outer[0].z).endVertex();
					buffer.vertex(outer[1].x, outer[1].y, outer[1].z).endVertex();
					buffer.vertex(inner[1].x, inner[1].y, inner[1].z).endVertex();
				}
			}
		});

		buffer.end();
		SPHERE_VBO.upload(buffer);
	}
	private static final Vector3f SPHERE_ROT_AXIS = new Vector3f(1.0F, 1.0F, 0.0F);
	static {
		SPHERE_ROT_AXIS.normalize();
	}

	private static final ResourceLocation TEXTURE = new ResourceLocation(CQRMain.MODID, "textures/entity/boss/ender_calamity.png");
	private static final ResourceLocation MODEL_RESLOC = new ResourceLocation(CQRMain.MODID, "geo/entity/boss/ender_calamity.geo.json");

	public RenderCQREnderCalamity(EntityRendererManager renderManager) {
		super(renderManager, new ModelEnderCalamityGeo(MODEL_RESLOC, TEXTURE, "boss/ender_calamity"));

		this.addLayer(new LayerGlowingAreasGeo<EntityCQREnderCalamity>(this, this.TEXTURE_GETTER, this.MODEL_ID_GETTER));
	}

	@Override
	public void render(EntityCQREnderCalamity entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);

		if (entity.isShieldActive()) {
			float width = this.getWidthScale(entity);
			float height = this.getHeightScale(entity);

			stack.pushPose();
			stack.translate(0.0D, entity.getBbHeight() * 0.5D, 0.0D);
			stack.scale(width, height, width);
			stack.scale(1.25F, 1.25F, 1.25F);
			stack.mulPose(SPHERE_ROT_AXIS.rotationDegrees((entity.tickCount + partialTicks) * 4.0F));

			float f = 0.7F + 0.15F * MathHelper.sin((entity.tickCount + partialTicks) * 0.1F);
			RenderSystem.color4f(0.6F, 0.2F, 0.7F, f);
			SphereRenderer.renderSphere(stack, CQRRenderTypes.sphere(), SPHERE_VBO, GL11.GL_TRIANGLES, null, true, false);
			RenderSystem.color4f(0.6F, 0.2F, 0.7F, f * 0.35F);
			SphereRenderer.renderSphere(stack, CQRRenderTypes.sphere(), SPHERE_VBO, GL11.GL_TRIANGLES, null, false, true);

			stack.popPose();
		}
	}

	// we do not hold items, so we can ignore this
	@Override
	protected ItemStack getHeldItemForBone(String boneName, EntityCQREnderCalamity currentEntity) {
		return null;
	}

	@Nullable
	@Override
	protected BlockState getHeldBlockForBone(String boneName, EntityCQREnderCalamity currentEntity) {
		Optional<BlockState> optional = currentEntity.getBlockFromHand(E_CALAMITY_HAND.getFromBoneName(boneName));
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	protected void preRenderBlock(MatrixStack stack, BlockState block, String boneName, EntityCQREnderCalamity currentEntity) {
		// Unused
	}

	@Override
	protected void postRenderBlock(MatrixStack stack, BlockState block, String boneName, EntityCQREnderCalamity currentEntity) {
		// Unused
	}

	@Override
	protected ResourceLocation getTextureForBone(String boneName, EntityCQREnderCalamity currentEntity) {
		if(boneName.equalsIgnoreCase(StandardBipedBones.CAPE_BONE) && currentEntity.hasCape()) {
			return currentEntity.getResourceLocationOfCape();
		}
		return null;
	}

	@Override
	protected float getDeathMaxRotation(EntityCQREnderCalamity entityLivingBaseIn) {
		return 0;
	}

	@Override
	protected TransformType getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
		return TransformType.NONE;
	}

	@Override
	protected void preRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, EntityCQREnderCalamity currentEntity, IBone bone) {
		
	}

	@Override
	protected void postRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, EntityCQREnderCalamity currentEntity, IBone bone) {
		
	}
	
	@Override
	protected boolean isArmorBone(GeoBone bone) {
		return false;
	}
	
	@Override
	public RenderType getRenderType(EntityCQREnderCalamity animatable, float partialTicks, MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
	}

}
