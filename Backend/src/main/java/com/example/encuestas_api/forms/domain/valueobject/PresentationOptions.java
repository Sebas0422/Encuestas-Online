package com.example.encuestas_api.forms.domain.valueobject;

public final class PresentationOptions {
    private final boolean shuffleQuestions;
    private final boolean shuffleOptions;
    private final boolean progressBar;
    private final boolean paginated;

    private PresentationOptions(boolean shuffleQuestions, boolean shuffleOptions, boolean progressBar, boolean paginated) {
        this.shuffleQuestions = shuffleQuestions;
        this.shuffleOptions = shuffleOptions;
        this.progressBar = progressBar;
        this.paginated = paginated;
    }

    public static PresentationOptions of(boolean shuffleQuestions, boolean shuffleOptions, boolean progressBar, boolean paginated) {
        return new PresentationOptions(shuffleQuestions, shuffleOptions, progressBar, paginated);
    }

    public boolean shuffleQuestions(){ return shuffleQuestions; }
    public boolean shuffleOptions(){ return shuffleOptions; }
    public boolean progressBar(){ return progressBar; }
    public boolean paginated(){ return paginated; }
}
