// Question.java
package com.example.encuestas_api.questions.domain.model;

import com.example.encuestas_api.questions.domain.exception.QuestionTypeMismatchException;
import com.example.encuestas_api.questions.domain.valueobject.Prompt;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class Question {
    private final Long id;
    private final Long formId;
    private final Long sectionId;
    private final int position;

    private final QuestionType type;
    private final Prompt prompt;
    private final String helpText;
    private final boolean required;
    private final boolean shuffleOptions;

    // settings específicos (uno activo según type)
    private final ChoiceSettings choice;
    private final TextSettings text;
    private final MatchingSettings matching;

    private final Instant createdAt;
    private final Instant updatedAt;

    private Question(Long id, Long formId, Long sectionId, int position,
                     QuestionType type, Prompt prompt, String helpText, boolean required, boolean shuffleOptions,
                     ChoiceSettings choice, TextSettings text, MatchingSettings matching,
                     Instant createdAt, Instant updatedAt) {

        if (formId == null) throw new IllegalArgumentException("formId requerido");
        if (type == null) throw new IllegalArgumentException("type requerido");
        if (prompt == null) throw new IllegalArgumentException("prompt requerido");
        if (position < 0) throw new IllegalArgumentException("position >= 0");
        if (createdAt == null || updatedAt == null) throw new IllegalArgumentException("timestamps requeridos");

        switch (type) {
            case CHOICE, TRUE_FALSE -> {
                if (choice == null) throw new QuestionTypeMismatchException("Se requiere ChoiceSettings");
                if (text != null || matching != null) throw new QuestionTypeMismatchException("Settings incompatibles");
            }
            case TEXT -> {
                if (text == null) throw new QuestionTypeMismatchException("Se requiere TextSettings");
                if (choice != null || matching != null) throw new QuestionTypeMismatchException("Settings incompatibles");
            }
            case MATCHING -> {
                if (matching == null) throw new QuestionTypeMismatchException("Se requiere MatchingSettings");
                if (choice != null || text != null) throw new QuestionTypeMismatchException("Settings incompatibles");
            }
        }

        this.id = id;
        this.formId = formId;
        this.sectionId = sectionId;
        this.position = position;
        this.type = type;
        this.prompt = prompt;
        this.helpText = (helpText == null || helpText.isBlank()) ? null : helpText.trim();
        this.required = required;
        this.shuffleOptions = shuffleOptions;
        this.choice = choice;
        this.text = text;
        this.matching = matching;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Question newChoice(Long formId, Long sectionId, int position, Prompt prompt,
                                     String helpText, boolean required, boolean shuffleOptions,
                                     ChoiceSettings choice, Instant now) {
        return new Question(null, formId, sectionId, position, QuestionType.CHOICE,
                prompt, helpText, required, shuffleOptions, choice, null, null, now, now);
    }

    public static Question newTrueFalse(Long formId, Long sectionId, int position, Prompt prompt,
                                        String helpText, boolean required, boolean shuffleOptions,
                                        Option trueOption, Option falseOption, Instant now) {
        var settings = ChoiceSettings.single(List.of(trueOption, falseOption));
        return new Question(null, formId, sectionId, position, QuestionType.TRUE_FALSE,
                prompt, helpText, required, shuffleOptions, settings, null, null, now, now);
    }

    public static Question newText(Long formId, Long sectionId, int position, Prompt prompt,
                                   String helpText, boolean required, TextSettings text, Instant now) {
        return new Question(null, formId, sectionId, position, QuestionType.TEXT,
                prompt, helpText, required, false, null, text, null, now, now);
    }

    public static Question newMatching(Long formId, Long sectionId, int position, Prompt prompt,
                                       String helpText, boolean required, boolean shuffleRightColumn,
                                       MatchingSettings matching, Instant now) {
        return new Question(null, formId, sectionId, position, QuestionType.MATCHING,
                prompt, helpText, required, shuffleRightColumn, null, null, matching, now, now);
    }

    public static Question rehydrate(Long id, Long formId, Long sectionId, int position, QuestionType type,
                                     Prompt prompt, String helpText, boolean required, boolean shuffleOptions,
                                     ChoiceSettings choice, TextSettings text, MatchingSettings matching,
                                     Instant createdAt, Instant updatedAt) {
        return new Question(id, formId, sectionId, position, type, prompt, helpText, required, shuffleOptions,
                choice, text, matching, createdAt, updatedAt);
    }


    public Question moveTo(Long newSectionId, int newPosition, Instant now) {
        return new Question(id, formId, newSectionId, newPosition, type, prompt, helpText, required, shuffleOptions,
                choice, text, matching, createdAt, now);
    }

    public Question rename(Prompt newPrompt, Instant now) {
        return new Question(id, formId, sectionId, position, type, newPrompt, helpText, required, shuffleOptions,
                choice, text, matching, createdAt, now);
    }

    public Question changeHelp(String newHelp, Instant now) {
        return new Question(id, formId, sectionId, position, type, prompt, newHelp, required, shuffleOptions,
                choice, text, matching, createdAt, now);
    }

    public Question setRequired(boolean req, Instant now) {
        return new Question(id, formId, sectionId, position, type, prompt, helpText, req, shuffleOptions,
                choice, text, matching, createdAt, now);
    }

    public Question setShuffleOptions(boolean shuffle, Instant now) {
        return new Question(id, formId, sectionId, position, type, prompt, helpText, required, shuffle,
                choice, text, matching, createdAt, now);
    }

    public Question replaceChoiceOptions(List<Option> options, Instant now) {
        if (type != QuestionType.CHOICE && type != QuestionType.TRUE_FALSE)
            throw new QuestionTypeMismatchException("replaceChoiceOptions aplica a CHOICE/TRUE_FALSE");
        var mode = choice.mode();
        var bounds = (mode == SelectionMode.MULTI) ? choice.withBounds(choice.minSelections(), choice.maxSelections()) : choice;
        var updated = bounds.replaceOptions(options);
        return new Question(id, formId, sectionId, position, type, prompt, helpText, required, shuffleOptions,
                updated, null, null, createdAt, now);
    }

    public Question setMultiBounds(Integer min, Integer max, Instant now) {
        if (type != QuestionType.CHOICE)
            throw new QuestionTypeMismatchException("setMultiBounds solo para CHOICE (MULTI)");
        var updated = choice.withBounds(min, max);
        return new Question(id, formId, sectionId, position, type, prompt, helpText, required, shuffleOptions,
                updated, null, null, createdAt, now);
    }

    public Question setTextSettings(TextSettings newText, Instant now) {
        if (type != QuestionType.TEXT)
            throw new QuestionTypeMismatchException("setTextSettings solo para TEXT");
        return new Question(id, formId, sectionId, position, type, prompt, helpText, required, shuffleOptions,
                null, newText, null, createdAt, now);
    }

    public Question setMatching(MatchingSettings newMatching, Instant now) {
        if (type != QuestionType.MATCHING)
            throw new QuestionTypeMismatchException("setMatching solo para MATCHING");
        return new Question(id, formId, sectionId, position, type, prompt, helpText, required, shuffleOptions,
                null, null, newMatching, createdAt, now);
    }

    public Long getId(){ return id; }
    public Long getFormId(){ return formId; }
    public Long getSectionId(){ return sectionId; }
    public int getPosition(){ return position; }
    public QuestionType getType(){ return type; }
    public Prompt getPrompt(){ return prompt; }
    public String getHelpText(){ return helpText; }
    public boolean isRequired(){ return required; }
    public boolean isShuffleOptions(){ return shuffleOptions; }
    public ChoiceSettings getChoice(){ return choice; }
    public TextSettings getText(){ return text; }
    public MatchingSettings getMatching(){ return matching; }
    public Instant getCreatedAt(){ return createdAt; }
    public Instant getUpdatedAt(){ return updatedAt; }

    @Override public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Question q)) return false;
        return id != null && Objects.equals(id, q.id);
    }
    @Override public int hashCode(){ return id == null ? System.identityHashCode(this) : id.hashCode(); }
}
