package de.niclasl.voltrix.common.registries.blocks.entities.transmission;

import de.niclasl.voltrix.common.registries.blocks.custom.transmission.EnergyMeter;
import de.niclasl.voltrix.common.registries.blocks.entities.ModBlockEntities;
import de.niclasl.voltrix.common.registries.blocks.entities.base.AbstractCableEntity;
import de.niclasl.voltrix.common.registries.blocks.entities.base.AbstractEnergyEntity;
import de.niclasl.voltrix_api.energy.ConnectionMode;
import de.niclasl.voltrix_api.energy.ElectricalProperties;
import de.niclasl.voltrix_api.energy.IPowerStateReceiver;
import de.niclasl.voltrix_api.energy.state.PowerState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

public class EnergyMeterEntity extends AbstractEnergyEntity implements IPowerStateReceiver {

    private PowerState powerState = PowerState.EMPTY;

    public EnergyMeterEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_METER.get(), pos, state, 0, ElectricalProperties.EMPTY);
    }

    @Override
    protected ConnectionMode getDefaultConnection(Direction direction) {
        if (level == null) {
            return ConnectionMode.NONE;
        }

        BlockPos nextPos = worldPosition.relative(direction);
        BlockEntity nextEntity = level.getBlockEntity(nextPos);

        if (nextEntity instanceof AbstractCableEntity) {
            return ConnectionMode.BOTH;
        } else {
            return ConnectionMode.NONE;
        }
    }

    @Override
    public void handleUpdateTag(@NonNull ValueInput input) {
        super.handleUpdateTag(input);

        this.powerState = new PowerState(
                input.getIntOr("voltage", 0),
                input.getIntOr("amperage", 0),
                input.getBooleanOr("overloaded", false)
        );
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        this.powerState = new PowerState(
                input.getIntOr("voltage", 0),
                input.getIntOr("amperage", 0),
                input.getBooleanOr("overloaded", false)
        );
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);

        output.putInt("voltage", this.powerState.voltage());
        output.putInt("amperage", this.powerState.amperage());
        output.putBoolean("overloaded", this.powerState.overloaded());
    }

    @Override
    public void setPowerState(PowerState powerState) {
        this.powerState = powerState;

        setChanged();

        if (level != null && !level.isClientSide()) {

            boolean overloaded = powerState.overloaded();

            if (getBlockState().getValue(EnergyMeter.OVERLOADED) != overloaded) {
                level.setBlock(worldPosition, getBlockState().setValue(EnergyMeter.OVERLOADED, overloaded),
                        3);
            }

            sync();
        }
    }

    @Override
    public PowerState getPowerState() {
        return powerState;
    }
}
