package team.cqr.cqrepoured.client.render.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.client.render.RenderSpriteBase;
import team.cqr.cqrepoured.entity.projectiles.ProjectilePoisonSpell;

public class RenderProjectilePoisonSpell extends RenderSpriteBase<ProjectilePoisonSpell> {

	public RenderProjectilePoisonSpell(Context renderManager) {
		super(renderManager, CQRMain.prefix("textures/entity/poison_spell.png"));
	}

}
