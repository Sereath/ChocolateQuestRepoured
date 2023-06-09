package team.cqr.cqrepoured.client.model.armor;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.world.entity.LivingEntity;

/**
 * @author Silentine
 */
public class ModelArmorHeavy<T extends LivingEntity> extends ModelCustomArmorBase<T> {

	public ModelRenderer lowerHeadArmor;
	public ModelRenderer chestExtension;
	public ModelRenderer pauldronR1;
	public ModelRenderer pauldronR2;
	public ModelRenderer pauldronL1;
	public ModelRenderer pauldronL2;
	public ModelRenderer skirtR;
	public ModelRenderer skirtL;

	public ModelArmorHeavy(float scale) {
		super(RenderType::armorCutoutNoCull, scale, 128, 128);

		this.lowerHeadArmor = new ModelRenderer(this, 0, 64);
		this.lowerHeadArmor.setPos(0.0F, 0.0F, 0.0F);
		this.lowerHeadArmor.addBox(-4.5F, -8.5F, -4.5F, 9, 9, 9, 0.49F);

		this.chestExtension = new ModelRenderer(this, 24, 96);
		this.chestExtension.setPos(0.0F, 0.0F, 0.0F);
		this.chestExtension.addBox(-4.5F, -0.5F, -2.5F, 9, 13, 5, 0.6F);

		this.pauldronR1 = new ModelRenderer(this, 0, 96);
		this.pauldronR1.setPos(0.0F, 0.0F, 0.0F);
		this.pauldronR1.addBox(-4.0F, -3.5F, -2.5F, 5, 5, 5, 0.75F);
		this.setRotateAngle(this.pauldronR1, 0.0F, 0.0F, 0.08726646259971647F);
		this.pauldronR2 = new ModelRenderer(this, 0, 106);
		this.pauldronR2.setPos(0.0F, 0.0F, 0.0F);
		this.pauldronR2.addBox(0.0F, -3.75F, -3.0F, 6, 6, 6, 0.75F);
		this.setRotateAngle(this.pauldronR2, 0.0F, 0.0F, -0.17453292519943295F);

		this.pauldronL1 = new ModelRenderer(this, 0, 96);
		this.pauldronL1.mirror = true;
		this.pauldronL1.setPos(0.0F, 0.0F, 0.0F);
		this.pauldronL1.addBox(-1.0F, -3.5F, -2.5F, 5, 5, 5, 0.75F);
		this.setRotateAngle(this.pauldronL1, 0.0F, 0.0F, -0.08726646259971647F);
		this.pauldronL2 = new ModelRenderer(this, 0, 106);
		this.pauldronL2.mirror = true;
		this.pauldronL2.setPos(0.0F, 0.0F, 0.0F);
		this.pauldronL2.addBox(-6.0F, -3.75F, -3.0F, 6, 6, 6, 0.75F);
		this.setRotateAngle(this.pauldronL2, 0.0F, 0.0F, 0.17453292519943295F);

		this.skirtR = new ModelRenderer(this, 64, 64);
		this.skirtR.setPos(-2.6F, -2.5F, 0.0F);
		this.skirtR.addBox(-0.5F, -0.5F, -3.0F, 6, 6, 6, -0.25F);
		this.setRotateAngle(this.skirtR, 0.0F, 0.0F, 0.2617993877991494F);
		this.skirtL = new ModelRenderer(this, 64, 64);
		this.skirtL.mirror = true;
		this.skirtL.setPos(2.6F, -2.5F, 0.0F);
		this.skirtL.addBox(-5.6F, -0.5F, -3.0F, 6, 6, 6, -0.25F);
		this.setRotateAngle(this.skirtL, 0.0F, 0.0F, -0.2617993877991494F);

		this.head.addChild(this.lowerHeadArmor);
		this.body.addChild(this.chestExtension);
		this.rightArm.addChild(this.pauldronR1);
		this.pauldronR1.addChild(this.pauldronR2);
		this.leftArm.addChild(this.pauldronL1);
		this.pauldronL1.addChild(this.pauldronL2);
		this.rightLeg.addChild(this.skirtR);
		this.leftLeg.addChild(this.skirtL);
	}

}
