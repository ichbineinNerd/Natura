package com.progwml6.natura.overworld.block.leaves;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.progwml6.natura.common.block.base.BlockLeavesBase;
import com.progwml6.natura.library.NaturaRegistry;
import com.progwml6.natura.overworld.NaturaOverworld;
import com.progwml6.natura.overworld.block.logs.BlockOverworldLog2;
import com.progwml6.natura.overworld.block.logs.BlockOverworldLog2.LogType;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOverworldLeaves2 extends BlockLeavesBase
{
    public BlockOverworldLeaves2()
    {
        this.setCreativeTab(NaturaRegistry.tabWorld);

        Blocks.FIRE.setFireInfo(this, 30, 60);

        this.setDefaultState(this.blockState.getBaseState().withProperty(CHECK_DECAY, false).withProperty(DECAYABLE, true));
    }

    @Override
    public void updateTick(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, Random rand)
    {
        super.updateTick(worldIn, pos, state, rand);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
    {
        for (LogType type : LogType.values())
        {
            list.add(new ItemStack(this, 1, this.getMetaFromState(this.getDefaultState().withProperty(BlockOverworldLog2.TYPE, type))));
        }
    }

    @Override
    protected int getSaplingDropChance(IBlockState state)
    {
        return 25;
    }

    // sapling item
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(NaturaOverworld.overworldSapling2);
    }

    @Override
    protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance)
    {
    }

    // sapling meta
    @Override
    public int damageDropped(IBlockState state)
    {
        return (state.getValue(BlockOverworldLog2.TYPE)).ordinal() & 3; // only first 2 bits
    }

    // item dropped on silktouching
    @Override
    protected ItemStack getSilkTouchDrop(@Nonnull IBlockState state)
    {
        return new ItemStack(Item.getItemFromBlock(this), 1, (state.getValue(BlockOverworldLog2.TYPE)).ordinal() & 3);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockOverworldLog2.TYPE, CHECK_DECAY, DECAYABLE);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        int type = meta % 4;
        if (type < 0 || type >= LogType.values().length)
        {
            type = 0;
        }
        LogType logtype = LogType.values()[type];
        return this.getDefaultState()
                .withProperty(BlockOverworldLog2.TYPE, logtype)
                .withProperty(DECAYABLE, (meta & 4) == 0)
                .withProperty(CHECK_DECAY, (meta & 8) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int meta = (state.getValue(BlockOverworldLog2.TYPE)).ordinal() & 3; // only first 2 bits

        if (!state.getValue(DECAYABLE))
        {
            meta |= 4;
        }

        if (state.getValue(CHECK_DECAY))
        {
            meta |= 8;
        }

        return meta;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        IBlockState state = world.getBlockState(pos);
        return Lists.newArrayList(this.getSilkTouchDrop(state));
    }

    @Override
    public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return !NaturaOverworld.proxy.fancyGraphicsEnabled();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return NaturaOverworld.proxy.fancyGraphicsEnabled() ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.SOLID;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return !NaturaOverworld.proxy.fancyGraphicsEnabled() && blockAccess.getBlockState(pos.offset(side)).getBlock() == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
}
