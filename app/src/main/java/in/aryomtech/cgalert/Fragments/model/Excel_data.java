package in.aryomtech.cgalert.Fragments.model;

import java.io.Serializable;

public class Excel_data implements Serializable {

    private String A;
    private String B;
    private String C;
    private String D;
    private String E;
    private String F;
    private String G;
    private String H;
    private String I;
    private String J;
    private String K;
    private String L;
    private String M;
    private String N;
    private String date;
    private String type;
    private String pushkey;
    private String reminded;
    private String seen;
    private String date_of_alert;
    private String sent;
    private String number;

    public Excel_data(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, String k, String l, String m, String n, String date, String type, String pushkey, String reminded, String seen, String date_of_alert, String sent, String number) {
        A = a;
        B = b;
        C = c;
        D = d;
        E = e;
        F = f;
        G = g;
        H = h;
        I = i;
        J = j;
        K = k;
        L = l;
        M = m;
        N = n;
        this.date = date;
        this.type = type;
        this.pushkey = pushkey;
        this.reminded = reminded;
        this.seen = seen;
        this.date_of_alert = date_of_alert;
        this.sent = sent;
        this.number = number;
    }

    public Excel_data() {
    }

    public String getSent() {
        return sent;
    }

    public String getSeen() {
        return seen;
    }

    public String getA() {
        return A;
    }

    public String getB() {
        return B;
    }

    public String getC() {
        return C;
    }

    public String getD() {
        return D;
    }

    public String getE() {
        return E;
    }

    public String getF() {
        return F;
    }

    public String getG() {
        return G;
    }

    public String getH() {
        return H;
    }

    public String getI() {
        return I;
    }

    public String getJ() {
        return J;
    }

    public String getK() {
        return K;
    }

    public String getL() {
        return L;
    }

    public String getM() {
        return M;
    }

    public String getN() {
        return N;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getPushkey() {
        return pushkey;
    }

    public String getReminded() {
        return reminded;
    }

    public String getDate_of_alert() {
        return date_of_alert;
    }

    public String getNumber() {
        return number;
    }
}

