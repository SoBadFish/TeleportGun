package org.sobadfish.teleportgun.utils;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.SpawnParticleEffectPacket;
import cn.nukkit.scheduler.PluginTask;
import org.sobadfish.teleportgun.TeleportGunMainClass;

public class GenerateParticleUtils {


    /**
     * 添加自定义颜色粒子
     * */
    public static void addDoorParticleXy(Level level, Vector3 vector3,boolean isBlue,boolean isXy){

        SpawnParticleEffectPacket pk = new SpawnParticleEffectPacket();
        pk.identifier = "ltrpg:teleportdoor_"+(isXy?"xy":"xz")+(isBlue?"_blue":"");
//        System.out.println("identifier： "+pk.identifier);
        pk.dimensionId = level.getDimensionData().getDimensionId();
        pk.position = vector3.asVector3f();
        level.addChunkPacket(vector3.getChunkX(),vector3.getChunkZ(),pk);


    }

    /**
     * 获取玩家面前x格的位置坐标
     *
     * @param playerPos 玩家当前坐标 (x, y, z)
     * @return 面前一格的位置坐标
     */
    public static Vector3 getPositionInFrontOfPlayer(Entity playerPos, float side) {
        // 获取玩家朝向的方向向量 (单位向量)
        Vector3 directionVector = playerPos.getDirectionVector();

        // 计算前 N 格的位置 (方向向量乘以距离并加到当前位置)
        float frontX = (float) (playerPos.x + directionVector.x * side);
        float frontY = (float) (playerPos.y + directionVector.y * side); // 高度一般保持不变
        float frontZ = (float) (playerPos.z + directionVector.z * side);

        // 返回面前一格的坐标
        return new Vector3(frontX, frontY, frontZ);
    }

    public static String asLocation(Position position) {
        return position.getX() + ":" + position.getY() + ":" + position.getZ() + ":" + position.level.getFolderName();
    }

    public static Position asPosition(String location) {
        String[] sl = location.split(":");
        if(sl.length <= 3 ) return null;
        Level level =   Server.getInstance().getLevelByName(sl[3]);
        if(level == null) return null;
        return new Position(
                Double.parseDouble(sl[0]),
                Double.parseDouble(sl[1]),
                Double.parseDouble(sl[2]),
                Server.getInstance().getLevelByName(sl[3]));
    }

    public static void addDelayTask(PluginTask<TeleportGunMainClass> task, int delay) {
        Server.getInstance().getScheduler().scheduleDelayedTask(TeleportGunMainClass.INSTANCE, task, delay);
    }



}
