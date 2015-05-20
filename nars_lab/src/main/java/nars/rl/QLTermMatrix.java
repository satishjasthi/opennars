package nars.rl;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import nars.Global;
import nars.NAR;
import nars.Symbols;
import nars.budget.Budget;
import nars.nal.DirectProcess;
import nars.nal.Task;
import nars.nal.concept.Concept;
import nars.nal.nal5.Implication;
import nars.nal.nal7.TemporalRules;
import nars.nal.term.Compound;
import nars.nal.term.Term;
import nars.util.index.ConceptMatrix;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;


/**
 * QL Agent coprocessor "Brain" operating in terms of (unprocessed) discrete Q-states
 * S = state
 * A = action
 */
abstract public class QLTermMatrix<S extends Term, A extends Term> extends ConceptMatrix<S,A, Implication, QEntry> {

    public AbstractHaiQBrain<S,A> brain;


    /** pending tasks to execute to prevent CME */
    transient private final List<Task> stateActionImplications = Global.newArrayList();

    final int implicationOrder = TemporalRules.ORDER_FORWARD; //TemporalRules.ORDER_FORWARD;

    /**
     * what type of state implication (q-entry) affected: belief (.) or goal (!)
     */
    char implicationPunctuation = Symbols.GOAL;
    float updateThresh = Global.TRUTH_EPSILON * 2; //seems to be better to aggregate them to a significant amount before generating a new belief otherwise it spams the belief tables


    float sensedStatePriorityChanged = 1.0f; //scales priority by this amount
    float sensedStatePrioritySame = 0.85f; //scales priority by this amount

    /**
     * min threshold of q-update necessary to cause an effect
     */


    //boolean updateEachFrame = false; //TODO specify as a frequency either in # per frame or cycle (ex: hz)

    //OPERATING PARAMETERS ------------------------
    /** confidence of update beliefs (a learning rate); set to zero to disable */
    float qUpdateConfidence = 0.25f;

    /** confidence of reward update beliefs; set to zero to disable reward beliefs */
    float rewardBeliefConfidence = 0.8f;

    /** confidence of reward command goal; set to zero to disable reward beliefs */
    float rewardGoalConfidence = 0.8f;

    /** confidence of state belief updates */
    @Deprecated protected float stateUpdateConfidence = 0.9f;

    //TODO belief update priority, durability etc
    //TODO reward goal priority, durability etc


    protected float actedGoalConfidence = 0; //set to 0 to disable qAutonomous
    protected float actedBeliefConfidence = 0; //set to 0 to disable qAutonomous
    float actionPriority = Global.DEFAULT_GOAL_PRIORITY;
    float actionDurability = Global.DEFAULT_GOAL_DURABILITY;


    WeakHashMap<S,Task> lastState = new WeakHashMap();



    public QLTermMatrix(NAR nar) {
        super(nar);


        brain = new AbstractHaiQBrain<S, A>() {


            @Override
            public double eligibility(S state, A action) {
                QEntry v = getEntry(state, action);
                if (v == null) return 0;
                return v.getE();
            }

            @Override
            public void qUpdate(S state, A action, double dqDivE, double eMult, double eAdd) {


                if (action == null) return;

                QEntry v = getEntry(state, action);
                if (v == null) {
                    //attempt conceptualization of the term
                    Concept c = nar.memory.conceptualize(
                            new Budget(0.8f, 0.8f, 0.8f),
                            qterm(state,action)
                    );
                    if (c != null) {
                        v = getEntry(c, state, action);
                    }

                    /*input(
                            nar.memory.newTask(qterm(state, action))
                                    .present()
                                    .judgment()
                                    .truth(0.5f, 0.5f)
                                    .get());*/
                }
                if (v != null) {
                    if (Double.isFinite(dqDivE) && Double.isFinite(eMult)) {
                        double e = v.getE();
                        v.addDQ(dqDivE * e);
                        v.updateE(eMult, eAdd);

                        v.commit(qUpdateConfidence, updateThresh);
                    }
                }
            }

            @Override
            public double q(S state, A action) {
                return QLTermMatrix.this.qSentence(state, action);
            }

//            @Deprecated @Override
//            protected A qlearn(S state, double reward, A nextAction, double confidence) {
//                //belief about current state
//                if (stateUpdateConfidence > 0) {
//
//                    //TODO avoid using String
//                    input(nar.task((state) + ". :|: %" + confidence + ";" + stateUpdateConfidence + "%"));
//                }
//
//                return super.qlearn(state, reward, nextAction, confidence);
//            }

            @Override
            public Iterable<S> getStates() {
                return rows;
            }

            @Override
            public Iterable<A> getActions() {
                return cols;
            }

            @Override
            public A getRandomAction() {
                final List<A> t = Lists.newArrayList(getActions());
                if (t.isEmpty()) return null;
                int n = (int)(Math.random() * t.size());
                return t.get(n);
            }

            public boolean isState(S s) { return QLTermMatrix.this.isRow(s); }
            public boolean isAction(A a) {
                return QLTermMatrix.this.isCol(a);
            }

        };

        brain.setAlpha(0.4f);
        brain.setEpsilon(0.1f);
        brain.setLambda(0.5f);

    }



    public double qSentence(final S state, final A action) {
        QEntry v = getEntry(state, action);
        if (v == null) return Double.NaN;
        return v.getQSentence(implicationPunctuation);
    }

    public double q(final S state, final A action) {
        return q(state, action, 0);
    }

    public double q(final S state, final A action, double deltaEligibility) {
        QEntry v = getEntry(state, action);
        if (v == null) return 0;
        if (deltaEligibility!=0) {
            v.addE(deltaEligibility);
        }
        return v.getQ();
    }




    public void setqUpdateConfidence(float qUpdateConfidence) {
        this.qUpdateConfidence = qUpdateConfidence;
    }

    public void init() {

    }


    @Override
    public boolean isEntry(Term x) {
        if (!(x instanceof Implication)) return false;
        if (x.getTemporalOrder() != implicationOrder) return false;


        Implication i = (Implication)x;
        return (isRow((S) i.getSubject()) && isCol((A) i.getPredicate()));
    }

    public S getRow(Implication t) {
        if (t.getTemporalOrder()!=implicationOrder) return null;
        S subj = (S)t.getSubject();
        if (isRow(subj)) return subj;
        return null;
    }

    public A getCol(Implication t) {
        if (t.getTemporalOrder()!=implicationOrder) return null;
        A pred = (A)t.getPredicate();
        if (isCol(pred)) return pred;
        return null;
    }


    public Implication qterm(S s, A a) {
        return Implication.make(s, a, implicationOrder);
        /*
        Implication i = termCache.get(s, a);
        if (i == null) {
            i = Implication.make(s, a, implicationOrder);
            if (i!=null)
                termCache.put(s, a, i);
        }
        return i;
        */
    }


    abstract public boolean isRow(Term s);
    abstract public boolean isCol(Term a);

    abstract public Term getRewardTerm();

    /**
     * this should return operations which call an operator that calls this instance's learn(state, reward) function at the end of its execution
     */
    //abstract public Operation getActionOperation(int s);



//    protected synchronized void qCommit() {
//        if (qUpdateConfidence == 0) return;
//
//
//        //input all dQ values
//        for (Table.Cell<S, A, QEntry> c : table.cellSet()) {
//            S state = c.getRowKey();
//            A action = c.getColumnKey();
//            Task t = qCommit(state, action, c.getValue());
//            if (t!=null)
//                stateActionImplications.add(t);
//        }
//
//        for (int i = 0; i< stateActionImplications.size(); i++) {
//            Task t = stateActionImplications.get(i);
//            input(t);
//        }
//        stateActionImplications.clear();
//    }



    public boolean stateChanged(Task newState) {
        S state = (S)newState.getTerm();
        Task last = lastState.get(state);
        boolean changed = (last == null ||
                !newState.getTruth().equals(last.getTruth())
                //TODO compare time?
                //TODO compare priority? (before it was scaled?)
        );
        lastState.put(state, newState);
        last = newState;
        return changed;
    }

    /** fire all actions (ex: to teach them at the beginning) */
    public void spontaneous(float goalConf) {
        spontaneous(cols, goalConf);
    }

    public void spontaneous(float goalConf, Iterable<String> actions) {
        spontaneous(Iterables.transform(actions, new Function<String, A>() {
            @Override
            public A apply(String t) {
                return (A) nar.term(t);
            }
        }), goalConf);
    }

    /** fire all actions (ex: to teach them at the beginning) */
    public void spontaneous(Iterable<A> actions, float goalConf) {
        for (A a : actions) {
            spontaneous(goalConf, a);
        }
    }

    /** fire all actions (ex: to teach them at the beginning) */
    public void spontaneous(Set<Map.Entry<A, Concept>> actions, float goalConf) {
        for (Map.Entry<A, Concept> a : actions) {
            spontaneous(goalConf, a.getKey());
        }
    }

    /** fire all actions (ex: to teach them at the beginning) */
    public void spontaneous(float goalConf, Term... actions) {
        for (Term a : actions) {
            acted(a, Symbols.GOAL, goalConf);
        }
    }


    public void acted(Term action) {
        acted(action, Symbols.GOAL, actedGoalConfidence);
        acted(action, Symbols.JUDGMENT, actedBeliefConfidence);
    }

    public void acted(Term action, char punctuation, float conf) {
        if (conf > 0)
            acted(action, punctuation, 1.0f, conf, actionPriority, actionDurability);
    }

    public void acted(Term action, char punctuation, float freq, float conf, float priority, float durability) {
        Task t = nar.memory.task((Compound) action).punctuation(punctuation).truth(freq, conf).budget(priority, durability).present().get();
        input(t);
    }

//    public void react() {
//        act(getNextAction(), Symbols.GOAL);
//    }



//    public Term learn( S state, final double reward, A nextAction, double confidence) {
//        believeReward((float) reward);
//        goalReward();
//
//        Term l = brain.learn(state, reward, nextAction, confidence);
//        qCommit();
//        return l;
//    }



    protected void goalReward() {
        //seek reward goal
        if (rewardGoalConfidence > 0) {
            input(nar.memory.task((Compound) getRewardTerm()).present().goal().truth(1.0f, rewardGoalConfidence).get());
        }
    }

    protected void believeReward(float reward) {
        //belief about current reward amount
        if (rewardBeliefConfidence > 0) {

            //expects -1..+1 as reward range input
            float rFreq = reward /2.0f + 0.5f;

            //clip reward to bounds
            if (rFreq < 0) rFreq = 0;
            if (rFreq > 1f) rFreq = 1f;

            input(nar.memory.task((Compound) getRewardTerm()).judgment().present().truth(rFreq, rewardGoalConfidence).get());
        }
    }

    public void setActedGoalConfidence(float qAutonomicGoalConfidence) {
        this.actedGoalConfidence = qAutonomicGoalConfidence;
    }

    public void setActedBeliefConfidence(float actedBeliefConfidence) {
        this.actedBeliefConfidence = actedBeliefConfidence;
    }

    protected void input(Task t) {
        //System.out.println("ql: " + t);
        DirectProcess.run(nar, t);
        //nar.input(t);
    }

}