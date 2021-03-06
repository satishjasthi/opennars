//package nars.meter;
//
//import nars.NAR;
//import nars.event.NARReaction;
//import nars.process.ConceptProcess;
//import nars.task.Task;
//import nars.util.event.On;
//
//import java.util.Collection;
//
///**
// * Meter utility for analyzing useless inference processes
// * --no derivations
// * --derivations which are rejected by memory
// * --...
// */
//public class UselessProcess extends NARReaction {
//
//    private final NAR nar;
//    private final On conceptProcessed;
//
//    public UselessProcess(NAR n) {
//        super(n);
//        conceptProcessed = n.memory.eventConceptProcess.on(c -> {
//            onConceptProcessed(c);
//        });
//        this.nar = n;
//    }
//
//    @Override
//    public void event(Class event, Object... args) {
//    }
//
//    void onConceptProcessed(ConceptProcess arg) {
//        Collection<Task> derived = arg.getDerived();
//        int numDerived = derived.size();
//        if (numDerived == 0) {
//            System.err.println(nar.time() + ": " +  arg + " no derivations" );
//        }
//        else {
//            if (arg.getTaskLink().type==0) {
//                System.err.println(nar.time() + " type 0 tasklink caused derivations: " + arg);
//            }
//            System.err.println(nar.time() + ": " +  arg + " OK derived=" + numDerived );
//        }
//    }
//}
