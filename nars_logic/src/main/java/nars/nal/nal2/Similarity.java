/*
 * Similarity.java
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
package nars.nal.nal2;

import nars.Op;
import nars.term.Statement;
import nars.term.Term;

/**
 * A Statement about a Similarity relation.
 */
public class Similarity extends Statement {

    /**
     * Constructor with partial values, called by make
     */
    protected Similarity(final Term subj, final Term pred) {
        super();
        init(subj, pred);
    }


    /**
     * Try to make a new compound from two term. Called by the logic rules.
     * @param subject The first component
     * @param predicate The second component
     * @return A compound generated or null
     */
    public static Term make(final Term subject, final Term predicate) {

        if (invalidStatement(subject, predicate)) {
            return null;
        }

        int compare = subject.compareTo(predicate);
        if (compare > 0)
            return new Similarity(predicate, subject);
        else if (compare < 0)
            return new Similarity(subject, predicate);
        else {
            throw new RuntimeException("subject and predicate are equal according to compareTo: " + subject + " , " + predicate);
        }

    }

    /**
     * Get the operate of the term.
     * @return the operate of the term
     */
    @Override
    public final Op op() {
        return Op.SIMILARITY;
    }

    /**
     * Check if the compound is commutative.
     * @return true for commutative
     */
    @Override
    public final boolean isCommutative() {
        return true;
    }
}
