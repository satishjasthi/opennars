package nars.op.software.scheme;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import nars.$;
import nars.nal.nal8.operator.TermFunction;
import nars.op.software.scheme.cons.Cons;
import nars.op.software.scheme.expressions.Expression;
import nars.op.software.scheme.expressions.ListExpression;
import nars.op.software.scheme.expressions.NumberExpression;
import nars.op.software.scheme.expressions.SymbolExpression;
import nars.term.Term;
import nars.term.atom.Atom;
import nars.term.compound.Compound;
import nars.term.op.Operator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class scheme extends TermFunction {

    public static final SchemeClosure env = DefaultEnvironment.newInstance();

    static final Function<Term,Expression> narsToScheme = term -> {

        if (term instanceof Compound) {
            //return ListExpression.list(SymbolExpression.symbol("quote"), new SchemeProduct((Product)term));
            return new SchemeProduct((Compound)term);
        }
        if (term instanceof Atom) {

            String s = term.toString();

            //attempt to parse as number
            try {
                double d = Double.parseDouble(s);
                return new NumberExpression((long)d);
            }
            catch (NumberFormatException e) { }
            //atomic symbol
            return new SymbolExpression(s);
        }
        throw new RuntimeException("Invalid term for scheme: " + term);
    };

    /** adapter class for NARS term -> Scheme expression; temporary until the two API are merged better */
    public static class SchemeProduct extends ListExpression {


        public SchemeProduct(Compound p) {
            super(Cons.copyOf(Iterables.transform(p, narsToScheme)));
        }
    }

    public Expression eval(Compound p) {
        return Evaluator.evaluate(new SchemeProduct(p), env);
    }

    //TODO make narsToScheme method

    public static final Function<Expression, Term> schemeToNars = new Function<Expression, Term>() {
        @Override
        public Term apply(Expression schemeObj) {
            if (schemeObj instanceof ListExpression) {
                List<Term> elements = Lists.newArrayList(StreamSupport.stream(((ListExpression) schemeObj).value.spliterator(), false).map(schemeToNars::apply).collect(Collectors.toList()));
                return $.p( elements );
            }
            //TODO handle other types, like Object[] etc
            else {
                //return Term.get("\"" + schemeObj.print() + "\"" );
                return Atom.the(schemeObj.print());
            }
            //throw new RuntimeException("Invalid expression for term: " + schemeObj);

        }
    };

    @Override
    public Term function(Compound o) {
        Term[] x = Operator.opArgsArray(o);
        Term code = x[0];

        return code instanceof Compound ? schemeToNars.apply(eval(((Compound) code))) : schemeToNars.apply(eval($.p(x)));
        //Set = evaluate as a cond?
//        else {
//
//        }

        //return null;
    }


}
