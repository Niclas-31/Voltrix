package de.niclasl.voltrix.extensions;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public class ModExtensions {
    public static final EnumProxy<Rarity> MYTHIC = new EnumProxy<>(
            Rarity.class, 4, "voltrix:mythic",
            (UnaryOperator<Style>) style -> style.withColor(ChatFormatting.DARK_RED)
    );

    public static final EnumProxy<Rarity> LEGENDARY = new EnumProxy<>(
            Rarity.class, 5, "voltrix:legendary",
            (UnaryOperator<Style>) style -> style.withColor(ChatFormatting.GOLD)
    );
}