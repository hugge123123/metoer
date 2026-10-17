package dev.escoter.stafflookalert;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.sound.PositionedSoundInstance;

import java.util.Locale;

public class StaffLookAlert extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> watchNames = sgGeneral.add(new StringSetting.Builder()
        .name("watch-names")
        .description("Player names to watch, separated by commas. Does not identify admins automatically.")
        .defaultValue("")
        .build());
    private final Setting<Double> maxDistance = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-distance").description("Maximum distance for a look-direction check.")
        .defaultValue(32.0).min(4.0).max(128.0).sliderMin(4.0).sliderMax(128.0).build());
    private final Setting<Double> angle = sgGeneral.add(new DoubleSetting.Builder()
        .name("look-angle").description("Maximum angle between their look direction and the direction toward you.")
        .defaultValue(12.0).min(2.0).max(45.0).sliderMin(2.0).sliderMax(45.0).build());
    private final Setting<Boolean> sound = sgGeneral.add(new BoolSetting.Builder()
        .name("sound").description("Play a sound when a watched player appears to look at you.")
        .defaultValue(true).build());
    private final Setting<Boolean> chatAlert = sgGeneral.add(new BoolSetting.Builder()
        .name("chat-alert").description("Show a chat warning.")
        .defaultValue(true).build());

    private long lastAlertTime = 0L;

    public StaffLookAlert() {
        super(Categories.Misc, "staff-look-alert", "Warns when a player on your watch list appears to look toward you.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        String configured = watchNames.get().toLowerCase(Locale.ROOT).trim();
        if (configured.isEmpty()) return;
        Vec3d myPos = mc.player.getEyePos();
        long now = System.currentTimeMillis();

        for (AbstractClientPlayerEntity other : mc.world.getPlayers()) {
            if (other == mc.player) continue;
            String name = other.getName().getString();
            if (name == null || !isWatched(configured, name)) continue;
            Vec3d toMe = myPos.subtract(other.getEyePos());
            double distance = toMe.length();
            if (distance < 0.01 || distance > maxDistance.get()) continue;
            Vec3d look = other.getRotationVec(1.0f).normalize();
            double dot = look.dotProduct(toMe.normalize());
            double threshold = Math.cos(Math.toRadians(angle.get()));
            if (dot >= threshold && (now - lastAlertTime) > 5000L) {
                lastAlertTime = now;
                if (chatAlert.get()) info("Look alert: %s may be looking toward you.", name);
                if (sound.get()) {
                    mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1.0f));
                }
                break;
            }
        }
    }

    private boolean isWatched(String configured, String name) {
        for (String entry : configured.split(",")) {
            if (entry.trim().equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}
