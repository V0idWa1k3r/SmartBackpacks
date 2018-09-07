package v0id.vsb.util;

import java.util.function.Supplier;

public class Lazy<T>
{
    private final Supplier<T> supplier;
    private T t;
    private boolean initialized;

    public Lazy(Supplier<T> supplier)
    {
        this.supplier = supplier;
    }

    public T get()
    {
        if (!this.initialized)
        {
            this.t = this.supplier.get();
            this.initialized = true;
        }

        return this.t;
    }

    public void reset()
    {
        this.initialized = false;
    }

    public boolean isInitialized()
    {
        return this.initialized;
    }
}
