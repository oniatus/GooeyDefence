/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.gooeyDefence.worldGeneration.rasterizers;

import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.worldGeneration.facets.DefenceFieldFacet;
import org.terasology.math.ChunkMath;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;

import java.util.Map;

/**
 * Places blocks according to the values set in {@link org.terasology.gooeyDefence.worldGeneration.providers.DefenceFieldProvider}.
 * Any block close to the entrance, including those in the mini dome, are set to a different block than the rest of the dome.
 */
public class DefenceFieldRasterizer implements WorldRasterizer {
    private Block block;
    private Block altBlock;

    @Override
    public void initialize() {
        block = CoreRegistry.get(BlockManager.class).getBlock("GooeyDefence:WorldBlock");
        altBlock = CoreRegistry.get(BlockManager.class).getBlock("GooeyDefence:AltWorldBlock");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        DefenceFieldFacet fieldFacet = chunkRegion.getFacet(DefenceFieldFacet.class);
        for (Map.Entry<Vector3i, Boolean> entry : fieldFacet.getWorldEntries().entrySet()) {
            if (entry.getValue()) {
                Vector3i pos = entry.getKey();
                if ((int) DefenceField.distanceToNearestEntrance(pos) < DefenceField.entranceRingSize() + 2) {
                    chunk.setBlock(ChunkMath.calcBlockPos(pos), altBlock);
                } else {
                    chunk.setBlock(ChunkMath.calcBlockPos(pos), block);
                }
            }
        }
    }
}

