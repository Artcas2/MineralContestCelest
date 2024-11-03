package fr.synchroneyes.mapbuilder.Blocks;

import fr.synchroneyes.mapbuilder.Blocks.BlocksDataColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlocksColorChanger {
    public static void changeBlockColor(Block givenBlock, BlocksDataColor wantedColor) {
        if (givenBlock.getType().equals((Object)Material.GRAY_CONCRETE)) {
            return;
        }
        String[] wantedType = new String[]{"concrete", "terracotta", "glass"};
        String blockType = givenBlock.getType().toString().replace("Material.", "");
        String[] exploded_block_type = blockType.split("_");
        for (int indexExplodedBlockType = 0; indexExplodedBlockType < exploded_block_type.length; ++indexExplodedBlockType) {
            for (int indexWantedType = 0; indexWantedType < wantedType.length; ++indexWantedType) {
                if (!exploded_block_type[indexExplodedBlockType].equalsIgnoreCase(wantedType[indexWantedType])) continue;
                blockType = exploded_block_type[indexExplodedBlockType].equalsIgnoreCase("glass") ? exploded_block_type[indexExplodedBlockType - 1] + "_" + exploded_block_type[indexExplodedBlockType] : exploded_block_type[indexExplodedBlockType];
                blockType = (wantedColor.color + "_" + blockType).toUpperCase();
                System.out.println(blockType);
            }
        }
        Material newBlockType = Material.valueOf((String)blockType);
        givenBlock.setType(newBlockType);
        givenBlock.getState().getData().setData(Byte.parseByte(wantedColor.blockDataColor + ""));
    }
}

