package org.openjfx.visual.mapmodes;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class MapModeController {
    private final CachedStyleProvider cachedStyleProvider;
    private final Map<String, CellStyleProvider> providers;

    public void setMode(String mode) {
        CellStyleProvider provider = providers.get(mode);
        if (provider != null) {
            cachedStyleProvider.setProvider(provider);
        }
    }
}
