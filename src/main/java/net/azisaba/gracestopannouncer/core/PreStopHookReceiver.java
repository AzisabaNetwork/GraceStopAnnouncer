package net.azisaba.gracestopannouncer.core;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class PreStopHookReceiver {

  private final int port;
  private final HoldConnectionHandler handler;

  @Getter @Setter private ExecutorService executorService = Executors.newFixedThreadPool(1);

  public boolean start() {
    HttpServer server;
    try {
      server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
    } catch (IOException ex) {
      ex.printStackTrace();
      return false;
    }

    server.setExecutor(executorService);
    server.createContext("/prestop", new RequestHandler(handler));
    server.start();
    return true;
  }
}
