package team.cqr.cqrepoured.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import team.cqr.cqrepoured.CQRMain;
import team.cqr.cqrepoured.entity.projectiles.ProjectileHookShotHook;
import team.cqr.cqrepoured.init.CQRItems;
import team.cqr.cqrepoured.init.CQRSounds;
import team.cqr.cqrepoured.util.PropertyFileHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Copyright (c) 15 Feb 2019 Developed by KalgogSmash GitHub: https://github.com/KalgogSmash
 */
public abstract class ItemHookshotBase extends ItemLore {

	//TODO: Replace with tags
	protected List<Block> validLatchBlocks = new ArrayList<>();
	protected List<Tag.INamedTag<Block>> latchGroups = new ArrayList<>();

	public ItemHookshotBase(String hookshotName, Properties props) {
		super(props.stacksTo(1));

		this.loadPropertiesFromFile(hookshotName);

	/*	this.addPropertyOverride(new ResourceLocation("hook_out"), new IItemPropertyGetter() {
			@Override
			@OnlyIn(Dist.CLIENT)
			public float call(ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn) {
				if (entityIn != null && stack.getItem() instanceof ItemHookshotBase) {
					CompoundNBT stackTag = stack.getTag();
					if ((stackTag != null) && (stackTag.getBoolean("isShooting"))) {
						return 1.0f;
					}
				}

				return 0.0f;
			}
		}); */
	}

	private void loadPropertiesFromFile(String hookshotName) {
		Collection<File> files = FileUtils.listFiles(CQRMain.CQ_ITEM_FOLDER, new String[] { "properties", "prop", "cfg" }, true);
		// Find the property file that matches this hookshot name
		Optional<File> configFile = files.stream().filter(f -> FilenameUtils.getBaseName(f.getName()).equalsIgnoreCase(hookshotName)).findFirst();

		if (configFile.isPresent()) {
			java.util.Properties hookshotConfig = new java.util.Properties();
			try (InputStream in = new FileInputStream(configFile.get())) {
				hookshotConfig.load(in);

				String[] latchBlocks = PropertyFileHelper.getStringArrayProperty(hookshotConfig, "latchblocks", new String[0], true);
				for (String blockType : latchBlocks) {
					ResourceLocation rs = new ResourceLocation(blockType.toLowerCase());
					if(ForgeRegistries.BLOCKS.containsKey(rs)) {
						this.validLatchBlocks.add(ForgeRegistries.BLOCKS.getValue(rs));
						continue;
					} else {
						IOptionalNamedTag<Block> groupMatch = BlockTags.createOptional(rs);
						if(!groupMatch.isDefaulted()) {
							this.latchGroups.add(groupMatch);
						}
						continue;
					}
					//CQRMain.logger.error("{}: Invalid latch block: {}", configFile.get().getName(), blockType);
				}

			} catch (IOException e) {
				CQRMain.logger.error("{}: Failed to load file!", configFile.get().getName(), e);
			}
		}
	}

	public boolean canLatchToBlock(Block block) {
		for (Tag.INamedTag<Block> bg : this.latchGroups) {
			if (bg.contains(block)) {
				return true;
			}
		}
		return this.validLatchBlocks.contains(block);
	}

	public abstract double getHookRange();

	public abstract ProjectileHookShotHook getNewHookEntity(Level worldIn, LivingEntity shooter, ItemStack stack);

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		this.shoot(stack, worldIn, playerIn);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}

	public void shoot(ItemStack stack, Level worldIn, Player player) {

		if (!worldIn.isClientSide) {
			ProjectileHookShotHook hookEntity = this.getNewHookEntity(worldIn, player, stack);
			hookEntity.shootHook(player, this.getHookRange(), 1.8D);
			worldIn.addFreshEntity(hookEntity);
			player.getCooldowns().addCooldown(this, 100);
			stack.hurtAndBreak(1, player, (p_220045_0_) -> {
		         p_220045_0_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
		      });
			worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), CQRSounds.GUN_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
		}
	}

	public ProjectileHookShotHook entityAIshoot(Level worldIn, LivingEntity shooter, Entity target, InteractionHand handIn) {
		if (!worldIn.isClientSide) {
			ProjectileHookShotHook hookEntity = this.getNewHookEntity(worldIn, shooter, shooter.getUseItem());
			Vec3 v = target.position().subtract(shooter.position());
			hookEntity.shootHook(shooter, v.x, v.y, v.z, this.getHookRange(), 1.8D);
			worldIn.addFreshEntity(hookEntity);
			return hookEntity;
		}
		return null;
	}

	public SoundEvent getShootSound() {
		return CQRSounds.GUN_SHOOT;
	}

	public double getRange() {
		return 16.0D;
	}

	public int getCooldown() {
		return 300;
	}

	public int getChargeTicks() {
		return 0;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	//#TODO add call in client setup
	public static void registerItemModelProperty()
	{
		ItemModelsProperties.register(CQRItems.HOOKSHOT.get(), new ResourceLocation("hook_out"), (stack, world, entity) -> {
			if (entity != null && stack.getItem() instanceof ItemHookshotBase) {
				CompoundTag stackTag = stack.getTag();
				if ((stackTag != null) && (stackTag.getBoolean("isShooting"))) {
					return 1.0F;
				}
			}
			return 0.0F;
		});
	}
}
