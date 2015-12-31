package nars.index;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import nars.Op;
import nars.term.Term;
import nars.term.TermContainer;
import nars.term.Termed;
import nars.term.compile.TermIndex;
import nars.time.Clock;
import nars.util.WeakValueHashMap;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

/** TermIndex implemented with GuavaCache with
 * optional WeakRef policy.
 * suitable for running indefnitely and obeying AIKR
 * principles
 * */
public class GuavaIndex implements TermIndex {

    final Cache<Term,Termed> data;
    final WeakValueHashMap subterms;


    public GuavaIndex() {
        this(CacheBuilder.newBuilder());
    }

    public GuavaIndex(Clock reasonerClock, int expirationCycles) {
        this(CacheBuilder.newBuilder()
                //.maximumSize(capacity)

//                .expireAfterWrite(expirationCycles, TimeUnit.NANOSECONDS)
//                .expireAfterAccess(expirationCycles, TimeUnit.NANOSECONDS)
//                .ticker(new Ticker() {
//                    @Override public long read() {
//                        return reasonerClock.time();
//                    }
//                })

                //.weakValues()
                //.softValues()

                //.recordStats()
//                .removalListener((e) -> {
//                    if (e.getCause()!= RemovalCause.REPLACED)
//                        System.err.println("guava remove: " + e + " : " + e.getCause() );
//                }));
        );

    }

    public GuavaIndex(CacheBuilder cb) {
        this.data = cb.build();
        this.subterms = new WeakValueHashMap();
//        subterms = CacheBuilder.newBuilder()
//                //.maximumSize(capacity)
//                .softValues()
////                .removalListener((e) -> {
////                    if (e.getCause()!= RemovalCause.REPLACED)
////                        System.err.println("guava remove: " + e + " : " + e.getCause() );
////                })
//              .build();
    }

    @Override
    public int subtermsCount() {
        return subterms.size();
    }

    @Override
    public void forEach(Consumer<? super Termed> c) {
        data.asMap().forEach((k,v) -> {
            c.accept(k);
        });
    }

    /** gets an existing item or applies the builder to produce something to return */
    @Override
    public <K extends Term> Termed<K> apply(K key, Function<K, Termed> builder)  {
        try {
            return data.get(key, () -> builder.apply(key));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public Termed getTermIfPresent(Termed t) {
        return data.getIfPresent(t.term());
    }


    @Override
    public void clear() {
        data.invalidateAll();
        data.cleanUp();
//        subterms.invalidateAll();
//        subterms.cleanUp();
    }

    @Override
    public Object remove(Term key) {
        data.invalidate(key);
        return key; //?
    }

    @Override
    public void putTerm(Termed termed) {
        data.put(termed.term(), termed);
    }

    @Override
    public int size() {
        return (int)data.size();
    }

    @Override
    public Termed internCompound(Op op, int relation, TermContainer subterms) {
        return MapIndex.intern(op, relation, getIfAbsentIntern(subterms));
    }

    @Override
    public TermContainer getIfAbsentIntern(TermContainer s) {
//        try {
//            //return subterms.get(s, () -> internSubterms(s.terms()));
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
        return (TermContainer) subterms.computeIfAbsent(s, (ss) -> internSubterms(((TermContainer) ss).terms()));
    }

    @Override
    public Termed get(Object t) {

        if (!(t instanceof Termed)) {
            throw new RuntimeException("invalid key");
        }

        Termed xx = (Termed) t;
        Term x = xx.term();

        if (!MapIndex.isInternable(x)) {
            return xx;
        }

        try {
            return data.get(x, () -> intern(x));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
