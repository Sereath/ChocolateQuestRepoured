package team.cqr.cqrepoured.customtextures;

import javax.annotation.Nonnull;

import net.minecraft.resources.ResourceLocation;

public interface IHasTextureOverride {

	public boolean hasTextureOverride();

	public ResourceLocation getTextureOverride();

	public void setCustomTexture(@Nonnull ResourceLocation texture);

}
