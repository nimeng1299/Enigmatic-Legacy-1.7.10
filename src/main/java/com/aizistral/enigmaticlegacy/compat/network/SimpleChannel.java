package com.aizistral.enigmaticlegacy.compat.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.entity.player.EntityPlayerMP;

import com.aizistral.enigmaticlegacy.compat.network.NetworkEvent.NetworkDirection;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Channel facade mirroring Forge 1.20's SimpleChannel on top of 1.7.10's
 * SimpleNetworkWrapper.
 */
public class SimpleChannel {

    private static final Map<String, SimpleChannel> CHANNELS = new HashMap<>();

    private final String name;
    private final cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper wrapper;
    private final Map<Integer, Entry> entries = new HashMap<>();

    private SimpleChannel(String name) {
        this.name = name;
        this.wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(name);
        this.wrapper.registerMessage(TransportHandler.class, TransportMessage.class, 0, Side.CLIENT);
        this.wrapper.registerMessage(TransportHandler.class, TransportMessage.class, 0, Side.SERVER);
        CHANNELS.put(name, this);
    }

    public static SimpleChannel create(String name) {
        return new SimpleChannel(name);
    }

    public <MSG> MessageBuilder<MSG> messageBuilder(Class<MSG> type, int id) {
        return new MessageBuilder<>(this, type, id);
    }

    public <MSG> void registerMessage(int id, Class<MSG> type, BiConsumer<MSG, FriendlyByteBuf> encoder,
        Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler) {
        this.entries.put(id, new Entry(id, type, encoder, decoder, handler));
    }

    @SuppressWarnings("unchecked")
    public void send(PacketDistributor.Target<?> target, Object message) {
        TransportMessage transport = this.encodeMessage(message);

        switch (target.distributor.getType()) {
            case PLAYER:
                this.wrapper.sendTo(transport, (EntityPlayerMP) target.get());
                break;
            case ALL:
                this.wrapper.sendToAll(transport);
                break;
            case ALL_EXCEPT:
                EntityPlayerMP excluded = (EntityPlayerMP) target.get();
                for (Object player : FMLCommonHandler.instance()
                    .getMinecraftServerInstance()
                    .getConfigurationManager().playerEntityList) {
                    if (player != excluded) {
                        this.wrapper.sendTo(transport, (EntityPlayerMP) player);
                    }
                }
                break;
            case DIMENSION:
                Object dim = target.get();
                int dimensionId = dim instanceof Integer ? (Integer) dim : 0;
                this.wrapper.sendToDimension(transport, dimensionId);
                break;
            case SERVER:
                this.wrapper.sendToServer(transport);
                break;
            case NEAR:
                PacketDistributor.TargetPoint point = (PacketDistributor.TargetPoint) target.get();
                if (point != null && FMLCommonHandler.instance()
                    .getMinecraftServerInstance() != null) {
                    for (Object player : FMLCommonHandler.instance()
                        .getMinecraftServerInstance()
                        .getConfigurationManager().playerEntityList) {
                        EntityPlayerMP mp = (EntityPlayerMP) player;
                        if (mp.worldObj.provider.dimensionId == point.dimension
                            && mp.getDistanceSq(point.x, point.y, point.z) <= point.radius * point.radius) {
                            this.wrapper.sendTo(transport, mp);
                        }
                    }
                }
                break;
        }
    }

    public void sendToServer(Object message) {
        this.wrapper.sendToServer(this.encodeMessage(message));
    }

    public void sendTo(Object message, EntityPlayerMP player) {
        this.wrapper.sendTo(this.encodeMessage(message), player);
    }

    public void sendToAll(Object message) {
        this.wrapper.sendToAll(this.encodeMessage(message));
    }

    public void sendToDimension(Object message, int dimensionId) {
        this.wrapper.sendToDimension(this.encodeMessage(message), dimensionId);
    }

    private TransportMessage encodeMessage(Object message) {
        Entry entry = this.findEntry(message.getClass());

        if (entry == null) {
            throw new IllegalArgumentException("Message class not registered: " + message.getClass());
        }

        ByteBuf payload = Unpooled.buffer();
        FriendlyByteBuf friendly = new FriendlyByteBuf(payload);
        entry.encoder.accept(message, friendly);
        byte[] data = new byte[payload.readableBytes()];
        payload.readBytes(data);

        return new TransportMessage(this.name, entry.id, data);
    }

    private Entry findEntry(Class<?> clazz) {
        for (Entry entry : this.entries.values()) {
            if (entry.type.isAssignableFrom(clazz)) return entry;
        }
        return null;
    }

    static void scheduleTask(Side side, Runnable runnable) {
        if (side == Side.CLIENT) {
            net.minecraft.client.Minecraft.getMinecraft()
                .func_152344_a(runnable);
        } else {
            runnable.run();
        }
    }

    public static class MessageBuilder<MSG> {

        private final SimpleChannel channel;
        private final Class<MSG> type;
        private final int id;
        private BiConsumer<MSG, FriendlyByteBuf> encoder;
        private Function<FriendlyByteBuf, MSG> decoder;
        private BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler;

        private MessageBuilder(SimpleChannel channel, Class<MSG> type, int id) {
            this.channel = channel;
            this.type = type;
            this.id = id;
        }

        public MessageBuilder<MSG> encoder(BiConsumer<MSG, FriendlyByteBuf> encoder) {
            this.encoder = encoder;
            return this;
        }

        public MessageBuilder<MSG> decoder(Function<FriendlyByteBuf, MSG> decoder) {
            this.decoder = decoder;
            return this;
        }

        public MessageBuilder<MSG> consumerMainThread(BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler) {
            this.handler = handler;
            return this;
        }

        public MessageBuilder<MSG> consumer(BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler) {
            this.handler = handler;
            return this;
        }

        public MessageBuilder<MSG> add() {
            this.channel.registerMessage(this.id, this.type, this.encoder, this.decoder, this.handler);
            return this;
        }
    }

    private static class Entry {

        private final int id;
        private final Class<?> type;
        private final BiConsumer encoder;
        private final Function decoder;
        private final BiConsumer handler;

        @SuppressWarnings("unchecked")
        private Entry(int id, Class<?> type, BiConsumer<?, FriendlyByteBuf> encoder,
            Function<FriendlyByteBuf, ?> decoder, BiConsumer<?, Supplier<NetworkEvent.Context>> handler) {
            this.id = id;
            this.type = type;
            this.encoder = encoder;
            this.decoder = decoder;
            this.handler = handler;
        }
    }

    public static class TransportMessage implements IMessage {

        private String channelName;
        private int id;
        private byte[] data;

        public TransportMessage() {
            // NO-OP
        }

        public TransportMessage(String channelName, int id, byte[] data) {
            this.channelName = channelName;
            this.id = id;
            this.data = data;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.channelName = cpw.mods.fml.common.network.ByteBufUtils.readUTF8String(buf);
            this.id = buf.readInt();
            this.data = new byte[buf.readInt()];
            buf.readBytes(this.data);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            cpw.mods.fml.common.network.ByteBufUtils.writeUTF8String(buf, this.channelName);
            buf.writeInt(this.id);
            buf.writeInt(this.data.length);
            buf.writeBytes(this.data);
        }
    }

    public static class TransportHandler implements IMessageHandler<TransportMessage, IMessage> {

        @Override
        public IMessage onMessage(TransportMessage message, MessageContext ctx) {
            SimpleChannel channel = CHANNELS.get(message.channelName);
            if (channel == null) return null;
            channel.dispatch(message, ctx);
            return null;
        }
    }

    private void dispatch(TransportMessage message, MessageContext ctx) {
        Entry entry = this.entries.get(message.id);
        if (entry == null) return;

        FriendlyByteBuf friendly = new FriendlyByteBuf(Unpooled.wrappedBuffer(message.data));
        Object decoded = entry.decoder.apply(friendly);

        EntityPlayerMP sender = ctx.side == Side.SERVER ? ctx.getServerHandler().playerEntity : null;
        NetworkDirection direction = ctx.side == Side.SERVER ? NetworkDirection.PLAY_TO_SERVER
            : NetworkDirection.PLAY_TO_CLIENT;
        NetworkEvent.Context context = new NetworkEvent.Context(direction, ctx.side, sender);

        entry.handler.accept(decoded, (Supplier<NetworkEvent.Context>) () -> context);
    }

}
