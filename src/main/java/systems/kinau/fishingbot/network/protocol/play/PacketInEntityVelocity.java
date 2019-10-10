/*
 * Created by David Luedtke (MrKinau)
 * 2019/5/5
 */

package systems.kinau.fishingbot.network.protocol.play;

import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;
import systems.kinau.fishingbot.MineBot;
import systems.kinau.fishingbot.fishing.FishingManager;
import systems.kinau.fishingbot.network.protocol.NetworkHandler;
import systems.kinau.fishingbot.network.protocol.Packet;
import systems.kinau.fishingbot.network.protocol.ProtocolConstants;
import systems.kinau.fishingbot.network.utils.ByteArrayDataInputWrapper;

@NoArgsConstructor
public class PacketInEntityVelocity extends Packet {

    private static short lastY = -1;

    @Override
    public void write(ByteArrayDataOutput out, int protocolId) { }

    @Override
    public void read(ByteArrayDataInputWrapper in, NetworkHandler networkHandler, int length, int protocolId) {
        if(!(MineBot.getInstance().getManager() instanceof FishingManager))
            return;
        int eid = readVarInt(in);
        short x = in.readShort();
        short y = in.readShort();
        short z = in.readShort();

        ((FishingManager)MineBot.getInstance().getManager()).addPossibleMotion(eid, x, y, z);

        if(((FishingManager)MineBot.getInstance().getManager()).getCurrentBobber() != eid)
            return;

        switch (protocolId) {
            case ProtocolConstants.MINECRAFT_1_10:
            case ProtocolConstants.MINECRAFT_1_9_4:
            case ProtocolConstants.MINECRAFT_1_9_2:
            case ProtocolConstants.MINECRAFT_1_9_1:
            case ProtocolConstants.MINECRAFT_1_9:
            case ProtocolConstants.MINECRAFT_1_8: {
                ((FishingManager)MineBot.getInstance().getManager()).fish();
                break;
            }
            case ProtocolConstants.MINECRAFT_1_13_2:
            case ProtocolConstants.MINECRAFT_1_13_1:
            case ProtocolConstants.MINECRAFT_1_13:
            case ProtocolConstants.MINECRAFT_1_12_2:
            case ProtocolConstants.MINECRAFT_1_12_1:
            case ProtocolConstants.MINECRAFT_1_12:
            case ProtocolConstants.MINECRAFT_1_11_1:
            case ProtocolConstants.MINECRAFT_1_11:
            case ProtocolConstants.MINECRAFT_1_14:
            case ProtocolConstants.MINECRAFT_1_14_1:
            case ProtocolConstants.MINECRAFT_1_14_2:
            case ProtocolConstants.MINECRAFT_1_14_3:
            case ProtocolConstants.MINECRAFT_1_14_4:
            default: {
                if(Math.abs(y) > 350) {
                    ((FishingManager)MineBot.getInstance().getManager()).fish();
                } else if(lastY == 0 && y == 0) {               //Sometimes Minecraft does not push the bobber down, but this workaround works good
                    ((FishingManager)MineBot.getInstance().getManager()).fish();
                }
                break;
            }
        }

        lastY = y;
    }
}
