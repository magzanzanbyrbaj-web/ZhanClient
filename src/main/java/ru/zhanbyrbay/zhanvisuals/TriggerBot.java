package myself;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Optional;
import kotlin.jvm.functions.Function0;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class TriggerBot extends Module {
    
    // Настройки с ползунками и переключателями для удобного меню
    public final I1I1o1oOO1l0OIio0IoiIoI0OOOloiOl rangeSetting = this.Command(new I1I1o1oOO1l0OIio0IoiIoI0OOOloiOl("Дистанция атаки", 3.2f, 1.0f, 6.0f, 0.1f, " блоков"));
    public final I1I1o1oOO1l0OIio0IoiIoI0OOOloiOl extraRangeSetting = this.Command(new I1I1o1oOO1l0OIio0IoiIoI0OOOloiOl("Доп. дистанция (Hitbox)", 0.4f, 0.0f, 2.0f, 0.05f, " блоков"));
    
    public final OoOIiI0OlOoIoiilIoI1IOolIOI1ilIO throughWalls = this.Command(new OoOIiI0OlOoIoiilIoI1IOolIOI1ilIO("Бить сквозь стены", false));
    public final OoOIiI0OlOoIoiilIoI1IOolIOI1ilIO raycastCheck = this.Command(new OoOIiI0OlOoIoiilIoI1IOolIOI1ilIO("Raycast проверка", true));
    public final OoOIiI0OlOoIoiilIoI1IOolIOI1ilIO coolDownCheck = this.Command(new OoOIiI0OlOoIoiilIoI1IOolIOI1ilIO("Учитывать кулдаун", true));
    
    public final I1I1o1oOO1l0OIio0IoiIoI0OOOloiOl coolDownValue = this.Command(this.Command(new I1I1o1oOO1l0OIio0IoiIoI0OOOloiOl("Мин. кулдаун", 80.0f, 0.0f, 100.0f, 1.0f, "%"), (Function0<Boolean>)((Function0)() -> (Boolean)this.coolDownCheck.ilIIio0IOOIIlO0Ii0lIIIoOIOllilIl())));
    
    public final OoOIiI0OlOoIoiilIoI1IOolIOI1ilIO tpsSync = this.Command(new OoOIiI0OlOoIoiilIoI1IOolIOI1ilIO("TPS Синхронизация", true));
    public final OoOIiI0OlOoIoiilIoI1IOolIOI1ilIO targetPlayers = this.Command(new OoOIiI0OlOoIoiilIoI1IOolIOI1ilIO("Цель: Игроки", true));
    public final OoOIiI0OlOoIoiilIoI1IOolIOI1ilIO targetMobs = this.Command(new OoOIiI0OlOoIoiilIoI1IOolIOI1ilIO("Цель: Мобы", false));

    private LivingEntity targetEntity;
    private boolean isAuraActive;

    public TriggerBot() {
        super("TriggerBot / Aura", I1o0oiilooOioioIoooOoOoIiioOOI0O.ilIIio0IOOIIlO0Ii0lIIIoOIOllilIl, "Автоматическая атака целей в радиусе действия", false, -1);
    }

    public void tick() {
        if (!this.I0iO0ilIOOIooiolOiIOOOIolI0IO1oo()) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || mc.gameMode == null || mc.screen != null) return;

        LivingEntity foundTarget = this.findBestTarget(player, mc);
        if (foundTarget != null) {
            this.targetEntity = foundTarget;
            if (this.checkConditions(player, mc)) {
                this.attack(player, foundTarget, mc);
            }
        } else {
            this.targetEntity = null;
        }
    }

    private LivingEntity findBestTarget(LocalPlayer player, Minecraft mc) {
        float baseRange = ((Float)this.rangeSetting.ilIIio0IOOIIlO0Ii0lIIIoOIOllilIl()).floatValue();
        float extra = ((Float)this.extraRangeSetting.ilIIio0IOOIIlO0Ii0lIIIoOIOllilIl()).floatValue();
        double totalRange = baseRange + extra;

        Vec3 eye = player.getEyePosition(1.0f);
        AABB searchBox = player.getBoundingBox().inflate(totalRange);

        LivingEntity best = null;
        double minDistanceSq = totalRange * totalRange;

        for (Entity entity : mc.level.getEntitiesOfClass(LivingEntity.class, searchBox, e -> e != player && e.isAlive())) {
            if (!isValidTarget((LivingEntity) entity)) continue;
            
            double distSq = player.distanceToSqr(entity);
            if (distSq <= minDistanceSq) {
                best = (LivingEntity) entity;
                minDistanceSq = distSq;
            }
        }
        return best;
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (entity instanceof Player && !((Boolean)this.targetPlayers.ilIIio0IOOIIlO0Ii0lIIIoOIOllilIl()).booleanValue()) return false;
        if (entity instanceof Mob && !((Boolean)this.targetMobs.ilIIio0IOOIIlO0Ii0lIIIoOIOllilIl()).booleanValue()) return false;
        return entity.isAlive() && !entity.isSpectator();
    }

    private boolean checkConditions(LocalPlayer player, Minecraft mc) {
        if ((Boolean)this.coolDownCheck.ilIIio0IOOIIlO0Ii0lIIIoOIOllilIl()).booleanValue()) {
            float requiredCD = ((Float)this.coolDownValue.ilIIio0IOOIIlO0Ii0lIIIoOIOllilIl()).floatValue() / 100.0f;
            if (player.getAttackStrengthScale(0.5f) < requiredCD) {
                return false;
            }
        }
        return true;
    }

    private void attack(LocalPlayer player, LivingEntity target, Minecraft mc) {
        mc.gameMode.attack((Player) player, (Entity) target);
        player.swing(InteractionHand.MAIN_HAND);
        player.resetAttackStrengthTicker();
    }
}
