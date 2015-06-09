package nars.nal.stamp;

import nars.Memory;
import nars.budget.DirectBudget;
import nars.nal.Sentence;
import nars.nal.Task;
import nars.nal.nal7.Tense;
import nars.nal.nal8.Operation;
import nars.nal.task.TaskSeed;
import nars.nal.term.Compound;

/**
 * applies Stamp information to a sentence. default IStamp implementation.
 */
public class Stamper<C extends Compound> extends DirectBudget implements Stamp, StampEvidence, AbstractStamper {

    /**
     * serial numbers. not to be modified after Stamp constructor has initialized it
     */
    public long[] evidentialBase = null;

    /**
     * duration (in cycles) which any contained intervals are measured by
     */
    protected int duration;
    /**
     * creation time of the stamp
     */
    private long creationTime;

    /**
     * estimated occurrence time of the event*
     */
    protected long occurrenceTime;

//    /**
//     * used when the occurrence time cannot be estimated, means "unknown"
//     */
    //public static final long UNKNOWN = Integer.MAX_VALUE;


    /**
     * optional first parent stamp
     */
    public Stamp a = null;

    /**
     * optional second parent stamp
     */
    public Stamp b = null;

    private long[] evidentialSetCached;

    @Deprecated public Stamper(final Memory memory, final Tense tense) {
        this(memory, memory.time(), tense);
    }

    @Deprecated public Stamper(final Memory memory, long creationTime, final Tense tense) {
        this(memory, creationTime, Stamp.getOccurrenceTime(creationTime, tense, memory.duration()));
    }

    public Stamper(Operation operation, Memory memory, Tense tense) {
        this(operation.getTask().sentence, memory, tense);
    }
    public Stamper(Sentence s, Memory memory, Tense tense) {
        this(s, s.getCreationTime(), Stamp.getOccurrenceTime(s.getCreationTime(), tense, memory.duration()));
    }

    public Stamper(final Memory memory, long creationTime, final long occurenceTime) {
        this.duration = memory.duration();
        this.creationTime = creationTime;
        this.occurrenceTime = occurenceTime;
    }

    public Stamper(long[] evidentialBase, Stamp a, Stamp b, long creationTime, long occurrenceTime, int duration) {
        this.a = a;
        this.b = b;
        this.creationTime = creationTime;
        this.occurrenceTime = occurrenceTime;
        this.duration = duration;
        this.evidentialBase = evidentialBase;
    }

    public Stamper(Memory memory, long occurrence) {
        this(memory, memory.time(), occurrence);
    }

    public Stamper(Stamp s, long occ) {
        this(s, s.getOccurrenceTime(), occ);
    }
    public Stamper(Task task, long occ) {
        this(task.sentence, occ);
    }

    public Stamper() {
        super();
    }

    public Stamper clone() {
        return new Stamper(evidentialBase, a, b, creationTime, occurrenceTime, duration);
    }

    public Stamper cloneEternal() {
        return new Stamper(evidentialBase, a, b, creationTime, Stamp.ETERNAL, duration);
    }

    public Stamper(long[] evidentialBase, long creationTime, long occurrenceTime, int duration) {
        this(evidentialBase, null, null, creationTime, occurrenceTime, duration);
    }

    public Stamper(Stamp a, long creationTime, long occurrenceTime) {
        this(a, null, creationTime, occurrenceTime);
    }

    public Stamper(Stamp a, Stamp b, long creationTime, long occurrenceTime) {
        this.a = a;
        this.b = b;
        this.creationTime = creationTime;
        this.occurrenceTime = occurrenceTime;
    }

    @Deprecated
    public Stamper<C> setOccurrenceTime(long occurrenceTime) {
        this.occurrenceTime = occurrenceTime;
        return this;
    }

    @Override
    public Stamp setDuration(int d) {
        this.duration = d;
        return this;
    }

    @Override
    public Stamp setEvidentialBase(long[] b) {
        this.evidentialBase = b;
        return this;
    }


    public Stamper<C> setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public Stamper<C> setEternal() {
        return setOccurrenceTime(Stamp.ETERNAL);
    }



    @Override public void applyToStamp(final Stamp target) {

        target.setDuration(getDuration())
              .setTime(getCreationTime(), getOccurrenceTime())
              .setEvidence(getEvidentialBase(), evidentialSetCached);

    }

    @Override
    public long getCreationTime() {
        return 0;
    }

    public int getDuration() {
        if (this.duration == 0) {
            if (b!=null)
                return (this.duration = b.getDuration());
            else if (a!=null)
                return (this.duration = a.getDuration());
            else
                throw new RuntimeException("Unknown duration");
        }
        return duration;
    }

    @Override
    public long getOccurrenceTime() {
        return occurrenceTime;
    }

    @Override
    public Stamp cloneWithNewCreationTime(long newCreationTime) {
        throw new UnsupportedOperationException("Not impl");
    }

    @Override
    public Stamp cloneWithNewOccurrenceTime(long newOcurrenceTime) {
        throw new UnsupportedOperationException("Not impl");
    }

    @Override
    public long[] getEvidentialSet() {
        updateEvidence();
        return evidentialSetCached;
    }

    public long[] getEvidentialBase() {
        updateEvidence();
        return evidentialBase;
    }

    protected void updateEvidence() {
        if (evidentialBase == null) {
            Stamp p = null; //parent to inherit some properties from

            if ((a == null) && (b == null)) {
                //will be assigned a new serial
            } else if ((a != null) && (b != null)) {
                //evidentialBase = Stamp.zip(a.getEvidentialSet(), b.getEvidentialSet());
                evidentialBase = Stamp.zip(a.getEvidentialBase(), b.getEvidentialBase());
                p = a;
            }
            else if (a == null) p = b;
            else if (b == null) p = a;


            if (p!=null) {
                this.evidentialBase = p.getEvidentialBase();
                this.evidentialSetCached = p.getEvidentialSet();
            }
        }
    }


    public boolean isEternal() {
        return occurrenceTime == Stamp.ETERNAL;
    }



}
