/**
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package buildcraft.core;

import buildcraft.api.core.Position;
import buildcraft.core.proxy.CoreProxy;

import net.minecraft.src.Entity;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public class EntityLaser extends Entity {

	protected Position head, tail;

	public double renderSize = 0;
	public double angleY = 0;
	public double angleZ = 0;
	protected String texture;

	public EntityLaser(World world) {
		super(world);

		initClientSide();
	}

	public EntityLaser(World world, Position head, Position tail) {

		super(world);

		this.head = head;
		this.tail = tail;

		setPositionAndRotation(head.x, head.y, head.z, 0, 0);

		initServerSide();
	}

	protected void initClientSide() {
		
		head = new Position(0, 0, 0);
		tail = new Position(0, 0, 0);
		
		dataWatcher.addObject(8 , Integer.valueOf(0));
		dataWatcher.addObject(9 , Integer.valueOf(0));
		dataWatcher.addObject(10, Integer.valueOf(0));
		dataWatcher.addObject(11, Integer.valueOf(0));
		dataWatcher.addObject(12, Integer.valueOf(0));
		dataWatcher.addObject(13, Integer.valueOf(0));

		dataWatcher.addObject(14, Byte.valueOf((byte) 0));
		
	}
	
	protected void initServerSide() {

		preventEntitySpawning = false;
		noClip = true;
		isImmuneToFire = true;

		setPositionAndRotation(head.x, head.y, head.z, 0, 0);
		setSize(10, 10);

		dataWatcher.addObject(8 , Integer.valueOf(encodeDouble(head.x)));
		dataWatcher.addObject(9 , Integer.valueOf(encodeDouble(head.y)));
		dataWatcher.addObject(10, Integer.valueOf(encodeDouble(head.z)));
		dataWatcher.addObject(11, Integer.valueOf(encodeDouble(tail.x)));
		dataWatcher.addObject(12, Integer.valueOf(encodeDouble(tail.y)));
		dataWatcher.addObject(13, Integer.valueOf(encodeDouble(tail.z)));

		dataWatcher.addObject(14, Byte.valueOf((byte) 0));
	}

	@Override
	public void onUpdate() {

		if (head == null || tail == null)
			return;

		if (CoreProxy.proxy.isRenderWorld(worldObj))
			updateData();

		boundingBox.minX = Math.min(head.x, tail.x);
		boundingBox.minY = Math.min(head.y, tail.y);
		boundingBox.minZ = Math.min(head.z, tail.z);

		boundingBox.maxX = Math.max(head.x, tail.x);
		boundingBox.maxY = Math.max(head.y, tail.y);
		boundingBox.maxZ = Math.max(head.z, tail.z);

		boundingBox.minX--;
		boundingBox.minY--;
		boundingBox.minZ--;

		boundingBox.maxX++;
		boundingBox.maxY++;
		boundingBox.maxZ++;

		double dx = head.x - tail.x;
		double dy = head.y - tail.y;
		double dz = head.z - tail.z;

		renderSize = Math.sqrt(dx * dx + dy * dy + dz * dz);
		angleZ = 360 - (Math.atan2(dz, dx) * 180.0 / Math.PI + 180.0);
		dx = Math.sqrt(renderSize * renderSize - dy * dy);
		angleY = -Math.atan2(dy, dx) * 180 / Math.PI;
	}

	protected void updateData() {

		head.x = decodeDouble(dataWatcher.getWatchableObjectInt(8));
		head.y = decodeDouble(dataWatcher.getWatchableObjectInt(9));
		head.z = decodeDouble(dataWatcher.getWatchableObjectInt(10));
		tail.x = decodeDouble(dataWatcher.getWatchableObjectInt(11));
		tail.y = decodeDouble(dataWatcher.getWatchableObjectInt(12));
		tail.z = decodeDouble(dataWatcher.getWatchableObjectInt(13));
	}

	public void setPositions(Position head, Position tail) {

		this.head = head;
		this.tail = tail;

		setPositionAndRotation(head.x, head.y, head.z, 0, 0);

		dataWatcher.updateObject(8 , Integer.valueOf(encodeDouble(head.x)));
		dataWatcher.updateObject(9 , Integer.valueOf(encodeDouble(head.y)));
		dataWatcher.updateObject(10, Integer.valueOf(encodeDouble(head.z)));
		dataWatcher.updateObject(11, Integer.valueOf(encodeDouble(tail.x)));
		dataWatcher.updateObject(12, Integer.valueOf(encodeDouble(tail.y)));
		dataWatcher.updateObject(13, Integer.valueOf(encodeDouble(tail.z)));

		onUpdate();
	}

	public void show() {
		dataWatcher.updateObject(14, Byte.valueOf((byte) 1));
	}

	public void hide() {
		dataWatcher.updateObject(14, Byte.valueOf((byte) 0));
	}

	public boolean isVisible() {
		return dataWatcher.getWatchableObjectByte(14) == 0 ? false : true;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public String getTexture() {
		return texture;
	}

	private int encodeDouble(double d) {
		return (int) (d * 8000);
	}

	private double decodeDouble(int i) {
		return (i / 8000D);
	}

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {

		double headX = nbt.getDouble("headX");
		double headY = nbt.getDouble("headZ");
		double headZ = nbt.getDouble("headY");
		head = new Position(headX, headY, headZ);

		double tailX = nbt.getDouble("tailX");
		double tailY = nbt.getDouble("tailZ");
		double tailZ = nbt.getDouble("tailY");
		tail = new Position(tailX, tailY, tailZ);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {

		nbt.setDouble("headX", head.x);
		nbt.setDouble("headY", head.y);
		nbt.setDouble("headZ", head.z);

		nbt.setDouble("tailX", tail.x);
		nbt.setDouble("tailY", tail.y);
		nbt.setDouble("tailZ", tail.z);
	}
}
