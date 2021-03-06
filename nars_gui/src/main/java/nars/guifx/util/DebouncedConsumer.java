package nars.guifx.util;

import javafx.application.Platform;
import nars.NAR;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Created by me on 8/30/15.
 */
public interface DebouncedConsumer extends Consumer<NAR> {

    @Override
    default void accept(NAR n) {
        if (canQueue().compareAndSet(true, false)) {

            final long now = System.currentTimeMillis();
            final long prev = lastInvocation().getAndSet(now);
            final long delta = now - prev;

            update(delta);

            boolean fx = Platform.isFxApplicationThread();

            final Runnable upd = () -> {

                run(delta);

                canQueue().set(true);
            };

            if (fx)
                upd.run();
            else
                Platform.runLater(upd);
        }
    }

    /**
     * gate/lock which this will use to prevent repeats. should be initialized as true
     */
    public AtomicBoolean canQueue();

    public AtomicLong lastInvocation();

    //min update period?

    /**
     * invoked in the trigger thread
     */
    public void update(long msSinceLast);

    public void run(long msSinceLast);

}
