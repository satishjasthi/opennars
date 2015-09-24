package nars.meta.pre;

import nars.meta.RuleMatch;
import nars.nal.nal3.SetExt;
import nars.nal.nal3.SetInt;
import nars.nal.nal3.SetTensional;
import nars.term.Compound;
import nars.term.Term;

import java.util.ArrayList;

/**
 * Created by me on 8/15/15.
 */
public class Union extends PreCondition3 {

    public Union(Term arg1, Term arg2, Term arg3) {
        super(arg1, arg2, arg3);
    }

    @Override
    public boolean test(RuleMatch m, Term a, Term b, Term c) {

        if(a==null || b==null || c==null || (!((a instanceof SetExt) && (b instanceof SetExt)) && !((a instanceof SetInt) && (b instanceof SetInt)))) {
            return false;
        }

        //ok both are extensional sets or intensional sets, build difference
        SetTensional A = (SetTensional) a;
        SetTensional B = (SetTensional) b;

        ArrayList<Term> terms = new ArrayList<Term>();
        for(Term t: A.terms()) { //set difference
            terms.add(t);
        }
        for(Term t2 : B.terms()) {
            terms.add(t2);
        }

        if(a instanceof SetExt) {
            Compound res = SetExt.make(terms);
            if(res==null) {
                return false;
            }
            m.map1.put(c, res);
        }
        if(a instanceof SetInt) {
            Compound res = SetInt.make(terms);
            if(res==null) {
                return false;
            }
            m.map1.put(c, res);
        }

        return true;
    }
}
