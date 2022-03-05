package fr.ferret.model;

import fr.ferret.utils.Resource;

public final class State {
    public static final String DOWNLOADING_HEADER= Resource.getTextElement("state.downloadingHeader");
    public static final String DOWNLOADING_LINES= Resource.getTextElement("state.downloadingLines");
    public static final String WRITING= Resource.getTextElement("state.writingFile");
    public static final String WRITTEN= Resource.getTextElement("state.fileWritten");

    private State() {
    }
}
