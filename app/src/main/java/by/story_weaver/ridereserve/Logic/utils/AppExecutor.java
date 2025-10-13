package by.story_weaver.ridereserve.Logic.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppExecutor implements Executor {

    private final Executor delegate;

    @Inject
    public AppExecutor() {
        this.delegate = Executors.newFixedThreadPool(2);
    }

    @Override
    public void execute(Runnable command) {
        delegate.execute(command);
    }
}
