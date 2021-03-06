package nars.concept;

import nars.Memory;
import nars.budget.Budget;
import nars.budget.Item;
import nars.term.Term;
import nars.truth.Stamp;

import java.util.Map;

/**
 * Created by me on 7/29/15.
 */
public abstract class AbstractConcept extends Item<Term> implements Concept {

    private final Term term;

    long creationTime = Stamp.TIMELESS;
    protected Map meta = null;
    protected boolean constant = false;

    @Deprecated protected transient Memory memory;

    //@Deprecated final static Variable how = new Variable("?how");

    public AbstractConcept(final Term term) {
        super(Budget.zero);
        this.term = term;
    }

    @Override
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * metadata table where processes can store and retrieve concept-specific data by a key. lazily allocated
     */
    @Override
    final public Map<Object, Object> getMeta() {
        return meta;
    }

    @Override
    @Deprecated public void setMeta(Map<Object, Object> meta) {
        this.meta = meta;
    }

    /**
     * Reference to the memory to which the Concept belongs
     */
    @Override
    final public Memory getMemory() {
        return memory;
    }

    public final void setMemory(Memory memory) {
        this.memory = memory;
        if (memory!=null) {
            if (this.creationTime == Stamp.TIMELESS) {
                this.creationTime = memory.time();
            }
        }
    }

    /**
     * The term is the unique ID of the concept
     */
    @Override
    final public Term getTerm() {
        return term;
    }

    @Override
    final public long getCreationTime() {
        return creationTime;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Concept)) return false;
        return ((Concept) obj).getTerm().equals(getTerm());
    }

    @Override
    final public int hashCode() {
        return getTerm().hashCode();
    }

    @Override
    final public Term name() {
        return getTerm();
    }

    /**
     * Return a string representation of the concept, called in ConceptBag only
     *
     * @return The concept name, with taskBudget in the full version
     */
    @Override
    final public String toString() {  // called from concept bag
        //return (super.toStringBrief() + " " + key);
        //return super.toStringExternal();
        return getTerm().toString();
    }

    /** called by memory, dont call self or otherwise */
    @Override public boolean delete() {
        /*if (getMemory().inCycle())
            throw new RuntimeException("concept " + this + " attempt to delete() during an active cycle; must be done between cycles");
        */
        if (!super.delete())
            return false;

        if (getMeta() != null) {
            getMeta().clear();
            setMeta(null);
        }
        return true;
    }


    @Override
    final public boolean isConstant() {
        return constant;
    }

    @Override
    final public boolean setConstant(boolean b) {
        this.constant = b;
        return constant;
    }
}
