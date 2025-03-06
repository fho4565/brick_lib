package com.fho4565.brick_lib.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import static com.fho4565.brick_lib.item.ICooldownItem.*;

public class CooldownDataComponent {
    public static Codec<CooldownDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf(COOLDOWN_TAG).forGetter(CooldownDataComponent::currentCd),
                    Codec.INT.fieldOf(COOLDOWN_MAX_TAG).forGetter(CooldownDataComponent::maxTime),
                    Codec.BOOL.fieldOf(TICK_TAG).forGetter(CooldownDataComponent::tick),
                    Codec.BOOL.fieldOf(AUTO_TAG).forGetter(CooldownDataComponent::auto),
                    Codec.BOOL.fieldOf(RENDER_BAR_TAG).forGetter(CooldownDataComponent::render_bar),
                    Codec.BOOL.fieldOf(RENDER_BAR_WHEN_ENDS_TAG).forGetter(CooldownDataComponent::render_bar_when_ends)
            ).apply(instance, CooldownDataComponent::new)
    );
    public static StreamCodec<RegistryFriendlyByteBuf, CooldownDataComponent> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistriesTrusted(CODEC);
    protected int currentCd;
    int maxTime;
    boolean tick;
    boolean auto;
    boolean render_bar;
    boolean render_bar_when_ends;

    public CooldownDataComponent(int currentCd, int maxTime, boolean tick, boolean auto, boolean render_bar, boolean render_bar_when_ends) {
        this.tick = tick;
        this.render_bar_when_ends = render_bar_when_ends;
        this.render_bar = render_bar;
        this.maxTime = maxTime;
        this.currentCd = currentCd;
        this.auto = auto;
    }

    public static void setCODEC(Codec<CooldownDataComponent> CODEC) {
        CooldownDataComponent.CODEC = CODEC;
    }

    public static void setStreamCodec(StreamCodec<RegistryFriendlyByteBuf, CooldownDataComponent> streamCodec) {
        STREAM_CODEC = streamCodec;
    }

    public boolean render_bar() {
        return render_bar;
    }

    public boolean auto() {
        return auto;
    }

    public int currentCd() {
        return currentCd;
    }

    public int maxTime() {
        return maxTime;
    }

    public boolean render_bar_when_ends() {
        return render_bar_when_ends;
    }

    public boolean tick() {
        return tick;
    }

    public void setRender_bar(boolean render_bar) {
        this.render_bar = render_bar;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public void setCurrentCd(int currentCd) {
        this.currentCd = currentCd;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public void setRender_bar_when_ends(boolean render_bar_when_ends) {
        this.render_bar_when_ends = render_bar_when_ends;
    }

    public void setTick(boolean tick) {
        this.tick = tick;
    }
}
