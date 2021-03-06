package nars.java;

import com.google.common.collect.Lists;
import nars.Global;
import nars.NAR;
import nars.nar.Default;
import nars.term.Term;
import nars.util.meter.EventCount;
import org.junit.Ignore;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.countMatches;
import static org.junit.Assert.*;


public class NALObjectsTest  {

    public static class TestClass {

        public int count = 0;
        public int val = -1;


        public double the() {
            double v = (64 * Math.random());
            this.val = (int)v;
            return v;
        }

        public void noParamMethodReturningVoid() {
            count++;
            //System.out.println("base call");
            //return Math.random();
        }

        public float multiply(float a, float b) {
            count++;
            return a * b;
        }

        @Override
        public String toString() {
            return "TestClass[" + count + "]";
        }

        public List<Method> getClassMethods() {
            Method[] m = getClass().getMethods();
            List<Method> l = Global.newArrayList();
            for (Method x : m)
                if (NALObjects.isMethodVisible(x))
                    if (!x.getName().equals("getClassMethods"))
                        l.add(x);
            return l;
        }
    }


    @Test public void testInvocationExternal() throws Exception {
        testMethodInvocationAndFeedback(true);
    }

    @Test public void testInvocationInternal() throws Exception {
        testMethodInvocationAndFeedback(false);
    }


    /** test that the methods of invoking an instance method are indistinguishable
     * whether it occurred from outside, or from a NAR goal
     *
     * only one invocation and one feedback should occurr
     * regardless of the method and preconditions
     */
    public void testMethodInvocationAndFeedback(boolean external) throws Exception {

        NAR n = new Default(128, 1, 1, 1);

        StringWriter ns = new StringWriter();
        n.log(new PrintWriter(ns));

        //n.log();

        String instance = "obj";

        NALObjects no = new NALObjects(n);

        final TestClass wrapper;
        final TestClass wrapped = new TestClass();

        wrapper = no.wrap(instance, wrapped);

        assertEquals(5, n.memory.exe.size());

        assertNotEquals(TestClass.class, wrapper.getClass());
        assertEquals(TestClass.class, wrapper.getClass().getSuperclass());

        if (external) {
            //INVOKE EXTERNALLY
            wrapper.multiply(2, 3);
        }
        else {
            //INVOKE VOLITIONALLY
            n.input("TestClass_multiply(" + instance + ", (2, 3), #x)! :|:");

        }



        n.frame(8);


        assertEquals(0, wrapped.count); //unaffected
        assertEquals(1, wrapper.count); //wrapper fields affected

        //WHAT TO EXPECT
        /*

        CLASS IS IN WHICH PACKAGE
          eventTaskProcess: $0.50;0.80;0.95$
                 <{((nars, java), TestClass)} --> package>. %1.00;0.90% Input

        OBJECT INSTANCE OF WHICH CLASS
          eventTaskProcess: $0.50;0.80;0.95$
                <{obj} --> ((nars, java), TestClass)>. %1.00;0.90% Input

        METHOD INVOKED (either by reasoner explicitly, or by external source)
          eventTaskProcess: $0.60;0.90;0.95$
                TestClass_multiply(obj, (2, 3), #1)! :\: %1.00;0.90% Input

        METHOD RETURNED VALUE
          eventTaskProcess: $0.50;0.80;0.95$
                <{6} --> (/, ^TestClass_multiply, obj, (2, 3), _)>. :\: %1.00;0.90% Input

        TODO: method metadata, including parameter typing information
        */


        //System.out.println(ns.getBuffer().toString());
        //System.out.println();
        //System.out.println(ms.getBuffer().toString());

        //TODO use TestNAR and test for right tense


        String bs = ns.getBuffer().toString();

        System.out.println(bs);

        String invocationGoal = "TestClass_multiply(obj, (2, 3), #1)! :|: %1.00;0.90%";
        assertEquals(1, countMatches(bs, invocationGoal));

        if (external) {
            assertEquals(1, countMatches(bs, invocationGoal + " Puppet"));
        }
        else {
            assertEquals(1, countMatches(bs, invocationGoal + " Input"));
        }

        String execution = "Execute: $0.60;0.90;0.95$ TestClass_multiply(obj, (2, 3), #1)! 0+0 %1.00;0.90%";
        String feedback = "TaskProcess: $0.50;0.80;0.95$ <6 --> (/, ^TestClass_multiply, obj, (2, 3), _)>.";

        assertEquals(1, countMatches(bs, execution));
        assertEquals(1, countMatches(bs, feedback));
        assertEquals(1, countMatches(bs, "Feedback"));

    }


    @Test
    public void testDynamicProxyObjects() throws Exception {


        NAR n = new Default();

        EventCount count = new EventCount(n);

        TestClass tc = new NALObjects(n).wrap("myJavaObject", new TestClass());

        tc.noParamMethodReturningVoid();
        assertEquals(6.0, tc.multiply(2, 3), 0.001);
        assertNotNull( tc.the() );


        n.frame(4);


        assertNotEquals(tc.getClass(), TestClass.class);
        assertTrue(1 <= count.numInputs());


    }


    @Test public void testTermizerPrimitives() {

        testTermizer(null);

        testTermizer(0);
        testTermizer(3.14159);

        testTermizer('a');

        testTermizer("a b c"); //should result in quoted
    }

    @Test public void testTermizerBoxed() {
        testTermizer(new Integer(1));
        testTermizer(new Float(3.14159));
    }
    @Test public void testTermizerCollections() {
        testTermizer(Lists.newArrayList("x", "y"));
    }
    @Test public void testTermizerArray() {
        testTermizer(new String[] { "x", "y" } );
    }

    @Test public void testMapTermizer() {
        Map map = new HashMap();
        map.put("k1", "v1");
        map.put("k2", "v2");
        testTermizer(map, "{<{\"v1\"}-->\"k1\">,<{\"v2\"}-->\"k2\">}");

    }

    static void testTermizer(Object o, String termtoString) {
        DefaultTermizer t = new DefaultTermizer();
        Term term = t.term(o);
        assertNotNull(term);
        assertEquals(termtoString, term.toString(false));
    }

    static void testTermizer(Object o) {
        DefaultTermizer t = new DefaultTermizer();
        Term term = t.term(o);
        assertNotNull(term);
        Object p = t.object(term);

        //System.out.println(t.objects);

        //if (o!=null)
            assertEquals(p, o);
        /*else
            assertNull(p==null ? "('null' value)" : p.getClass().toString(),
                       p);*/


    }

    //TODO
    @Ignore
    @Test public void testOverloadedMethods() throws Exception {
        NAR n = new Default();

        NALObjects no = new NALObjects(n);
        ArrayList nc = no.wrap("ourList", new ArrayList());


        //n.stdout();
        nc.add("item");
        //nc.add("x");

        n.frame(2);

        nc.toArray();

        nc.size();

        nc.clear();

        nc.size();


        nc.add("item");
        nc.add("item");

        nc.toArray();

        nc.size();



        nc.add(1);
        nc.get(0);
        nc.size();
        nc.clear();

        n.frame(50);

    }

    @Test
    public void testLearnMethods() throws Exception {


        NAR n = new Default(512,8,4,2);

        n.log();

        //EventCount count = new EventCount(n);

        TestClass tc = new NALObjects(n).wrap("obj", new TestClass());


        tc.getClassMethods();


        n.frame(32);



    }

}