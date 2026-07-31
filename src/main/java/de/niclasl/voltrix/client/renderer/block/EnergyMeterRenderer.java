package de.niclasl.voltrix.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.niclasl.voltrix.common.registries.blocks.custom.transmission.EnergyMeter;
import de.niclasl.voltrix.common.registries.blocks.entities.transmission.EnergyMeterEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class EnergyMeterRenderer implements BlockEntityRenderer<EnergyMeterEntity, EnergyMeterRenderState> {

    private final Font font;

    public EnergyMeterRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
    }

    @Override
    public @NonNull EnergyMeterRenderState createRenderState() {
        return new EnergyMeterRenderState();
    }

    @Override
    public void extractRenderState(@NonNull EnergyMeterEntity entity, @NonNull EnergyMeterRenderState state, float partialTicks,
                                   @NonNull Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPosition, breakProgress);

        state.facing = entity.getBlockState().getValue(EnergyMeter.FACING);
        state.rotation = switch (state.facing) {
            case NORTH -> 180f;
            case WEST -> -90f;
            case EAST -> 90f;
            default -> 0f;
        };
        state.powerState = entity.getPowerState();
        state.lines = List.of(
                "Voltage: " + state.powerState.voltage() + " V",
                "Amperage: " + state.powerState.amperage() + " A",
                "Power: " + state.powerState.power() + " W",
                "Overloaded: " + state.powerState.overloaded()
        );
    }

    @Override
    public void submit(@NonNull EnergyMeterRenderState state, @NonNull PoseStack poseStack,
                       @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState cameraState) {
        poseStack.pushPose();

        switch (state.facing) {
            case NORTH -> poseStack.translate(0.5, 0.7, 0);
            case SOUTH -> poseStack.translate(0.5, 0.7, 1.0);
            case WEST -> poseStack.translate(0, 0.7, 0.5);
            case EAST -> poseStack.translate(1.0, 0.7, 0.5);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation));

        poseStack.scale(0.008f, -0.008f, 0.008f);

        float y = 0;

        for (String line : state.lines) {

            FormattedCharSequence text = Component.literal(line).getVisualOrderText();

            float x = -font.width(text) / 2f;

            if (!state.powerState.overloaded()) {
                collector.submitText(
                        poseStack,
                        x,
                        y,
                        text,
                        false,
                        Font.DisplayMode.POLYGON_OFFSET,
                        LightCoordsUtil.FULL_BRIGHT,
                        DyeColor.YELLOW.getTextColor(),
                        0,
                        0
                );
            } else {
                collector.submitText(
                        poseStack,
                        x,
                        y,
                        text,
                        false,
                        Font.DisplayMode.POLYGON_OFFSET,
                        LightCoordsUtil.FULL_BRIGHT,
                        DyeColor.RED.getTextColor(),
                        0,
                        0
                );
            }

            y += font.lineHeight;
        }

        poseStack.popPose();
    }
}