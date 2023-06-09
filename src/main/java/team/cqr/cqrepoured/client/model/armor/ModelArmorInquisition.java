package team.cqr.cqrepoured.client.model.armor;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.world.entity.LivingEntity;

/**
 * @author Silentine
 */
public class ModelArmorInquisition<T extends LivingEntity> extends ModelCustomArmorBase<T> {

	public ModelRenderer helmR;
	public ModelRenderer helmL;
	public ModelRenderer chest1;
	public ModelRenderer pauldronR1;
	public ModelRenderer pauldronRWing1;
	public ModelRenderer pauldronRWing2;
	public ModelRenderer pauldronL1;
	public ModelRenderer pauldronLWing1;
	public ModelRenderer pauldronLWing2;
	public ModelRenderer gauntletR;
	public ModelRenderer gauntletL;

	public ModelArmorInquisition(float scale) {
		super(RenderType::armorCutoutNoCull, scale, 128, 128);

		this.helmR = new ModelRenderer(this, 0, 64);
		this.helmR.setPos(-5.0F, -1.0F, -5.0F);
		this.helmR.addBox(0.0F, -1.5F, -2.0F, 1, 4, 4, 0.0F);
		this.setRotateAngle(this.helmR, 0.0F, -0.7853981633974483F, 0.0F);
		this.helmL = new ModelRenderer(this, 0, 64);
		this.helmL.mirror = true;
		this.helmL.setPos(5.0F, -1.0F, -5.0F);
		this.helmL.addBox(-1.0F, -1.5F, -2.0F, 1, 4, 4, 0.0F);
		this.setRotateAngle(this.helmL, 0.0F, 0.7853981633974483F, 0.0F);

		this.chest1 = new ModelRenderer(this, 64, 64);
		this.chest1.setPos(0.0F, 1.0F, -3.5F);
		this.chest1.addBox(-4.5F, 0.0F, 0.0F, 9, 4, 4, 0.0F);
		this.setRotateAngle(this.chest1, -0.08726646259971647F, 0.0F, 0.0F);

		this.pauldronR1 = new ModelRenderer(this, 64, 0);
		this.pauldronR1.setPos(0.0F, 0.0F, 0.0F);
		this.pauldronR1.addBox(-3.75F, -2.5F, -2.5F, 5, 5, 5, 0.75F);
		this.setRotateAngle(this.pauldronR1, 0.0F, 0.0F, 0.08726646259971647F);
		this.pauldronRWing1 = new ModelRenderer(this, 64, 18);
		this.pauldronRWing1.setPos(0.0F, 0.0F, 0.0F);
		this.pauldronRWing1.addBox(-4.25F, -5.0F, -3.6F, 5, 5, 2, 0.75F);
		this.setRotateAngle(this.pauldronRWing1, 0.0F, 0.2617993877991494F, 0.08726646259971647F);
		this.pauldronRWing2 = new ModelRenderer(this, 64, 25);
		this.pauldronRWing2.setPos(0.0F, 0.0F, 0.0F);
		this.pauldronRWing2.addBox(-4.25F, -5.0F, 1.6F, 5, 5, 2, 0.75F);
		this.setRotateAngle(this.pauldronRWing2, 0.0F, -0.2617993877991494F, 0.08726646259971647F);
		this.gauntletR = new ModelRenderer(this, 64, 10);
		this.gauntletR.setPos(0.0F, 0.0F, 0.0F);
		this.gauntletR.addBox(-3.0F, 6.0F, -2.0F, 4, 4, 4, 0.1F);

		this.pauldronL1 = new ModelRenderer(this, 64, 0);
		this.pauldronL1.mirror = true;
		this.pauldronL1.setPos(0.0F, 0.0F, 0.0F);
		this.pauldronL1.addBox(-1.25F, -2.5F, -2.5F, 5, 5, 5, 0.75F);
		this.setRotateAngle(this.pauldronL1, 0.0F, 0.0F, -0.08726646259971647F);
		this.pauldronLWing1 = new ModelRenderer(this, 64, 18);
		this.pauldronLWing1.mirror = true;
		this.pauldronLWing1.setPos(0.0F, 0.0F, 0.0F);
		this.pauldronLWing1.addBox(-0.75F, -5.0F, -3.6F, 5, 5, 2, 0.75F);
		this.setRotateAngle(this.pauldronLWing1, 0.0F, -0.2617993877991494F, -0.08726646259971647F);
		this.pauldronLWing2 = new ModelRenderer(this, 64, 25);
		this.pauldronLWing2.mirror = true;
		this.pauldronLWing2.setPos(0.0F, 0.0F, 0.0F);
		this.pauldronLWing2.addBox(-0.75F, -5.0F, 1.6F, 5, 5, 2, 0.75F);
		this.setRotateAngle(this.pauldronLWing2, 0.0F, 0.2617993877991494F, -0.08726646259971647F);
		this.gauntletL = new ModelRenderer(this, 64, 10);
		this.gauntletL.mirror = true;
		this.gauntletL.setPos(0.0F, 0.0F, 0.0F);
		this.gauntletL.addBox(-1.0F, 6.0F, -2.0F, 4, 4, 4, 0.1F);

		this.head.addChild(this.helmR);
		this.head.addChild(this.helmL);
		this.body.addChild(this.chest1);
		this.rightArm.addChild(this.pauldronR1);
		this.rightArm.addChild(this.pauldronRWing1);
		this.rightArm.addChild(this.pauldronRWing2);
		this.rightArm.addChild(this.gauntletR);
		this.leftArm.addChild(this.pauldronL1);
		this.leftArm.addChild(this.pauldronLWing1);
		this.leftArm.addChild(this.pauldronLWing2);
		this.leftArm.addChild(this.gauntletL);
	}

}
