package team.cqr.cqrepoured.generation.world.level.levelgen.structure;

import java.util.Locale;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

public enum InhabitantSelectorType implements StringRepresentable {

	;

	public static final Codec<InhabitantSelectorType> CODEC = StringRepresentable.fromEnum(InhabitantSelectorType::values);
	private final Codec<? extends InhabitantSelector> codec;

	private InhabitantSelectorType(Codec<? extends InhabitantSelector> codec) {
		this.codec = codec;
	}

	public Codec<? extends InhabitantSelector> codec() {
		return codec;
	}

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}

}
