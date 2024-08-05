package team.cqr.cqrepoured.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.client.model.entity.ModelCQRBiped;
import team.cqr.cqrepoured.client.occlusion.EntityOcclusionTester;
import team.cqr.cqrepoured.client.render.MagicBellRenderer;
import team.cqr.cqrepoured.client.render.entity.layer.equipment.LayerCQREntityArmor;
import team.cqr.cqrepoured.client.render.entity.layer.equipment.LayerCQREntityCape;
import team.cqr.cqrepoured.client.render.entity.layer.equipment.LayerCQREntityPotion;
import team.cqr.cqrepoured.client.render.entity.layer.equipment.LayerCQRHeldItem;
import team.cqr.cqrepoured.client.render.entity.layer.equipment.LayerShoulderEntity;
import team.cqr.cqrepoured.client.render.entity.layer.special.LayerCQRLeaderFeather;
import team.cqr.cqrepoured.client.render.entity.layer.special.LayerCQRSpeechbubble;
import team.cqr.cqrepoured.client.render.texture.InvisibilityTexture;
import team.cqr.cqrepoured.customtextures.IHasTextureOverride;
import team.cqr.cqrepoured.entity.ITextureVariants;
import team.cqr.cqrepoured.entity.bases.AbstractEntityCQR;
import team.cqr.cqrepoured.item.ItemHookshotBase;
import team.cqr.cqrepoured.item.gun.ItemMusket;
import team.cqr.cqrepoured.item.gun.ItemMusketKnife;
import team.cqr.cqrepoured.item.gun.ItemRevolver;

public class RenderCQREntity<T extends AbstractEntityCQR> extends RenderLiving<T> {

	public ResourceLocation texture;
	public double widthScale;
	public double heightScale;

	private final String entityName;

	public RenderCQREntity(RenderManager rendermanagerIn, String textureName) {
		this(rendermanagerIn, textureName, 1.0D, 1.0D, false);
	}

	public RenderCQREntity(RenderManager rendermanagerIn, String textureName, boolean hasExtraLayer) {
		this(rendermanagerIn, textureName, 1.0D, 1.0D, hasExtraLayer);
	}

	public RenderCQREntity(RenderManager rendermanagerIn, String textureName, double widthScale, double heightScale) {
		this(rendermanagerIn, textureName, widthScale, heightScale, false);
	}

	public RenderCQREntity(RenderManager rendermanagerIn, String textureName, double widthScale, double heightScale, boolean hasExtraLayer) {
		this(rendermanagerIn, new ModelCQRBiped(64, 64, hasExtraLayer), 0.5F, textureName, widthScale, heightScale);
	}

	public RenderCQREntity(RenderManager rendermanagerIn, ModelBase model, float shadowSize, String textureName, double widthScale, double heightScale) {
		super(rendermanagerIn, model, shadowSize);
		this.entityName = textureName;
		this.texture = new ResourceLocation(CQRMain.MODID, "textures/entity/" + this.entityName + ".png");
		this.widthScale = widthScale;
		this.heightScale = heightScale;
		this.addLayer(new LayerCQREntityArmor(this));
		this.addLayer(new LayerCQRHeldItem(this));
		// this.addLayer(new LayerRevolver(this));
		this.addLayer(new LayerArrow(this));
		this.addLayer(new LayerElytra(this));
		this.addLayer(new LayerCQREntityCape(this));
		this.addLayer(new LayerCQREntityPotion(this));

		this.addLayer(new LayerCQRSpeechbubble(this));

		if (model instanceof ModelBiped) {
			this.addLayer(new LayerShoulderEntity(this));
			if (model instanceof ModelCQRBiped) {
				this.addLayer(new LayerCQRLeaderFeather(this, ((ModelCQRBiped) model).bipedHead));
				this.addLayer(new LayerCustomHead(((ModelCQRBiped) model).bipedHead));
			}
		}
	}

	protected double getWidthScale(T entity) {
		return this.widthScale * entity.getSizeVariation();
	}

	protected double getHeightScale(T entity) {
		return this.heightScale * entity.getSizeVariation();
	}

	@Override
	protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {
		super.preRenderCallback(entitylivingbaseIn, partialTickTime);

		double width = this.getWidthScale(entitylivingbaseIn);
		double height = this.getHeightScale(entitylivingbaseIn);
		GL11.glScaled(width, height, width);

		if (this.mainModel.isRiding) {
			GL11.glTranslatef(0, 0.6F, 0);
		}
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (!EntityOcclusionTester.isNotOccluded(entity)) {
			return;
		}

		if (this.mainModel instanceof ModelBiped) {
			GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
			ModelBiped model = (ModelBiped) this.mainModel;

			ItemStack itemMainHand = entity.getHeldItemMainhand();
			ItemStack itemOffHand = entity.getHeldItemOffhand();

			ModelBiped.ArmPose armPoseMain = ModelBiped.ArmPose.EMPTY;
			ModelBiped.ArmPose armPoseOff = ModelBiped.ArmPose.EMPTY;

			boolean dontRenderOffItem = false;
			boolean dontRenderMainItem = false;

			boolean flagMain = false;
			boolean flagOff = false;

			// Main arm
			if (!itemMainHand.isEmpty()) {
				if (itemMainHand.getItem() instanceof ItemMusket || itemMainHand.getItem() instanceof ItemMusketKnife) {
					armPoseMain = ModelBiped.ArmPose.BOW_AND_ARROW;
					dontRenderOffItem = true;
				} else if (itemMainHand.getItem() instanceof ItemRevolver || itemMainHand.getItem() instanceof ItemHookshotBase) {
					flagMain = true;
				} else if (entity.getItemInUseCount() > 0) {
					EnumAction action = itemMainHand.getItemUseAction();
					switch (action) {
					case DRINK:
					case EAT:
						armPoseMain = ModelBiped.ArmPose.ITEM;
						break;
					case BOW:
						armPoseMain = ModelBiped.ArmPose.BOW_AND_ARROW;
						dontRenderOffItem = true;
						break;
					case BLOCK:
						armPoseMain = ModelBiped.ArmPose.BLOCK;
						break;
					default:
						// armPoseMain = ModelBiped.ArmPose.EMPTY;
						break;
					}
				}
			}
			// Off arm
			if (!itemOffHand.isEmpty()) {
				// if(itemOffHand.getItem() instanceof ItemShield) {

				if (itemOffHand.getItem() instanceof ItemMusket || itemOffHand.getItem() instanceof ItemMusketKnife) {
					armPoseOff = ModelBiped.ArmPose.BOW_AND_ARROW;
					dontRenderMainItem = true;
				} else if (itemMainHand.getItem() instanceof ItemRevolver || itemOffHand.getItem() instanceof ItemHookshotBase) {
					flagOff = true;
				} else if (entity.getItemInUseCount() > 0) {
					EnumAction action = itemOffHand.getItemUseAction();
					switch (action) {
					case DRINK:
					case EAT:
						armPoseOff = ModelBiped.ArmPose.ITEM;
						break;
					case BOW:
						armPoseOff = ModelBiped.ArmPose.BOW_AND_ARROW;
						dontRenderMainItem = true;
						break;
					case BLOCK:
						armPoseOff = ModelBiped.ArmPose.BLOCK;
						break;
					default:
						break;

					}
				}

			}

			if (entity.getPrimaryHand() == EnumHandSide.LEFT) {
				ArmPose tmp = armPoseMain;
				armPoseMain = armPoseOff;
				armPoseOff = tmp;
				boolean tmp2 = dontRenderMainItem;
				dontRenderMainItem = dontRenderOffItem;
				dontRenderOffItem = tmp2;
			}
			if (!flagMain) {
				model.rightArmPose = armPoseMain;
			}
			if (!flagOff) {
				model.leftArmPose = armPoseOff;
			}
			if (dontRenderMainItem) {
				model.rightArmPose = ArmPose.EMPTY;
			}
			if (dontRenderOffItem) {
				model.leftArmPose = ArmPose.EMPTY;
			}
		}

		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		if (this.mainModel instanceof ModelBiped) {
			GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
		}
	}

	@Override
	protected void renderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		boolean flag = entitylivingbaseIn.getInvisibility() > 0.0F;
		if (flag) {
			GlStateManager.alphaFunc(GL11.GL_GREATER, entitylivingbaseIn.getInvisibility());
			this.bindTexture(InvisibilityTexture.get(this.getEntityTexture(entitylivingbaseIn)));
			this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			GlStateManager.depthFunc(GL11.GL_EQUAL);
		}
		super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
		if (flag) {
			GlStateManager.depthFunc(GL11.GL_LEQUAL);
		}
	}

	@Override
	protected void renderLivingAt(T entityLivingBaseIn, double x, double y, double z) {
		if (this.mainModel instanceof ModelBiped) {
			((ModelBiped) this.mainModel).isRiding = entityLivingBaseIn.isRiding() || entityLivingBaseIn.isSitting();
			((ModelBiped) this.mainModel).isSneak = entityLivingBaseIn.isSneaking();
		}

		super.renderLivingAt(entityLivingBaseIn, x, y, z);
	}

	protected ResourceLocation[] textureVariantCache = null;

	@Override
	protected ResourceLocation getEntityTexture(T entity) {
		if (entity instanceof IHasTextureOverride) {
			// Custom texture start
			if (((IHasTextureOverride) entity).hasTextureOverride()) {
				return ((IHasTextureOverride) entity).getTextureOverride();
			}
		}
		// Custom texture end
		if (entity instanceof ITextureVariants) {
			if (((ITextureVariants) entity).getTextureCount() > 1) {
				if (this.textureVariantCache == null) {
					this.textureVariantCache = new ResourceLocation[((ITextureVariants) entity).getTextureCount()];
				}
				final int index = ((ITextureVariants) entity).getTextureIndex();
				if (this.textureVariantCache[index] == null) {
					this.textureVariantCache[index] = new ResourceLocation(CQRMain.MODID, "textures/entity/" + this.entityName + "_" + index + ".png");
				}
				return this.textureVariantCache[index];
			}
		}
		return this.texture;
	}

	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
		this.shadowSize *= ((AbstractEntityCQR) entityIn).getSizeVariation();
		this.shadowOpaque = MathHelper.clamp(1.0F - ((AbstractEntityCQR) entityIn).getInvisibility(), 0.0F, 1.0F);
		super.doRenderShadowAndFire(entityIn, x, y, z, yaw, partialTicks);
		this.shadowSize /= ((AbstractEntityCQR) entityIn).getSizeVariation();
	}

	@Override
	protected int getTeamColor(T entityIn) {
		if (MagicBellRenderer.outlineColor != -1) {
			return MagicBellRenderer.outlineColor;
		}
		return super.getTeamColor(entityIn);
	}

	public void setupHeadOffsets(ModelRenderer modelRenderer, EntityEquipmentSlot slot) {

	}

	public void setupBodyOffsets(ModelRenderer modelRenderer, EntityEquipmentSlot slot) {

	}

	public void setupRightArmOffsets(ModelRenderer modelRenderer, EntityEquipmentSlot slot) {

	}

	public void setupLeftArmOffsets(ModelRenderer modelRenderer, EntityEquipmentSlot slot) {

	}

	public void setupRightLegOffsets(ModelRenderer modelRenderer, EntityEquipmentSlot slot) {

	}

	public void setupLeftLegOffsets(ModelRenderer modelRenderer, EntityEquipmentSlot slot) {

	}

	public void setupHeadwearOffsets(ModelRenderer modelRenderer, EntityEquipmentSlot slot) {

	}

	public void setupPotionOffsets(ModelRenderer modelRenderer) {

	}

	protected void applyTranslations(ModelRenderer modelRenderer) {
		GlStateManager.translate(modelRenderer.offsetX, modelRenderer.offsetY, modelRenderer.offsetZ);
		GlStateManager.translate(modelRenderer.rotationPointX * 0.0625F, modelRenderer.rotationPointY * 0.0625F, modelRenderer.rotationPointZ * 0.0625F);
	}

	protected void resetTranslations(ModelRenderer modelRenderer) {
		GlStateManager.translate(-modelRenderer.rotationPointX * 0.0625F, -modelRenderer.rotationPointY * 0.0625F, -modelRenderer.rotationPointZ * 0.0625F);
		GlStateManager.translate(-modelRenderer.offsetX, -modelRenderer.offsetY, -modelRenderer.offsetZ);
	}

	protected void applyRotations(ModelRenderer modelRenderer) {
		GlStateManager.rotate((float) Math.toDegrees(modelRenderer.rotateAngleZ), 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate((float) Math.toDegrees(modelRenderer.rotateAngleY), 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float) Math.toDegrees(modelRenderer.rotateAngleX), 1.0F, 0.0F, 0.0F);
	}

	protected void resetRotations(ModelRenderer modelRenderer) {
		GlStateManager.rotate((float) Math.toDegrees(modelRenderer.rotateAngleX), -1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((float) Math.toDegrees(modelRenderer.rotateAngleY), 0.0F, -1.0F, 0.0F);
		GlStateManager.rotate((float) Math.toDegrees(modelRenderer.rotateAngleZ), 0.0F, 0.0F, -1.0F);
	}

}
