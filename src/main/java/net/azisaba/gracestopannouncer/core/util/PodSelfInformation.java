package net.azisaba.gracestopannouncer.core.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PodSelfInformation {

  private final String namespace;
  private final String name;
}
