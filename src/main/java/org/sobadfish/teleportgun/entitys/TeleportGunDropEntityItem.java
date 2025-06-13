package org.sobadfish.teleportgun.entitys;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.custom.CustomEntity;
import cn.nukkit.entity.custom.EntityDefinition;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.ItemDespawnEvent;
import cn.nukkit.event.entity.ItemSpawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import org.sobadfish.teleportgun.customitem.CustomTeleportBlueItem;

/**
 * 掉落物实体 只是沿用运动逻辑..
 *
 * */
public class TeleportGunDropEntityItem extends Entity implements CustomEntity {

    public Item item;

    protected String owner;
    protected String thrower;
    protected int pickupDelay;
    protected boolean floatsInLava;


    public static final EntityDefinition DEF_BLUE =
            EntityDefinition
                    .builder()
                    .identifier("teleport_gun:blue_entity_item")
                    //.summonable(true)
                    .spawnEgg(true)
                    .implementation(TeleportGunDropEntityItem.class)
                    .build();

    public static final EntityDefinition DEF_GREEN =
            EntityDefinition
                    .builder()
                    .identifier("teleport_gun:green_entity_item")
                    //.summonable(true)
                    .spawnEgg(true)
                    .implementation(TeleportGunDropEntityItem.class)
                    .build();


    @Override
    public int getNetworkId() {
        return this.getEntityDefinition().getRuntimeId();
    }

    public TeleportGunDropEntityItem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.item = NBTIO.getItemHelper(this.namedTag.getCompound("Item"));
    }

    @Override
    public EntityDefinition getEntityDefinition() {
        if(getItem() instanceof CustomTeleportBlueItem){
            return DEF_BLUE;
        }else{
            return DEF_GREEN;
        }

    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(5);
        this.setHealth((float)this.namedTag.getShort("Health"));
        if (this.namedTag.contains("Age")) {
            this.age = this.namedTag.getShort("Age");
        }

        if (this.namedTag.contains("PickupDelay")) {
            this.pickupDelay = this.namedTag.getShort("PickupDelay");
        }

        if (this.namedTag.contains("Owner")) {
            this.owner = this.namedTag.getString("Owner");
        }

        if (this.namedTag.contains("Thrower")) {
            this.thrower = this.namedTag.getString("Thrower");
        }

        if (!this.namedTag.contains("Item")) {
            this.close();
        } else {
            this.item = NBTIO.getItemHelper(this.namedTag.getCompound("Item"));
            int id = this.item.getId();
            if (id >= 742 && id <= 752) {
                this.fireProof = true;
                this.floatsInLava = true;
            }

        }
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.5f;
    }

    public Item getItem() {
        return item;
    }


    public float getLength() {
        return 0.25F;
    }


    public float getGravity() {
        return 0.04F;
    }

    public float getDrag() {
        return 0.02F;
    }

    public boolean attack(EntityDamageEvent source) {
        EntityDamageEvent.DamageCause cause = source.getCause();
        if ((cause == EntityDamageEvent.DamageCause.VOID || cause == EntityDamageEvent.DamageCause.CONTACT || cause == EntityDamageEvent.DamageCause.FIRE_TICK || (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) && !this.isInsideOfWater() && (this.item == null || this.item.getId() != 399)) && super.attack(source)) {
            if (this.item != null && !this.isAlive()) {
                int id = this.item.getId();
                if (id != 218 && id != 205) {
                    return true;
                } else {
                    CompoundTag nbt = this.item.getNamedTag();
                    if (nbt == null) {
                        return true;
                    } else {
                        ListTag<CompoundTag> items = nbt.getList("Items", CompoundTag.class);

                        for(int i = 0; i < items.size(); ++i) {
                            CompoundTag itemTag = (CompoundTag)items.get(i);
                            Item item = NBTIO.getItemHelper(itemTag);
                            if (!item.isNull()) {
                                this.level.dropItem(this, item);
                            }
                        }

                        return true;
                    }
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        } else {
            int tickDiff = currentTick - this.lastUpdate;
            if (tickDiff <= 0 && !this.justCreated) {
                return true;
            } else {
                this.lastUpdate = currentTick;
                if (!this.fireProof && this.isInsideOfFire()) {
                    this.close();
                    return true;
                } else {
                    boolean hasUpdate = this.entityBaseTick(tickDiff);
                    if (this.isAlive()) {
                        age = 0;
                        if (this.pickupDelay > 0 && this.pickupDelay < 32767) {
                            this.pickupDelay -= tickDiff;
                            if (this.pickupDelay < 0) {
                                this.pickupDelay = 0;
                            }
                        }


                        this.updateLiquidMovement();
                        if (this.checkObstruction(this.x, this.y, this.z)) {
                            hasUpdate = true;
                        }

                        this.move(this.motionX, this.motionY, this.motionZ);
                        double friction = (double)(1.0F - this.getDrag());
                        Block block = this.getLevel().getBlock(this.getFloorX(), (int)Math.floor(this.y - 1.0D), this.getFloorZ());
                        if ((this.onGround || block instanceof BlockLiquid) && (Math.abs(this.motionX) > 1.0E-5D || Math.abs(this.motionZ) > 1.0E-5D)) {
                            double frictionFactor;
                            if (block instanceof BlockLiquid) {
                                frictionFactor = 0.8D;
                            } else {
                                frictionFactor = block.getFrictionFactor();
                            }

                            friction *= frictionFactor;
                        }

                        this.motionX *= friction;
                        this.motionY *= (double)(1.0F - this.getDrag());
                        this.motionZ *= friction;
                        if (this.onGround) {
                            this.motionY *= -0.5D;
                        }

                        this.updateMovement();
                    }

                    return hasUpdate || !this.onGround || Math.abs(this.motionX) > 1.0E-5D || Math.abs(this.motionY) > 1.0E-5D || Math.abs(this.motionZ) > 1.0E-5D;
                }
            }
        }
    }

    private void updateLiquidMovement() {
        Block block = this.level.getBlock((int)this.x, (int)this.boundingBox.getMaxY(), (int)this.z);
        if (block.isLiquidSource()) {
            this.motionY -= (double)this.getGravity() * -0.015D;
        } else {
            Block floor = this.getLevelBlock();
            if (floor.isLiquidSource() || (floor = this.level.getBlock(floor, 1)).isLiquidSource()) {
                double height = floor.y + 1.0D - (double)((BlockLiquid)floor).getFluidHeightPercent() - 0.1111111D;
                if ((double)this.getEyeY() < height) {
                    this.motionY = (double)this.getGravity() - 0.06D;
                    return;
                }
            }

            this.motionY -= (double)this.getGravity();
        }
    }

    public void saveNBT() {
        super.saveNBT();
        if (this.item != null) {
            this.namedTag.putCompound("Item", NBTIO.putItemHelper(this.item, -1));
            this.namedTag.putShort("Health", (int)this.getHealth());
            this.namedTag.putShort("PickupDelay", this.pickupDelay);
            if (this.owner != null) {
                this.namedTag.putString("Owner", this.owner);
            }

            if (this.thrower != null) {
                this.namedTag.putString("Thrower", this.thrower);
            }
        }

    }

    public boolean entityBaseTick(int tickDiff) {
        this.collisionBlocks = null;
        this.justCreated = false;
        if (!this.isAlive()) {
            this.despawnFromAll();
            this.close();
            return false;
        } else {
            boolean hasUpdate = false;
            this.checkBlockCollision();
            if (this.y <= (double)(this.level.getMinBlockY() - 16) && this.isAlive()) {
                this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.VOID, 10.0F));
                hasUpdate = true;
            }

            if (this.fireTicks > 0) {
                if (this.fireProof) {
                    this.fireTicks -= tickDiff << 2;
                    if (this.fireTicks < 0) {
                        this.fireTicks = 0;
                    }
                } else {
                    if (this.fireTicks % 20 == 0 || tickDiff > 20) {
                        this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.FIRE_TICK, 1.0F));
                    }

                    this.fireTicks -= tickDiff;
                }

                if (this.fireTicks <= 0) {
                    this.extinguish();
                } else if (!this.fireProof) {
                    this.setDataFlag(0, 0, true);
                    hasUpdate = true;
                }
            }

            if (this.noDamageTicks > 0) {
                this.noDamageTicks -= tickDiff;
                if (this.noDamageTicks < 0) {
                    this.noDamageTicks = 0;
                }
            }

            this.age += tickDiff;
            return hasUpdate;
        }
    }

    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : (this.item.hasCustomName() ? this.item.getCustomName() : this.item.getName());
    }

    public boolean canCollideWith(Entity entity) {
        return false;
    }
}
