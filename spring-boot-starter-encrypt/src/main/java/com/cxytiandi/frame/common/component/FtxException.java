package com.cxytiandi.frame.common.component;

public class FtxException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String return_code;

    private String return_desc;

    public FtxException(String rtn_code, String rtn_desc) {
        super(rtn_desc);
        this.return_code = rtn_code;
        this.return_desc = rtn_desc;
    }

    public String getReturn_code() {
        return return_code;
    }

    public FtxException setReturn_code(String return_code) {
        this.return_code = return_code;
        return this;
    }

    public String getReturn_desc() {
        return return_desc;
    }

    public FtxException setReturn_desc(String return_desc) {
        this.return_desc = return_desc;
        return this;
    }
}
