package net.azisaba.gracestopannouncer.core;

public interface ServerAdapter {

  void executeAsyncAfter(Runnable runnable, long delay);

  void notifyShutdown(int remainingTime);
}
