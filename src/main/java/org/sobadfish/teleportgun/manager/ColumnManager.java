package org.sobadfish.teleportgun.manager;

public class ColumnManager {

    /**
     * 传送点列表TAG标签 基本用于 ListTag
     * */
    public static final String TELEPORT_LIST_TAG = "teleport_points";

    /**
     * 是否允许传送玩家标签 用于BooleanTag
     * */
    public static final String ENABLE_PLAYER = "enable_player";

    /**
     * 用于开启传送门坐标 也是最终传送的坐标
     * */
    public static final String TELEPORT_LOCATION = "teleport_location";

    /**
     * 允许玩家设置的最大数量 用于IntTag
     * */
    public static final String MAX_TELEPORT_POINTS = "max_player_teleport_points";

    /**
     * 玩家保存的 传送点 基本用于 ListTag
     * */
    public static final String TELEPORT_HOME_LIST_TAG = "teleport_home_points";
}
