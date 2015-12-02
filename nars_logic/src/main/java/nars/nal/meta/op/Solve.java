package nars.nal.meta.op;

import nars.Op;
import nars.Premise;
import nars.Symbols;
import nars.nal.RuleMatch;
import nars.nal.TaskRule;
import nars.nal.meta.BeliefFunction;
import nars.nal.meta.DesireFunction;
import nars.nal.meta.PreCondition;
import nars.nal.meta.TruthFunction;
import nars.nal.nal7.Sequence;
import nars.task.Task;
import nars.term.Statement;
import nars.term.Term;

/**
 * first resolution of the conclusion's pattern term
 */
public final class Solve extends PreCondition {

    public final Term term;
    @Deprecated public final TaskRule rule;

    private transient final String id;

    public Solve(Term term, TaskRule rule) {
        this.term = term;
        this.rule = rule;
        this.id = getClass().getSimpleName() + '(' + term + ')';
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean test(RuleMatch match) {

        Term derivedTerm;

        if(null==(derivedTerm=match.resolve(term)))
            return false;

        match.post.derivedTerm = derivedTerm;


        final Term pattern = rule.term(0);
        final Term taskpart = pattern.term(0);
        final Term beliefpart = pattern.term(1);

        Term possibleSequenceHolder = null;

        if (rule.sequenceIntervalsFromBelief) {
            possibleSequenceHolder = beliefpart;
        }
        if (rule.sequenceIntervalsFromTask) {
            possibleSequenceHolder = taskpart;
        }

        if (possibleSequenceHolder!=null && possibleSequenceHolder.hasAny(Op.SEQUENCE)) {
            processSequence(match, derivedTerm, possibleSequenceHolder);

        }

        return true;
    }

    public void processSequence(RuleMatch match, Term derivedTerm, Term toInvestigate) {
        int TermIsSequence = 1;
        int TermSubjectIsSequence = 2;
        int TermPredicateIsSequence = 3;

        int mode = 0; //nothing
        //int sequence_term_amount = 0;


        if (rule.sequenceIntervalsFromBelief || rule.sequenceIntervalsFromTask) {
            if (toInvestigate instanceof Sequence) {
                //sequence_term_amount = ((Sequence) toInvestigate).terms().length;
                mode = TermIsSequence;
            } else if (toInvestigate instanceof Statement) {
                Statement st = (Statement) toInvestigate;
                if (st.getSubject() instanceof Sequence) {
                    //sequence_term_amount = ((Sequence) st.getSubject()).terms().length;
                    mode = TermSubjectIsSequence;
                } else if (st.getPredicate() instanceof Sequence) {
                    //sequence_term_amount = ((Sequence) st.getPredicate()).terms().length;
                    mode = TermPredicateIsSequence;
                }
            }
        }

        int Nothing = 0;
        if (mode != Nothing) {

            Sequence paste = null; //where to paste it to

            //TODO: THIS CODE EXISTS TWICE WITH DIFFERENT PARAMETERS, PLACE1
            if (mode == TermIsSequence && derivedTerm instanceof Sequence) {
                paste = (Sequence) derivedTerm;
            } else if (mode == TermSubjectIsSequence && derivedTerm instanceof Statement && ((Statement) derivedTerm).getSubject() instanceof Sequence) {
                paste = (Sequence) ((Statement) derivedTerm).getSubject();
            } else if (mode == TermPredicateIsSequence && derivedTerm instanceof Statement && ((Statement) derivedTerm).getPredicate() instanceof Sequence) {
                paste = (Sequence) ((Statement) derivedTerm).getPredicate();
            }
            //END CODE

            Term lookat = null;
            Premise premise = match.premise;

            if (rule.sequenceIntervalsFromTask) {
                lookat = premise.getTask().getTerm();
            } else if (rule.sequenceIntervalsFromBelief) {
                lookat = premise.getBelief().getTerm();
            }

            //TODO: THIS CODE EXISTS TWICE WITH DIFFERENT PARAMETERS, PLACE2
            Sequence copy = null; //where to copy the interval data from
            if (mode == TermIsSequence && lookat instanceof Sequence) {
                copy = (Sequence) lookat;
            } else if (mode == TermSubjectIsSequence && lookat instanceof Statement && ((Statement) lookat).getSubject() instanceof Sequence) {
                copy = (Sequence) ((Statement) lookat).getSubject();
            } else if (mode == TermPredicateIsSequence && lookat instanceof Statement && ((Statement) lookat).getPredicate() instanceof Sequence) {
                copy = (Sequence) ((Statement) lookat).getPredicate();
            }
            //END CODE

            //ok now we can finally copy the intervals.

            if (copy!=null) {

                int[] copyIntervals = copy.intervals();

                if (paste != null) {



                    int a = copy.terms().length;
                    int b = paste.terms().length;
                    boolean sameLength = a == b;
                    boolean OneLess = a - 1 == b;

                    if (!sameLength && !OneLess) {
                        System.out.println("the case where the resulting sequence has less elements should not happen and needs to be analyzed!!");
                    }

                    int[] pasteIntervals = paste.intervals();

                    if (OneLess) {
                        match.post.occurence_shift = copyIntervals[1]; //we shift according to first interval
                        System.arraycopy(copyIntervals, 2, pasteIntervals, 1, copyIntervals.length - 2);
                    } else if (sameLength) {
                        System.arraycopy(copyIntervals, 0, pasteIntervals, 0, copyIntervals.length);
                    }
                } else /* if (paste == null)  */ {
                    //ok we reduced to a single element, so its a one less case
                    match.post.occurence_shift = copyIntervals[1];
                }
            }
        }
    }

    public final static class Truth extends PreCondition {
        public final BeliefFunction belief;
        public final DesireFunction desire;
        public final char puncOverride;

        transient private final String id;

        public Truth(BeliefFunction belief, DesireFunction desire, char puncOverride) {
            this.belief = belief;
            this.desire = desire;
            this.puncOverride = puncOverride;

            String beliefLabel = belief==null ? "_" : belief.toString();
            String desireLabel = desire==null ? "_" : desire.toString();

            this.id = (puncOverride == 0) ?
                    (getClass().getSimpleName() + "(" + beliefLabel + ", " + desireLabel + ")")  :
                    (getClass().getSimpleName() + "(" + beliefLabel + ", " + desireLabel + ", " + puncOverride + ")");

        }

        @Override
        public String toString() {
            return id;
        }

        TruthFunction getTruth(char punc) {

            switch (punc) {

                case Symbols.JUDGMENT:
                    return belief;

                case Symbols.GOAL:
                    return desire;

            /*case Symbols.QUEST:
            case Symbols.QUESTION:
            */

                default:
                    return null;
            }

        }

        @Override
        public boolean test(RuleMatch match) {

            Premise premise = match.premise;

            final Task task = premise.getTask();

            /** calculate derived task truth value */


            Task belief = premise.getBelief();


            final nars.truth.Truth T = task.getTruth();
            final nars.truth.Truth B = belief == null ? null : belief.getTruth();


            /** calculate derived task punctuation */
            char punct = puncOverride;
            if (punct == 0) {
                /** use the default policy determined by parent task */
                punct = task.getPunctuation();
            }


            final nars.truth.Truth truth;
            TruthFunction tf;

            if (punct == Symbols.JUDGMENT || punct == Symbols.GOAL) {
                tf = getTruth(punct);
                if (tf == null)
                    return false;

                truth = tf.get(T, B);

                if (truth == null) {
                    //no truth value function was applicable but it was necessary, abort
                    return false;
                }
            } else {
                //question or quest, no truth is involved
                truth = null;
                tf = null;
            }

            /** eliminate cyclic double-premise results
             *  TODO move this earlier to precondition check, or change to altogether new policy
             */
            final boolean single = (belief == null);
            if ((!single) && (RuleMatch.cyclic(tf, premise))) {
//                if (Global.DEBUG && Global.DEBUG_REMOVED_CYCLIC_DERIVATIONS) {
//                    match.removeCyclic(outcome, premise, truth, punct);
//                }
                return false;
            }

            RuleMatch.PostMods post = match.post;
            post.truth = truth;
            post.punct = punct;

            return true;
        }
    }

}