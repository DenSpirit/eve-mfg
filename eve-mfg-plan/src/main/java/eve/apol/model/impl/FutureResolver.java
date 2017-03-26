package eve.apol.model.impl;

import java.util.concurrent.CompletableFuture;

import org.apache.http.concurrent.FutureCallback;

public class FutureResolver<T> implements FutureCallback<T> {
    
    private CompletableFuture<T> future;
    
    public FutureResolver(CompletableFuture<T> future) {
        this.future = future;
    }

    @Override
    public void completed(T result) {
        future.complete(result);
    }

    @Override
    public void failed(Exception ex) {
        future.completeExceptionally(ex);
    }

    @Override
    public void cancelled() {
        future.cancel(true);
    }

}
