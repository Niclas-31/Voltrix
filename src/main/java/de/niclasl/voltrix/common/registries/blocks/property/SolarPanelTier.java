package de.niclasl.voltrix.common.registries.blocks.property;

import com.mojang.serialization.Codec;
import de.niclasl.voltrix_api.energy.AmperageTier;
import de.niclasl.voltrix_api.energy.ElectricalProperties;
import de.niclasl.voltrix_api.energy.VoltageTier;

public enum SolarPanelTier {
    BASIC(VoltageTier.LV, AmperageTier.A2, 16384),
    ADVANCED(VoltageTier.MV, AmperageTier.A4, 131072),
    ELITE(VoltageTier.HV, AmperageTier.A8, 1048576);

    private final VoltageTier voltage;
    private final AmperageTier amperage;
    private final long capacity;

    public static final Codec<SolarPanelTier> CODEC = Codec.STRING.xmap(
            SolarPanelTier::valueOf,
            SolarPanelTier::name
    );

    SolarPanelTier(VoltageTier voltage, AmperageTier amperage, long capacity) {
        this.voltage = voltage;
        this.amperage = amperage;
        this.capacity = capacity;
    }

    public ElectricalProperties properties() {
        return ElectricalProperties.solarPanel(voltage, amperage);
    }

    public long capacity() {
        return capacity;
    }
}