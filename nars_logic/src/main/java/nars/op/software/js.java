package nars.op.software;

import nars.Memory;
import nars.NAR;
import nars.nal.truth.DefaultTruth;
import nars.nal.Task;
import nars.nal.truth.Truth;
import nars.nal.concept.ConstantConceptBuilder;
import nars.nal.nal8.Operation;
import nars.nal.nal8.operator.SynchOperator;
import nars.nal.nal8.operator.TermFunction;
import nars.nal.term.Atom;
import nars.nal.term.Term;
import nars.op.io.Echo;
import nars.op.mental.Mental;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import java.util.HashMap;
import java.util.List;

/**
 * Executes a Javascript expression
 */
public class js extends TermFunction implements Mental {

    ScriptEngine js = null;

    final HashMap global = new HashMap();

    class DynamicFunction extends TermFunction {

        private final String function;
        private Object fnCompiled;

        public DynamicFunction(String name, String function) {
            super(name);
            this.function = function;

            ensureJSLoaded();

            try {
                this.fnCompiled = js.eval(function);
            }
            catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        @Override public Object function(Term[] args) {

            Bindings bindings = newBindings(args);
            bindings.put("_o", fnCompiled);
            String input = "_o.apply(this,arg)";

            Object result;
            try {
                result = js.eval(input, bindings);
            } catch (Throwable ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
            return result;
        }


    }

    /** create dynamic javascript functions */
    //TODO make this an ImmediateOperator that will not conceptualize its subterms
    public class jsop extends SynchOperator {

        @Override
        protected List<Task> execute(Operation operation, Memory memory) {
            Term[] x = operation.argArray();
            String funcName = Atom.unquote(x[0]);
            String functionCode = Atom.unquote(x[1]);
            nar.input(new Echo(nars.op.software.js.class, "JS Operator Bind: " + funcName + " = " + functionCode));
            DynamicFunction d = new DynamicFunction(funcName, functionCode.toString());
            nar.on(d);

            operation.stop(memory);

            return null;
        }



    }

    public class JSBelievedConceptBuilder extends ConstantConceptBuilder {

        private Object fnCompiled;

        public JSBelievedConceptBuilder(String fnsource) {

            ensureJSLoaded();

            try {
                this.fnCompiled = js.eval(fnsource);
            }
            catch (Throwable ex) {
                ex.printStackTrace();
            }
        }


        @Override
        protected Truth truth(Term t, Memory m) {

            Bindings bindings = new SimpleBindings();
            bindings.put("t", t);
            bindings.put("_o", fnCompiled);
            String input = "_o.apply(this,[t])";

            Object result;
            try {
                result = js.eval(input, bindings);
            } catch (Throwable ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }

            if (result instanceof Number) {
                return new DefaultTruth(((Number)result).floatValue(), 0.99f);
            }
            if (result instanceof Object[]) {
                if (((Object[])result).length > 1) {
                    Object a = ((Object[])result)[0];
                    Object b = ((Object[])result)[1];
                    if ((a instanceof Number) && (b instanceof Number)) {
                        return new DefaultTruth(((Number) a).floatValue(), ((Number) b).floatValue());
                    }
                }
            }

            return null;
        }
    }


    /** create dynamic javascript functions */
    public class jsbelief extends SynchOperator {


        @Override
        protected List<Task> execute(Operation operation, Memory memory) {
            Term[] x = operation.argArray();

            String functionCode = Atom.unquote(x[0]);

            nar.on(new JSBelievedConceptBuilder(functionCode));

            operation.stop(memory);

            return null;
        }

    }


    @Override
    public boolean setEnabled(NAR n, boolean enabled) {
        boolean x = super.setEnabled(n, enabled);
        if (enabled) {
            n.on(new jsop());
            n.on(new jsbelief());
        }
        return x;
    }


    public Bindings newBindings(Term[] args) {

        Bindings bindings = new SimpleBindings();
        bindings.put("global", global);
        bindings.put("js", this);
        bindings.put("arg", args);
        bindings.put("memory", getMemory());
        bindings.put("nar", nar);

        return bindings;
    }

    protected synchronized void ensureJSLoaded() {
        if (js == null) {
            ScriptEngineManager factory = new ScriptEngineManager();
            js = factory.getEngineByName("JavaScript");
        }
    }

    @Override public Object function(Term[] args) {
        if (args.length < 1) {
            return null;
        }

        ensureJSLoaded();
        


        // copy over all arguments
        Term[] scriptArguments;
        scriptArguments = new Term[args.length-1];
        System.arraycopy(args, 1, scriptArguments, 0, args.length-1);

        Bindings bindings = newBindings(scriptArguments);


        
        String input = args[0].toString();
        if (input.charAt(0) == '"') {
            input = input.substring(1, input.length() - 1);
        }
        Object result;
        try {

            result = js.eval(input, bindings);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

}
