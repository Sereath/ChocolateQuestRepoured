package team.cqr.cqrepoured.client.render.entity.layer.geo;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.entity.MobEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.layer.AbstractLayerGeo;
import software.bernie.geckolib3.renderers.texture.AutoGlowingTexture;
import team.cqr.cqrepoured.client.init.CQRRenderTypes;

public class LayerGlowingAreasGeo<T extends MobEntity & IAnimatable> extends AbstractLayerGeo<T> {

	public LayerGlowingAreasGeo(GeoEntityRenderer<T> renderer, Function<T, ResourceLocation> funcGetCurrentTexture, Function<T, ResourceLocation> funcGetCurrentModel) {
		super(renderer, funcGetCurrentTexture, funcGetCurrentModel);
	}

	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		//EmissiveUtil.preEmissiveTextureRendering();

		//Not needed anymore?
		//this.geoRendererInstance.bindTexture(AutoGlowingTexture.get(this.funcGetCurrentTexture.apply(entityLivingBaseIn)));

		this.reRenderCurrentModelInRenderer(entityLivingBaseIn, partialTicks, matrixStackIn, bufferIn, packedLightIn, CQRRenderTypes.emissive(AutoGlowingTexture.get(this.funcGetCurrentTexture.apply(entityLivingBaseIn))));

		//EmissiveUtil.postEmissiveTextureRendering();
	}

}
