/*
 * Copyright 2018 MovingBlocks
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
package org.terasology.gooeyDefence.towerBlocks.effectors;

import org.terasology.gooeyDefence.towerBlocks.EffectCount;
import org.terasology.gooeyDefence.towerBlocks.EffectDuration;
import org.terasology.gooeyDefence.towerBlocks.base.TowerEffector;

/**
 * Slows down an enemy.
 * It does not deal any damage to the enemy.
 *
 * @see IceEffectorSystem
 */
public class IceEffectorComponent extends TowerEffector {
    /**
     * A multiplier for the enemies speed.
     * 0.9 will make them 10% slower, or 90% of their full speed;
     */
    private float slow;

    public float getSlow() {
        return slow;
    }

    @Override
    public EffectCount getEffectCount() {
        return EffectCount.CONTINUOUS;
    }

    @Override
    public EffectDuration getEffectDuration() {
        return EffectDuration.LASTING;
    }
}
