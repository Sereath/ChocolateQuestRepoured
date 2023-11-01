package team.cqr.cqrepoured.entity.trade;

import net.minecraft.network.chat.Component;
import team.cqr.cqrepoured.CQRConstants;

public enum EBuyResult {
	
	SUCCESS(CQRConstants.Translation.Trade.TRADE_RESULT_SUCCESS),
	NO_TRADE(CQRConstants.Translation.Trade.TRADE_RESULT_NO_TRADE),
	NO_INPUT(CQRConstants.Translation.Trade.TRADE_RESULT_NO_INPUT),
	NO_STOCK(CQRConstants.Translation.Trade.TRADE_RESULT_NO_STOCK),
	INPUT_INVALID(CQRConstants.Translation.Trade.TRADE_RESULT_INPUT_TYPE_INVALID),
	INPUT_RULES_NOT_MET(CQRConstants.Translation.Trade.TRADE_RESULT_INPUT_RULES_NOT_MET),
	CUSTOMER_RULES_NOT_MET(CQRConstants.Translation.Trade.TRADE_RESULT_CUSTOMER_RULES_NOT_MET);
	
	private final String translationKey;
	private final Component translationComponent;
	
	private EBuyResult(final String translationKey) {
		this.translationKey = translationKey;
		this.translationComponent = Component.translatable(translationKey);
	}
	
	public String getTranslationKey() {
		return this.translationKey;
	}
	
	public Component getTranslationComponent() {
		return this.translationComponent;
	}

}
