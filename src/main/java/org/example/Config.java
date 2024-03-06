package org.example;

import java.time.LocalDate;

public class Config {

    private LocalDate beginningDate=LocalDate.of(2024,1,1);

    public LocalDate getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(LocalDate beginningDate) {
        this.beginningDate = beginningDate;
    }

    public LocalDate getEndingDaten() {
        return endingDaten;
    }

    public void setEndingDaten(LocalDate endingDaten) {
        this.endingDaten = endingDaten;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAnswerName() {
        return answerName;
    }

    public void setAnswerName(String answerName) {
        this.answerName = answerName;
    }

    private LocalDate endingDaten=LocalDate.of(2024,3,3);
    private String fileName="Input.pdf";
    private String answerName="Answer.json";

    public String getVarFileName() {
        return varFileName;
    }

    public void setVarFileName(String varFileName) {
        this.varFileName = varFileName;
    }

    private String varFileName="variables.json";

    public String getRandomValueListFileName() {
        return randomValueListFileName;
    }

    public void setRandomValueListFileName(String randomValueListFileName) {
        this.randomValueListFileName = randomValueListFileName;
    }

    private String randomValueListFileName = "randomValueList.json";

}
