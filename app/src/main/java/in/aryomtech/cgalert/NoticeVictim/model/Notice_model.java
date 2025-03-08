package in.aryomtech.cgalert.NoticeVictim.model;

import androidx.annotation.Keep;

import java.io.Serializable;
@Keep
public class Notice_model implements Serializable {

    public String advocate;
    public String caseType;
    public String caseYear;
    public String crimeNo;
    public String crimeYear;
    public String pushkey;
    public String doc_url;
    public String district;
    public String hearingDate;
    public String noticeDate;
    public String station;
    public String appellant;
    public String caseNo;
    public String reminded;
    public String seen;
    public String sent;
    public String number;
    public String uploaded_file;
    public String uploaded_date;
    public String uid;

    public Notice_model(){}

    public String getUid() {
        return uid;
    }

    public String getUploaded_date() {
        return uploaded_date;
    }

    public String getUploaded_file() {
        return uploaded_file;
    }

    public String getReminded() {
        return reminded;
    }

    public String getSeen() {
        return seen;
    }

    public String getSent() {
        return sent;
    }

    public String getNumber() {
        return number;
    }

    public String getDoc_url() {
        return doc_url;
    }

    public String getAdvocate() {
        return advocate;
    }

    public String getCaseType() {
        return caseType;
    }

    public String getCaseYear() {
        return caseYear;
    }

    public String getCrimeNo() {
        return crimeNo;
    }

    public String getCrimeYear() {
        return crimeYear;
    }

    public String getDistrict() {
        return district;
    }

    public String getHearingDate() {
        return hearingDate;
    }

    public String getNoticeDate() {
        return noticeDate;
    }

    public String getStation() {
        return station;
    }

    public String getPushkey() {
        return pushkey;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public String getAppellant() {
        return appellant;
    }
}
