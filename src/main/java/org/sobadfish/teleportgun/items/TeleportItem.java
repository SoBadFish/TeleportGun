package org.sobadfish.teleportgun.items;

import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

public class TeleportItem {


    public Position startPosition;

    public Position endPosition;

    public long createTime;

    public TeleportItem(Position start, Position end) {
        this.startPosition = start;
        this.endPosition = end;
        this.createTime = System.currentTimeMillis();
    }
}
