package org.sobadfish.teleportgun.manager;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    public Config config;

    public LinkedHashMap<String,String> publicTeleportPoint = new LinkedHashMap<>();

    public LinkedHashMap<String,String> publicGreenTeleportPoint = new LinkedHashMap<>();

    // 禁止设置传送点的世界列表（文件夹名）
    public List<String> banSetPointWorlds = new ArrayList<>();

    public ConfigManager(Config config) {
        this.config = config;
    }

    public void load(){
        // 加载公共传送点
        Map<?,?> tp = (Map<?, ?>) config.get("public-teleport-point");
        if(tp != null) {
            for (Map.Entry<?, ?> entry : tp.entrySet()) {
                publicTeleportPoint.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
        // 加载绿色传送点
        Map<?,?> tpGreen = (Map<?, ?>) config.get("public-teleport-green-point");
        if(tpGreen != null) {
            for (Map.Entry<?, ?> entry : tpGreen.entrySet()) {
                publicGreenTeleportPoint.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
        // 加载禁止设置传送点的世界（文件夹名）
        List<?> banWorlds = (List<?>) config.get("ban-set-point-world");
        if(banWorlds != null) {
            for (Object worldName : banWorlds) {
                if(worldName instanceof String) {
                    banSetPointWorlds.add(((String) worldName).trim());
                }
            }
        }
    }

    // 判断世界是否被禁止设置传送点
    public boolean isBanSetPointWorld(String worldFolderName) {
        return banSetPointWorlds.contains(worldFolderName);
    }

    // 获取禁止设置传送点的世界列表
    public List<String> getBanSetPointWorlds() {
        return new ArrayList<>(banSetPointWorlds);
    }

    public Position getPositionByString(LinkedHashMap<String,String> map,String name){
        if(map.containsKey(name)){
            String[] split = map.get(name).split(":");
            if(split.length < 4){
                return null;
            }
            Level level = Server.getInstance().getLevelByName(split[3]);
            if(level == null) return null;
            return new Position(
                    Double.parseDouble(split[0]),
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[2]),
                    level);
        }else{
            return null;
        }
    }

    public Map<String,Position> getAllPosition(){
        Map<String, Position> map = new LinkedHashMap<>();
        for(Map.Entry<String, String> entry : publicTeleportPoint.entrySet()){
            Position pt = getPositionByString(publicTeleportPoint,entry.getKey());
            if(pt != null){
                map.put(entry.getKey(), pt);
            }
        }
        return map;
    }

    public Map<String,Position> getGreenAllPosition(){
        Map<String, Position> map = new LinkedHashMap<>();
        for(Map.Entry<String, String> entry : publicGreenTeleportPoint.entrySet()){
            Position pt = getPositionByString(publicGreenTeleportPoint,entry.getKey());
            if(pt != null){
                map.put(entry.getKey(), pt);
            }
        }
        return map;
    }

    public void addPosition(String name, String pos,boolean isGreen){
        if(isGreen){
            publicGreenTeleportPoint.put(name, pos);
        }else{
            publicTeleportPoint.put(name,pos);
        }
        save();
    }

    public void save(){
        config.set("public-teleport-point", publicTeleportPoint);
        config.set("public-teleport-green-point", publicGreenTeleportPoint);
        config.set("ban-set-point-world", banSetPointWorlds);
        config.save();
    }
}