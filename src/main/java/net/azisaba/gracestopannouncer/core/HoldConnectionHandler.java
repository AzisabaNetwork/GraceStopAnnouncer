package net.azisaba.gracestopannouncer.core;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import net.azisaba.gracestopannouncer.core.util.PodSelfInformation;
import org.apache.commons.lang3.time.DateFormatUtils;

@RequiredArgsConstructor
public class HoldConnectionHandler {

  private final KubernetesClient client;
  private final PodSelfInformation podInfo;
  private final ServerAdapter serverAdapter;

  private final CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

  private final ReentrantLock lock = new ReentrantLock();
  private boolean alreadyTriggered = false;

  public CompletableFuture<Integer> getCompletableFuture() {
    return completableFuture;
  }

  public void notifyPreStopTriggered() {
    lock.lock();
    try {
      if (alreadyTriggered) {
        return;
      }
      alreadyTriggered = true;
    } finally {
      lock.unlock();
    }

    serverAdapter.executeAsyncAfter(
        () -> {
          int retryCount = 5;
          int remainingTime = -1;

          for (int i = 0; i < retryCount; i++) {
            int value = getRemainingTime();
            if (value < 0) {

              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                completableFuture.complete(-1);
                Thread.currentThread().interrupt();
                return;
              }

              continue;
            }

            remainingTime = value;
            break;
          }

          if (remainingTime < 0) {
            completableFuture.complete(-1);
            return;
          }

          int remainingTimeForAnnounce = remainingTime - 60;

          if (remainingTimeForAnnounce <= 0) {
            completableFuture.complete(remainingTime);
          } else {
            serverAdapter.notifyShutdown(remainingTimeForAnnounce);
            serverAdapter.executeAsyncAfter(
                () -> completableFuture.complete(getRemainingTime()), remainingTimeForAnnounce);
          }
        },
        0L);
  }

  private int getRemainingTime() {
    Pod pod = client.pods().inNamespace(podInfo.getNamespace()).withName(podInfo.getName()).get();
    if (pod == null) {
      return -1;
    }
    if (pod.getMetadata().getDeletionTimestamp() == null) {
      return -1;
    }

    int remainingTime;
    try {
      long localDateInMillis =
          DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT
                  .parse(pod.getMetadata().getDeletionTimestamp())
                  .getTime()
              + TimeZone.getDefault().getRawOffset();

      remainingTime = (int) (localDateInMillis - new Date().getTime()) / 1000;
    } catch (ParseException e) {
      e.printStackTrace();
      return -1;
    }

    return remainingTime;
  }
}
