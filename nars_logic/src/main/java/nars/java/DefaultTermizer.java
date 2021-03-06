package nars.java;

import com.gs.collections.impl.bimap.mutable.HashBiMap;
import nars.$;
import nars.Global;
import nars.nal.nal2.Instance;
import nars.nal.nal3.SetExt;
import nars.nal.nal4.Product;
import nars.term.Atom;
import nars.term.Term;
import nars.term.Variable;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by me on 8/19/15.
 */
public class DefaultTermizer implements Termizer {


    public static final Atom PACKAGE = Atom.the("package");
    public static final Atom PRIMITIVE = Atom.the("primitive");
    public static final Variable INSTANCE_VAR = $.varDep("instance");

    final Map<Package, Term> packages = new HashMap();
    final Map<Class, Term> classes = new HashMap();


    final HashBiMap<Term,Object> instances = new HashBiMap();

    /*final HashMap<Term, Object> instances = new HashMap();
    final HashMap<Object, Term> objects = new HashMap();*/

    static final Set<Class> classInPackageExclusions = new HashSet() {{
        add(Class.class);
        add(Object.class);

        //since autoboxing can be managed, the distinction between boxed and unboxed values should not be seen by reasoner
        add(Float.class);
        add(Double.class);
        add(Boolean.class);
        add(Long.class);
        add(Integer.class);
        add(Short.class);
        add(Byte.class);
        add(Class.class);
    }};

    public DefaultTermizer() {
        map(NULL, null);
        map(TRUE, true);
        map(FALSE, false);
    }

    public void map(Term x, Object y) {
        instances.put(x, y);
    }

    /** dereference a term to an object (but do not un-termize) */
    @Override public Object object(final Term t) {

        if (t == NULL) return null;

        Object x = instances.get(t);
        if (x == null)
            return t; /** return the term intance itself */

        return x;
    }


    Term obj2term(Object o) {

        if (o == null)
            return NULL;


        if (o instanceof Term) return (Term)o;

        if (o instanceof String)
            return Atom.quote((String) o);

        if (o instanceof Boolean)
            return ((Boolean) o) ? TRUE : FALSE;

        if (o instanceof Number)
            return Atom.the((Number) o);

        if (o instanceof Class) {
            Class oc = (Class) o;

            Package p = oc.getPackage();
            if (p != null) {

                Term cterm = termClassInPackage(oc);

                if (reportClassInPackage(oc)) { //TODO use a method for other class exclusions
                    Term pkg = packages.get(p);
                    if (pkg == null) {
                        pkg = termPackage(p);
                        packages.put(p, pkg);
                        termClassInPackage(cterm, PACKAGE);
                    }

                    //TODO add recursive superclass ancestry?
                }

                return cterm;
            }
            return PRIMITIVE;
        }

        if (o instanceof int[]) {
            final List<Term> arg = Arrays.stream((int[]) o)
                    .mapToObj(e -> Atom.the(e)).collect(Collectors.toList());
            if (arg.isEmpty()) return EMPTY;
            return Product.make(
                    arg
            );
        } else if (o instanceof Object[]) {
            final List<Term> arg = Arrays.stream((Object[]) o).map(e -> term(e)).collect(Collectors.toList());
            if (arg.isEmpty()) return EMPTY;
            return Product.make(
                    arg
            );
        } else if (o instanceof List) {
            if (((List)o).isEmpty()) return EMPTY;

            //TODO can this be done with an array to avoid duplicate collection allocation


            Collection c = (Collection) o;
            List<Term> arg = Global.newArrayList(c.size());
            for (Object x : c) {
                Term y = term(x);
                arg.add(y);
            }

            if (arg.isEmpty()) return EMPTY;

            return Product.make(arg);

        /*} else if (o instanceof Stream) {
            return Atom.quote(o.toString().substring(17));
        }*/
        } else if (o instanceof Set) {
            Collection<Term> arg = (Collection<Term>) ((Collection) o).stream().map(e -> term(e)).collect(Collectors.toList());
            if (arg.isEmpty()) return EMPTY;
            return SetExt.make(arg);
        } else if (o instanceof Map) {

            Map mapo = (Map) o;
            Set<Term> components = Global.newHashSet(mapo.size());
            mapo.forEach((k, v) -> {

                Term tv = obj2term(v);
                Term tk = obj2term(k);

                if ((tv != null) && (tk!=null)) {
                    components.add(
                        Instance.make(tv, tk)
                    );
                }
            });
            if (components.isEmpty()) return EMPTY;
            return SetExt.make(components);
        }
        else if (o instanceof Method) {
            //translate the method to an operation term
            Method m = (Method)o;
            return $.oper(
                NALObjects.getMethodOperator(m),
                getMethodArgVariables(m)
            );
        }

        return termInstanceInClassInPackage(o);


//        //ensure package is term'ed
//        String pname = p.getName();
//        int period = pname.length()-1;
//        int last = period;
//        Term child = cterm;
//        while (( period = pname.lastIndexOf('.', period)) != -1) {
//            String parname = pname.substring(0, last);
//            Term parent = packages.get(parname);
//            if (parent == null) {
//                parent = Atom.the(parname);
//                nar.believe( Inheritance.make(child, parent) );
//                packages.put()
//                last = period;
//                child = parent;
//            }
//            else {
//                break;
//            }
//        }


    }

    private boolean reportClassInPackage(Class oc) {
        if (classInPackageExclusions.contains(oc)) return false;

        if (Term.class.isAssignableFrom(oc)) return false;
        if (oc.isPrimitive()) return false;


        return true;
    }


    /** (#arg1, #arg2, ...), #returnVar */
    private Term[] getMethodArgVariables(Method m) {

        //TODO handle static methods which will not receive first variable instance

        String varPrefix = m.getName() + "_";
        int n = m.getParameterCount();
        Product args = $.pro(getArgVariables(varPrefix, n));

        if (m.getReturnType() == void.class) {
            return new Term[]{
                INSTANCE_VAR,
                args
            };
        } else {
            return new Term[]{
                INSTANCE_VAR,
                args,
                $.varDep(varPrefix + "_return") //return var
            };
        }
    }

    private Term[] getArgVariables(String prefix, int numParams) {
        Term[] x = new Term[numParams];
        for (int i = 0; i < numParams; i++) {
            x[i] = $.varDep(prefix + i);
        }
        return x;
    }

    public static Term termClass(Class c) {
        return Atom.the(c.getSimpleName());
    }

    public static Term termClassInPackage(Class c) {
        return Product.make(
            termPackage(c.getPackage()),
            termClass(c)
        );
    }

    public static Term termPackage(Package p) {
        //TODO cache?
        String[] path = p.getName().split("\\.");
        return $.pro(path);

        //return Atom.the(p.getName());
    }

    public static Term termInstanceInClassInPackage(Object o) {
        //return o.getClass().getName() + '@' + Integer.toHexString(o.hashCode());
        //return o.getClass() + "_" + System.identityHashCode(o)
        return Product.make(
                    termPackage(o.getClass().getPackage()),
                    termClassInPackage(o.getClass()),
                    Atom.the(System.identityHashCode(o), 36)
                );
    }

    protected Term termClassInPackage(Term classs, @Deprecated Term packagge) {
        //TODO ??
        return null;
    }

    public Term term(final Object o) {
        if (o == null) return NULL;

        //        String cname = o.getClass().toString().substring(6) /* "class " */;
//        int slice = cname.length();
//
        Runnable[] post = new Runnable[1];


        Term result = obj2termCached(o, post);

        if (result!=null)
            if (post[0]!=null)
                post[0].run();

        return result;


        //TODO decide to use toString or System object id
        //String instanceName = o.toString();
//        if (instanceName.length() > slice)
//            instanceName = instanceName.substring(slice);

        //final Term oterm = Atom.quote(instanceName);

//        Term prevOterm = objects.put(o, oterm);
        //if (prevOterm == null) {


        //}
//        else {
//            if (!oterm.equals(prevOterm)) {
//                //toString value has changed, create similarity to associate
//                onInstanceChange(oterm, prevOterm);
//            }
//        }

        //return oterm;
    }

    public Term obj2termCached(Object o, Runnable[] post) {

        if (o == null) return NULL;
        if (o instanceof Term)
            return ((Term)o);

        return instances.inverse().computeIfAbsent(o, O -> {

            Term oterm = obj2term(O);

            Term clas = classes.computeIfAbsent(O.getClass(), this::obj2term);

            final Term finalClas = clas;
            post[0] = () ->  onInstanceOfClass(O, oterm, finalClas);

            //instances.put(oterm, o); //reverse

            return oterm;
        });
    }


    protected void onInstanceChange(Term oterm, Term prevOterm) {

    }

    protected void onInstanceOfClass(Object o, Term oterm, Term clas) {

    }


}
