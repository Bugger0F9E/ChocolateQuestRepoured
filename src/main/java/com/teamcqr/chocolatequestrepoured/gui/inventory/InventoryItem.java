package com.teamcqr.chocolatequestrepoured.gui.inventory;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

public class InventoryItem extends InventoryBasic
{
	private ItemStack stack;
	
	public InventoryItem(ItemStack stack, ITextComponent title, int slotCount)
	{
		super(title, slotCount);
		
		this.stack = stack;
		this.readFromNBT();
		this.writeToNBT();
	}
	
	@Override
    public void markDirty()
    {
    	writeToNBT();
	    super.markDirty();
    }
    
    public void writeToNBT()
    {
    	NBTTagCompound tag = this.getTagCompound(stack);
    	
    	NBTTagList itemList = new NBTTagList();
    	
    	for(int i = 0; i < this.getSizeInventory(); i++)
    	{
    		ItemStack stack = this.getStackInSlot(i);
    		
    		if(stack != null)
    		{
    			NBTTagCompound slotTag = new NBTTagCompound();
    			slotTag.setInteger("Index", i);
    			stack.writeToNBT(slotTag);
    			itemList.appendTag(slotTag);
    		}
    	}
    	tag.setTag("Items", itemList);
    }
    
    public void readFromNBT()
    {
    	NBTTagCompound tag = this.getTagCompound(stack);
    	
    	if(!tag.hasKey("Items"))
    	{
    		writeToNBT();
    		return;
    	}
    	
    	NBTTagList list = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
    	
    	if(list == null)
    	{
    		writeToNBT();
    		return;
    	}
    	
    	for(int i = 0; i < list.tagCount(); i++)
    	{
    		NBTTagCompound entry = list.getCompoundTagAt(i);
    		int index = entry.getInteger("Index");
    		setInventorySlotContents(index, new ItemStack(entry));
    	}
    }
    
    public NBTTagCompound getTagCompound(ItemStack stack)
    {
    	if(stack.getTagCompound() == null)
    	{
    		NBTTagCompound tag = new NBTTagCompound();
    		stack.setTagCompound(tag);
    	}
    	
    	return stack.getTagCompound();
    }
}