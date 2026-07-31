package de.niclasl.voltrix.client.renderer.block;

import de.niclasl.voltrix_api.energy.state.PowerState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

import java.util.List;

public class EnergyMeterRenderState extends BlockEntityRenderState {
    public Direction facing;
    public float rotation;
    public PowerState powerState;
    public List<String> lines;
}