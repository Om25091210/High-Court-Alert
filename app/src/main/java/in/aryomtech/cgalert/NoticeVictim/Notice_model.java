package in.aryomtech.cgalert.NoticeVictim;

public class Notice_model {

    private String advocate;
    private String caseType;
    private String caseYear;
    private String crimeNo;
    private String crimeYear;
    private String pushkey;
    private String district;
    private String hearingDate;
    private String noticeDate;
    private String station;
    private String appellant;
    private String caseNo;

    public Notice_model(){}

    public Notice_model(String advocate, String caseType, String caseYear, String crimeNo, String crimeYear, String district, String hearingDate, String noticeDate, String station, String pushkey, String appellant, String caseNo) {
        this.advocate = advocate;
        this.caseType = caseType;
        this.caseYear = caseYear;
        this.crimeNo = crimeNo;
        this.crimeYear = crimeYear;
        this.district = district;
        this.hearingDate = hearingDate;
        this.noticeDate = noticeDate;
        this.station = station;
        this.pushkey = pushkey;
        this.appellant = appellant;
        this.caseNo = caseNo;
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
