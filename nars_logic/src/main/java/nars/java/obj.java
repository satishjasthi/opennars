package nars.java;

import nars.$;
import nars.nal.nal8.operator.TermFunction;
import nars.term.Term;
import nars.term.atom.Atom;
import nars.term.compound.Compound;

/**
 * resolve a java object expression (instance or static) to a term
 */
public class obj extends TermFunction {

    final Termizer termizer = new DefaultTermizer();

    @Override public Object function(Compound x) {

        try {
            Term t = x.term(0);
            if (t instanceof Atom) {
                String tt = ((Atom)t).toStringUnquoted();
                Class c = Class.forName(tt);
                return $.p(termizer.term(c), termizer.term(c.getMethods()));
            }
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
