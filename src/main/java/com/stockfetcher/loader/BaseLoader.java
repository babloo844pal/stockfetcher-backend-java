package com.stockfetcher.loader;

import com.stockfetcher.pool.MainLoaderPool;

public abstract class BaseLoader {

    protected final MainLoaderPool loaderPool;

    public BaseLoader(MainLoaderPool loaderPool) {
        this.loaderPool = loaderPool;
    }

    /**
     * Schedule logic to be implemented by each sub-loader.
     */
    public abstract void scheduleTask();

    /**
     * Fetch and process data from an external API.
     */
    public abstract void loadData();
}
