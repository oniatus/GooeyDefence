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
package org.terasology.gooeyDefence.towerBlocks.targeters;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.gooeyDefence.DefenceField;
import org.terasology.gooeyDefence.EnemyManager;
import org.terasology.gooeyDefence.components.enemies.PathComponent;
import org.terasology.gooeyDefence.health.HealthComponent;
import org.terasology.gooeyDefence.towerBlocks.SelectionMethod;
import org.terasology.gooeyDefence.towerBlocks.base.TowerTargeter;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A base system for tower targeters that provides common methods.
 */
public class BaseTargeterSystem extends BaseComponentSystem {

    /**
     * Picks the target from all within range based upon the selection method
     *
     * @param targets         All enemies within range
     * @param selectionMethod The selection method
     * @return The single target, according to the selection method
     */
    protected EntityRef getSingleTarget(Set<EntityRef> targets, SelectionMethod selectionMethod) {
        Comparator<EntityRef> comparator;
        switch (selectionMethod) {
            case RANDOM:
                List<EntityRef> listTargets = new ArrayList<>(targets);
                Collections.shuffle(listTargets);
                return listTargets.get(0);
            case WEAK:
                comparator = (first, second) -> {
                    HealthComponent firstComponent = first.getComponent(HealthComponent.class);
                    HealthComponent secondComponent = second.getComponent(HealthComponent.class);
                    return firstComponent.getHealth() - secondComponent.getHealth();
                };
                break;
            case FIRST:
                comparator = (first, second) -> {
                    PathComponent firstComponent = DefenceField.getComponentExtending(first, PathComponent.class);
                    PathComponent secondComponent = DefenceField.getComponentExtending(second, PathComponent.class);
                    return firstComponent.getStep() - secondComponent.getStep();
                };
                break;
            case STRONG:
                comparator = (first, second) -> {
                    HealthComponent firstComponent = first.getComponent(HealthComponent.class);
                    HealthComponent secondComponent = second.getComponent(HealthComponent.class);
                    return secondComponent.getHealth() - firstComponent.getHealth();
                };
                break;
            default:
                throw new EnumConstantNotPresentException(SelectionMethod.class, selectionMethod.toString());
        }
        Optional<EntityRef> chosenEnemy = targets.stream().min(comparator);
        return chosenEnemy.orElse(EntityRef.NULL);
    }

    /**
     * Checks if the enemy from last round can be reused.
     *
     * @param targeterPos       The position of the target
     * @param targeterComponent The targeter
     * @return True if the targeter can attack the enemy
     */
    protected boolean canUseTarget(EntityRef target, Vector3f targeterPos, TowerTargeter targeterComponent) {
        return target.exists() &&
                target.getComponent(LocationComponent.class)
                        .getWorldPosition()
                        .distance(targeterPos) < targeterComponent.getRange();
    }

    /**
     * Gets a single targetable enemy within the tower's range
     * <p>
     * Attempts to use the entity that was targeted last round.
     * If that is not possible it picks an enemy in range based on the selection method listed
     *
     * @param targeterPos       The position of the targeter block
     * @param targeterComponent The targeter component on the targeter
     * @param enemyManager      The enemy manager to use if a new enemy needs to be picked
     * @return A suitable enemy in range, or the null entity if none was found
     */
    protected EntityRef getTarget(Vector3f targeterPos, TowerTargeter targeterComponent, EnemyManager enemyManager) {
        EntityRef target = targeterComponent.getLastTarget();

        if (!canUseTarget(target, targeterPos, targeterComponent)) {
            Set<EntityRef> enemiesInRange = enemyManager.getEnemiesInRange(
                    targeterPos,
                    targeterComponent.getRange());
            target = getSingleTarget(enemiesInRange, targeterComponent.getSelectionMethod());
        }
        return target;
    }
}
