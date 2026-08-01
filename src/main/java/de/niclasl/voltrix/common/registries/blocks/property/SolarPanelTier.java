package de.niclasl.voltrix.common.registries.blocks.property;

import com.mojang.serialization.Codec;
import de.niclasl.voltrix_api.energy.AmperageTier;
import de.niclasl.voltrix_api.energy.ElectricalProperties;
import de.niclasl.voltrix_api.energy.VoltageTier;

public enum SolarPanelTier {
    BASIC(VoltageTier.LV, AmperageTier.A2),
    ADVANCED(VoltageTier.MV, AmperageTier.A4),
    ELITE(VoltageTier.HV, AmperageTier.A8);

    private final VoltageTier voltage;
    private final AmperageTier amperage;

    public static final Codec<SolarPanelTier> CODEC = Codec.STRING.xmap(
            SolarPanelTier::valueOf,
            SolarPanelTier::name
    );

    SolarPanelTier(VoltageTier voltage, AmperageTier amperage) {
        this.voltage = voltage;
        this.amperage = amperage;
    }

    public ElectricalProperties properties() {
        return ElectricalProperties.solarPanel(voltage, amperage);
    }
}