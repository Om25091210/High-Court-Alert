package in.aryomtech.cgalert.Writ;

import java.util.ArrayList;

public class WritModel {

    private String Judgement;
    private String dSummary;
    private String dateOfFiling;
    private String dateReply;
    private String district;
    private String dueDate;
    private String judgementDate;
    private String nature;
    private String pushkey;
    private String summary;
    private String caseNo;
    private String caseYear;
    private String timeLimit;
    private String decisionDate;
    private ArrayList<String> appellants;
    private ArrayList<String> respondents;

    public WritModel() {
    }

    public WritModel(String judgement, String dSummary, String dateOfFiling, String dateReply, String district, String dueDate, String judgementDate, String nature, String pushkey, String summary,
                     String caseYear, String caseNo ,String timeLimit, ArrayList<String> appellants, ArrayList<String> respondents,String decisionDate) {
        Judgement = judgement;
        this.dSummary = dSummary;
        this.dateOfFiling = dateOfFiling;
        this.dateReply = dateReply;
        this.district = district;
        this.dueDate = dueDate;
        this.judgementDate = judgementDate;
        this.nature = nature;
        this.pushkey = pushkey;
        this.summary = summary;
        this.timeLimit = timeLimit;
        this.appellants = appellants;
        this.caseNo = caseNo;
        this.caseYear = caseYear;
        this.decisionDate = decisionDate;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public String getCaseYear() {
        return caseYear;
    }

    public String getJudgement() {
        return Judgement;
    }

    public String getdSummary() {
        return dSummary;
    }

    public String getDateOfFiling() {
        return dateOfFiling;
    }

    public String getDateReply() {
        return dateReply;
    }

    public String getDistrict() {
        return district;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getJudgementDate() {
        return judgementDate;
    }

    public String getNature() {
        return nature;
    }

    public ArrayList<String> getAppellants() {
        return appellants;
    }

    public ArrayList<String> getRespondents() {
        return respondents;
    }

    public String getPushkey() {
        return pushkey;
    }

    public String getSummary() {
        return summary;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public String getDecisionDate() {
        return decisionDate;
    }
}