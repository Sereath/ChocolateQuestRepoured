package team.cqr.cqrepoured.entity.trade.rules.input;

import com.mojang.serialization.Codec;

import net.minecraft.world.item.ItemStack;
import team.cqr.cqrepoured.init.CQRTradeRules;

public class MatchNBT implements ITradeMatchRule {

	@Override
	public boolean matches(ItemStack input, ItemStack toMatch) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Codec<? extends ITradeMatchRule> getType() {
		return CQRTradeRules.MATCH_NBT.get();
	}

}
