package in.aryomtech.cgalert.Writ.Model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.ArrayList;
@Keep
public class WritModel implements Serializable {

    public String Judgement;
    public String dSummary;
    public String dateOfFiling;
    public String dateReply;
    public String district;
    public String dueDate;
    public String judgementDate;
    public String case_nature;
    public String pushkey;
    public String summary;
    public String caseNo;
    public String caseYear;
    public String timeLimit;
    public String decisionDate;
    public String uploaded_date;
    public String uploaded_file;
    public ArrayList<String> appellant;
    public ArrayList<String> respondents;
    public String reminded;
    public String seen;
    public String sent;

    public WritModel() {
    }

    public WritModel(String judgement, String dSummary, String dateOfFiling, String dateReply, String district, String dueDate, String judgementDate, String case_nature, String pushkey, String summary, String caseNo, String caseYear, String timeLimit, String decisionDate, String uploaded_date, String uploaded_file, ArrayList<String> appellant, ArrayList<String> respondents, String reminded) {
        this.Judgement = judgement;
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

    public String getSeenn() {
        return seen;
    }

    public String getSentt() {
        return sent;
    }

    public ArrayList<String> getAppellant() {
        return appellant;
    }

    public String getRemind() {
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

    public String getJudgementt() {
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

    public String getkey() {
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
