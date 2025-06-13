package org.sobadfish.teleportgun.manager;

import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigManager {

    public Config config;

    public LinkedHashMap<String,String> publicTeleportPoint = new LinkedHashMap<>();

    public ConfigManager(Config config) {
        this.config = config;

    }


    public void load(){
        Map<?,?> tp = (Map<?, ?>) config.get("public-teleport-point");
        for(Map.Entry<?,?> entry : tp.entrySet()){
            publicTeleportPoint.put((String)entry.getKey(), (String)entry.getValue());
        }
    }
    
    public Position getPositionByString(String name){
        if(publicTeleportPoint.containsKey(name)){
            String[] split = publicTeleportPoint.get(name).split(":");
            return new Position(
                    Double.parseDouble(split[0]),
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[2]),
                    Server.getInstance().getLevelByName(split[3]));
        }else{
            return null;
        }

    }

    public Map<String,Position> getAllPosition(){
        Map<String, Position> map = new LinkedHashMap<>();
        for(Map.Entry<String, String> entry : publicTeleportPoint.entrySet()){
            Position pt = getPositionByString(entry.getKey());
            if(pt == null){
                continue;
            }
            map.put(entry.getKey(), pt);
        }
        return map;
    }

    public void addPosition(String name, Position pos){
        publicTeleportPoint.put(name, pos.x + ":" + pos.y + ":" + pos.z + ":" + pos.getLevel().getFolderName());
        save();
    }

    public void addPosition(String name, String pos){
        publicTeleportPoint.put(name, pos);
        save();
    }

    public void save(){
        config.set("public-teleport-point", publicTeleportPoint);
        config.save();
    }
}



