package net.azisaba.gracestopannouncer.core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RequestHandler implements HttpHandler {

  private final HoldConnectionHandler holdConnectionHandler;

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    holdConnectionHandler.notifyPreStopTriggered();
    int remainingTime = holdConnectionHandler.getCompletableFuture().join();

    String resBody = "{}";
    long contentLength = resBody.getBytes(StandardCharsets.UTF_8).length;
    httpExchange.sendResponseHeaders(200, contentLength);

    OutputStream os = httpExchange.getResponseBody();
    os.write(resBody.getBytes());
    os.close();
  }
}
