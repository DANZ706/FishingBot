package systems.kinau.fishingbot.network.item;

import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import systems.kinau.fishingbot.bot.Enchantment;
import systems.kinau.fishingbot.network.item.datacomponent.DataComponent;
import systems.kinau.fishingbot.network.item.datacomponent.components.EnchantmentsComponent;
import systems.kinau.fishingbot.network.protocol.Packet;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class ComponentItemData implements ItemData {

    private final List<DataComponent> presentComponents;
    private final List<DataComponent> emptyComponents;

    @Override
    public List<Enchantment> getEnchantments() {
        return presentComponents.stream()
                .filter(dataComponent -> dataComponent instanceof EnchantmentsComponent)
                .map(dataComponent -> (EnchantmentsComponent) dataComponent)
                .flatMap(enchantmentsComponent -> enchantmentsComponent.getEnchantments().stream())
                .collect(Collectors.toList());
    }

    @Override
    public void write(ByteArrayDataOutput output, int protocolId) {
        Packet.writeVarInt(presentComponents.size(), output);
        Packet.writeVarInt(emptyComponents.size(), output);
        for (DataComponent presentComponent : presentComponents) {
            Packet.writeVarInt(presentComponent.getComponentTypeId(), output);
            presentComponent.write(output, protocolId);
        }
        for (DataComponent emptyComponent : emptyComponents) {
            Packet.writeVarInt(emptyComponent.getComponentTypeId(), output);
        }
    }
}
