package in.aryomtech.cgalert.Writ.Model;

import java.util.ArrayList;

public class WritModel {

    private String Judgement;
    private String dSummary;
    private String dateOfFiling;
    private String dateReply;
    private String district;
    private String dueDate;
    private String judgementDate;
    private String case_nature;
    private String pushkey;
    private String summary;
    private String caseNo;
    private String caseYear;
    private String timeLimit;
    private String decisionDate;
    private String uploaded_date;
    private String uploaded_file;
    private ArrayList<String> appellant;
    private ArrayList<String> respondents;
    private String reminded;
    private String seen;
    private String sent;

    public WritModel() {
    }

    public WritModel(String judgement, String dSummary, String dateOfFiling, String dateReply, String district, String dueDate, String judgementDate, String case_nature, String pushkey, String summary, String caseNo, String caseYear, String timeLimit, String decisionDate, String uploaded_date, String uploaded_file, ArrayList<String> appellant, ArrayList<String> respondents, String reminded) {
        Judgement = judgement;
        this.dSummary = dSummary;
        this.dateOfFiling = dateOfFiling;
        this.dateReply = dateReply;
        this.district = district;
        this.dueDate = dueDate;
        this.judgementDate = judgementDate;
        this.case_nature = case_nature;
        this.pushkey = pushkey;
        this.summary = summary;
        this.caseNo = caseNo;
        this.caseYear = caseYear;
        this.timeLimit = timeLimit;
        this.decisionDate = decisionDate;
        this.uploaded_date = uploaded_date;
        this.uploaded_file = uploaded_file;
        this.appellant = appellant;
        this.respondents = respondents;
        this.reminded = reminded;
    }

    public String getSeen() {
        return seen;
    }

    public String getSent() {
        return sent;
    }

    public ArrayList<String> getAppellant() {
        return appellant;
    }

    public String getReminded() {
        return reminded;
    }

    public String getUploaded_date() {
        return uploaded_date;
    }

    public String getUploaded_file() {
        return uploaded_file;
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

    public String getCase_nature() {
        return case_nature;
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
