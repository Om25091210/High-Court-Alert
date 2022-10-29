package in.aryomtech.cgalert.SMS_service;


import org.apache.commons.httpclient.params.HttpParams;
import org.apache.http.params.CoreConnectionPNames;

public final class HttpConnectionParams implements CoreConnectionPNames {
    private HttpConnectionParams() {
    }

    public static int getSoTimeout(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            return params.getIntParameter("http.socket.timeout", 0);
        }
    }

    public static void setSoTimeout(HttpParams params, int timeout) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            params.setIntParameter("http.socket.timeout", timeout);
        }
    }

    public static boolean getSoReuseaddr(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            return params.getBooleanParameter("http.socket.reuseaddr", false);
        }
    }

    public static void setSoReuseaddr(HttpParams params, boolean reuseaddr) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            params.setBooleanParameter("http.socket.reuseaddr", reuseaddr);
        }
    }

    public static boolean getTcpNoDelay(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            return params.getBooleanParameter("http.tcp.nodelay", true);
        }
    }

    public static void setTcpNoDelay(HttpParams params, boolean value) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            params.setBooleanParameter("http.tcp.nodelay", value);
        }
    }

    public static int getSocketBufferSize(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            return params.getIntParameter("http.socket.buffer-size", -1);
        }
    }

    public static void setSocketBufferSize(HttpParams params, int size) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            params.setIntParameter("http.socket.buffer-size", size);
        }
    }

    public static int getLinger(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            return params.getIntParameter("http.socket.linger", -1);
        }
    }

    public static void setLinger(HttpParams params, int value) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            params.setIntParameter("http.socket.linger", value);
        }
    }

    public static int getConnectionTimeout(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            return params.getIntParameter("http.connection.timeout", 0);
        }
    }

    public static void setConnectionTimeout(HttpParams params, int timeout) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            params.setIntParameter("http.connection.timeout", timeout);
        }
    }

    public static boolean isStaleCheckingEnabled(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            return params.getBooleanParameter("http.connection.stalecheck", true);
        }
    }

    public static void setStaleCheckingEnabled(HttpParams params, boolean value) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            params.setBooleanParameter("http.connection.stalecheck", value);
        }
    }
}

