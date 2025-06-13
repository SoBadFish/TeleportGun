package org.sobadfish.teleportgun.tasks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityWalking;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.PluginTask;
import org.sobadfish.teleportgun.TeleportGunMainClass;
import org.sobadfish.teleportgun.items.TeleportItem;

import java.util.*;

public class TeleportRunTask extends PluginTask<TeleportGunMainClass> {

    public TeleportRunTask(TeleportGunMainClass owner) {
        super(owner);
    }

    @Override
    public void onRun(int i) {
        long now = System.currentTimeMillis();

        // 移除超时传送门
        owner.teleportItems.removeIf(item -> now - item.createTime > 5000);

        for (TeleportItem portal : owner.teleportItems) {
            handlePortal(portal.startPosition, portal.endPosition);
            handlePortal(portal.endPosition, portal.startPosition);
        }

        // 清理离开传送门区域的实体状态
        Iterator<UUID> iterator = owner.teleportedEntities.keySet().iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            Entity entity = getEntityById(uuid);
            if (entity == null) {
                iterator.remove();
                continue;
            }
            Position current = entity.getPosition();
            Position last = owner.teleportedEntities.get(uuid);
            if (current.distance(last) > 2.0) {
                iterator.remove();
            }
        }
    }

    private void handlePortal(Position from, Position to) {
        Level level = from.level;
        AxisAlignedBB box = new SimpleAxisAlignedBB(
                from.x - 1, from.y - 1, from.z - 1,
                from.x + 1, from.y + 1, from.z + 1
        );

        for (Entity entity : level.getNearbyEntities(box, null)) {
            if(entity instanceof EntityItem || entity instanceof EntityWalking || entity instanceof Player) {
                UUID id = entity.getUniqueId();
                if (!from.equals(owner.teleportedEntities.get(id))) {
                    //先检测地图是否加载 之后检查区块是否加载
                    Level level1 = to.level;
                    if(!Server.getInstance().isLevelLoaded(level1.getFolderName())){
                        Server.getInstance().loadLevel(level1.getFolderName());
                    }
                    if(!level1.isChunkLoaded(to.getChunkX(),to.getChunkZ())){
                        level1.loadChunk(to.getChunkX(),to.getChunkZ());
                    }

                    entity.teleport(to.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    owner.teleportedEntities.put(id, to);
                }
            }
        }
    }

    private Entity getEntityById(UUID uuid) {
        for (Level level : Server.getInstance().getLevels().values()) {
            for (Entity entity : level.getEntities()) {
                if (entity.getUniqueId().equals(uuid)) {
                    return entity;
                }
            }
        }
        return null;
    }
}