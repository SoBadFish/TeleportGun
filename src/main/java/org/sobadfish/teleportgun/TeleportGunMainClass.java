package org.sobadfish.teleportgun;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.custom.EntityManager;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityTeleportEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.teleportgun.customitem.BaseTeleportGunItem;
import org.sobadfish.teleportgun.customitem.CustomTeleportBlueItem;
import org.sobadfish.teleportgun.customitem.CustomTeleportItem;
import org.sobadfish.teleportgun.customitem.CustomTeleportWaterItem;
import org.sobadfish.teleportgun.entitys.TeleportGunDropEntityItem;
import org.sobadfish.teleportgun.form.ICustomForm;
import org.sobadfish.teleportgun.form.push.PlayerTeleportModelChoseForm;
import org.sobadfish.teleportgun.items.TeleportItem;
import org.sobadfish.teleportgun.manager.ColumnManager;
import org.sobadfish.teleportgun.manager.ConfigManager;
import org.sobadfish.teleportgun.manager.FormManager;
import org.sobadfish.teleportgun.tasks.TeleportRunTask;
import org.sobadfish.teleportgun.utils.GenerateParticleUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class TeleportGunMainClass extends PluginBase implements Listener {

    public static List<Player> lockedPlayers = new ArrayList<>();

    public static TeleportGunMainClass INSTANCE;

    public CopyOnWriteArrayList<TeleportItem> teleportItems = new CopyOnWriteArrayList<>();

    public Map<UUID, Position> teleportedEntities = new HashMap<>();

    public FormManager formManager;

    public ConfigManager configManager;

    @Override
    public void onLoad() {
        EntityManager.get().registerDefinition(TeleportGunDropEntityItem.DEF_BLUE);
        EntityManager.get().registerDefinition(TeleportGunDropEntityItem.DEF_GREEN);
    }



    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();
        reloadConfig();
        configManager = new ConfigManager(getConfig());
        configManager.load();
        Item.registerCustomItem(CustomTeleportItem.class);
        Item.registerCustomItem(CustomTeleportBlueItem.class);
        Item.registerCustomItem(CustomTeleportWaterItem.class);
        //修改lore后 再扔到创造背包
        if(formManager == null){
            formManager = new FormManager();
        }else{
            formManager.clearForms();
        }

        getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getScheduler().scheduleDelayedRepeatingTask(this,new TeleportRunTask(this),0,10);
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event){
        if(event.getEntity() instanceof TeleportGunDropEntityItem teleportGunDropEntityItem){
            if(event.getPlayer().getInventory().getItemInHand().getId() == 0){
                event.getPlayer().getInventory().setItemInHand(teleportGunDropEntityItem.getItem());
                teleportGunDropEntityItem.close();
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event){
        if(event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR){

            if(event.getBlock().getId() != 0){
                return;
            }
            if(lockedPlayers.contains(event.getPlayer())){
                lockedPlayers.remove(event.getPlayer());
                return;
            }
            Item item = event.getItem();
            if(event.getItem() instanceof BaseTeleportGunItem){
                if(event.getPlayer().isSneaking()){
                    //TODO 打开设置页面
                    openSettingPanel(event.getPlayer(),event.getItem());

                    event.setCancelled(true);
                    return;
                }
                //打开传送门
                if(item.getDamage() >= item.getMaxDurability()){
                    //TODO 传送液不足
                    TeleportGunMainClass.sendMessageToObject("&c没有足够的传送液",event.getPlayer());
                    return;
                }
                if(openDoor(event.getPlayer(),event.getBlock(),event.getFace(),event.getItem(),false)){
                    item.setDamage(item.getDamage() + 1);
                    event.getPlayer().getInventory().setItemInHand(item);
                }

            }

        }

    }

    public void openSettingPanel(Player player,Item item){
        PlayerTeleportModelChoseForm playerTeleportModelChoseForm = new PlayerTeleportModelChoseForm("",player,item);
        formManager.addForm(player,playerTeleportModelChoseForm);
    }

    public static boolean openDoor(Player player,Block block,BlockFace face,Item item,boolean isInBlock){
        //先判断是否有设置终点
        String teleportLocation = null;
        if(item.hasCompoundTag()){
            CompoundTag tag = item.getNamedTag();
            if(tag.contains(ColumnManager.TELEPORT_LOCATION)){
                teleportLocation = tag.getString(ColumnManager.TELEPORT_LOCATION);
            }
        }
        if(teleportLocation == null){
            TeleportGunMainClass.sendMessageToObject("&c未设定传送地点 可潜行右键传送枪对设定地点进行传送",player);
            return false;
        }
        Location location = generateEndLocation(teleportLocation);
        if(location == null){
            TeleportGunMainClass.sendMessageToObject("&c传送地点不存在",player);
            return false;
        }

        if(isInBlock){
            boolean isXy = false;
            Position tpt;
            if(face == BlockFace.UP || face == BlockFace.DOWN){
                if(face == BlockFace.UP){
                    tpt = new Position(block.x + 0.5,block.y + 0.2,block.z  + 0.5,player.getLevel());
                }else{
                    tpt = new Position(block.x  + 0.5, block.y - 1.2,block.z  + 0.5,player.getLevel());
                }
            }else{
                isXy = true;
                BlockFace back = face.rotateY().rotateY();
                Block nPos = block.getSide(back);
                tpt = new Position(nPos.x + face.getXOffset() * 0.5f,nPos.y + 0.5f,nPos.z+ face.getYOffset() * 0.5f,player.getLevel());
            }
            GenerateParticleUtils.addDoorParticleXy(player.getLevel(),tpt,item instanceof CustomTeleportBlueItem,isXy);

            //还要在终点开启一个传送门

            GenerateParticleUtils.addDoorParticleXy(location.level,location.add(0,1),item instanceof CustomTeleportBlueItem,isXy);

            TeleportGunMainClass.INSTANCE.teleportItems.add(new TeleportItem(tpt,location));

        }else{
            Vector3 pos = GenerateParticleUtils.getPositionInFrontOfPlayer(player,2f);
            Block[] pt = player.getLineOfSight(2,1);
            double py =  player.getPosition().y;
            if(pt != null && pt.length > 0){
                py = pt[0].y;
            }
            Position tpt = new Position(pos.x,py + 0.5,pos.z,player.getLevel());
            GenerateParticleUtils.addDoorParticleXy(player.getLevel(),tpt,item instanceof CustomTeleportBlueItem,true);

            GenerateParticleUtils.addDoorParticleXy(location.level,location.add(0,1),item instanceof CustomTeleportBlueItem,true);

            TeleportGunMainClass.INSTANCE.teleportItems.add(new TeleportItem(tpt,location));
        }
        return true;

    }

    private static Location generateEndLocation(String teleportLocation) {

        Position position = GenerateParticleUtils.asPosition(teleportLocation);
        if(position == null){
            return null;
        }
        Location location = position.getLocation();
        if(!location.level.isChunkLoaded(location.getChunkX(),location.getChunkZ())){
            location.level.loadChunk(location.getChunkX(),location.getChunkZ());
        }
        return location;
    }

    public static void sendMessageToObject(String msg, Object o){
        String message = TextFormat.colorize('&',"&7[&e传送枪&7]"+" &r"+msg);
        if(o != null){
            if(o instanceof Player){
                if(((Player) o).isOnline()) {
                    ((Player) o).sendMessage(message);
                    return;
                }
            }
            if(o instanceof EntityHuman){
                message = ((EntityHuman) o).getName()+"->"+message;
            }
        }
        TeleportGunMainClass.INSTANCE.getLogger().info(message);

    }

    public static void sendMessageToConsole(String msg){
        sendMessageToObject(msg,null);
    }


    @EventHandler
    public void onFormListener(PlayerFormRespondedEvent event){
        if (event.wasClosed()) {
            return;
        }
        Player player = event.getPlayer();
        ICustomForm<? extends FormResponse> customForm = formManager.getFrom(player.getName());
        if(!player.isOnline()){
            return;
        }
        if(customForm != null && event.getFormID() == customForm.getFormId()){
            FormResponse response = event.getResponse();
            if(response != null) {
                try {
                    customForm.callbackData(response);
                } catch (ClassCastException e) {
                    System.err.println("表单响应类型不匹配: " + e.getMessage());
                }
            }
        }

    }

    @EventHandler
    public void onInventoryTransaction(InventoryTransactionEvent event){
        InventoryTransaction transaction = event.getTransaction();
        for (InventoryAction action : transaction.getActions()) {
            for (Inventory inventory : transaction.getInventories()) {
                if(inventory instanceof PlayerInventory){
                    Item i = action.getSourceItem();
                    if(i instanceof BaseTeleportGunItem){
                        Player player = (Player) ((PlayerInventory) inventory).getHolder();
                        Item hand = player.getCursorInventory().getItem(0);
                        if(hand instanceof CustomTeleportWaterItem){
                            event.setCancelled();
                            Item cl = i.clone();
                            player.getCursorInventory().removeItem(hand);
                            player.getInventory().removeItem(i);
                            cl.setDamage(0);
                            int index = getItemIndex(player.getInventory(),i);
                            if(index != -1){
                                player.getInventory().setItem(index,cl);
                            }else{
                                player.getInventory().addItem(cl);
                            }
                            sendMessageToObject("&a传送液已注入",player);

                        }
                    }
                }
            }
        }
    }

    public int getItemIndex(PlayerInventory inventory,Item item){
        for(Map.Entry<Integer, Item> entry : inventory.getContents().entrySet()){
            if(entry.getValue().equals(item,true,true)){
                return entry.getKey();
            }
        }
        return -1;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemSpawn(ItemSpawnEvent event){
        if(event.getEntity().getItem() instanceof BaseTeleportGunItem){
            EntityItem entityItem = event.getEntity();
            event.getEntity().close();
            TeleportGunDropEntityItem item = new TeleportGunDropEntityItem(entityItem.chunk,entityItem.namedTag);
            item.spawnToAll();

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntitySpawn(EntityTeleportEvent event){
        //传送门终点不允许其他插件更改
        if(teleportedEntities.containsKey(event.getEntity().getUniqueId())){
            //检测传送点
            Position end = teleportedEntities.get(event.getEntity().getUniqueId());
            if(!end.equals(event.getTo())){
                //传送到了
                event.setTo(teleportedEntities.get(event.getEntity().getUniqueId()).getLocation());
            }
        }
    }

}
