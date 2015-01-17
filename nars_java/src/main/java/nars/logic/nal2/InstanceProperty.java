/*
 * InstanceProperty.java
 *
 * Copyright (C) 2008  Pei Wang
 *
 * This file is part of Open-NARS.
 *
 * Open-NARS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Open-NARS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Open-NARS.  If not, see <http://www.gnu.org/licenses/>.
 */

package nars.logic.nal2;

import nars.logic.entity.Term;
import nars.logic.nal1.Inheritance;
import nars.logic.nal3.SetExt;
import nars.logic.nal3.SetInt;

/**
 * A Statement about an InstanceProperty relation, which is used only in Narsese for I/O, 
 * and translated into Inheritance for internal use.
 */
public abstract class InstanceProperty /*extends Statement*/ {
    
    /**
     * Try to make a new compound from two components. Called by the logic rules.
     * <p>
     *  A {-] B becomes {A} --> [B]
     * @param subject The first component
     * @param predicate The second component
     * @param memory Reference to the memory
     * @return A compound generated or null
     */
    final public static Inheritance make(final Term subject, final Term predicate) {
        return Inheritance.make(new SetExt(subject), new SetInt(predicate));
    }
}