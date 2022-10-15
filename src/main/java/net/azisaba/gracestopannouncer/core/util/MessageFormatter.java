package net.azisaba.gracestopannouncer.core.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MessageFormatter {

  private String baseMessage;

  private String hourStr;
  private String minuteStr;
  private String secondStr;

  public String format(int remainingTime) {
    String time = formatTime(remainingTime);
    return baseMessage.replace("%time%", time);
  }

  private String formatTime(int remainingTime) {
    int second = remainingTime % 60;
    int minute = -1;
    if (remainingTime >= 60) {
      minute = ((remainingTime - second) / 60) % 3600;
    }

    int hour = -1;
    if (remainingTime >= 3600) {
      hour = (remainingTime - second - (minute * 60)) / 3600;
    }

    if (hour <= 0 && minute <= 0) {
      return secondStr.replace("%s", String.valueOf(second));
    } else if (hour <= 0) {
      if (second <= 0) {
        return minuteStr.replace("%m", String.valueOf(minute));
      } else {
        return minuteStr.replace("%m", String.valueOf(minute))
            + secondStr.replace("%s", String.valueOf(second));
      }
    } else {
      if (minute <= 0 && second <= 0) {
        return hourStr.replace("%h", String.valueOf(hour));
      } else if (minute <= 0) {
        return hourStr.replace("%h", String.valueOf(hour))
            + secondStr.replace("%s", String.valueOf(second));
      } else if (second <= 0) {
        return hourStr.replace("%h", String.valueOf(hour))
            + minuteStr.replace("%m", String.valueOf(minute));
      } else {
        return hourStr.replace("%h", String.valueOf(hour))
            + minuteStr.replace("%m", String.valueOf(minute))
            + secondStr.replace("%s", String.valueOf(second));
      }
    }
  }
}
