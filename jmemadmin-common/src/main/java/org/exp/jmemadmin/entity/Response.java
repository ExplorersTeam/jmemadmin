package org.exp.jmemadmin.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.exp.jmemadmin.common.utils.DateUtils;

/**
 * Bean for response.
 *
 * @author ZhangQingliang
 *
 */

@XmlRootElement
public class Response {
    @XmlElement(name = "result")
    private int code;

    @XmlElement(required = false)
    private String content;

    private String time = DateUtils.getNowTime();

    public Response() {
        // Do nothing.
    }

    public Response(int code) {
        this(code, null);
    }

    public Response(int code, String content) {
        if (ResultStatus.isValue(code)) {
            this.code = code;
        } else {
            this.code = ResultStatus.FAILED.value();
        }
        if (null != content) {
            this.content = content;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        if (ResultStatus.isValue(code)) {
            this.code = code;
        } else {
            this.code = ResultStatus.FAILED.value();
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        // Do nothing.
    }

    @Override
    public String toString() {
        return "Response [code=" + code + ", content=" + content + ", time=" + time + "]";
    }

    public enum ResultStatus {
        SUCCESS(0), FAILED(-1);

        private int value;

        ResultStatus(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static boolean isValue(int value) {
            ResultStatus[] statuses = values();
            for (int i = 0; i < statuses.length; ++i) {
                if (value == statuses[i].value()) {
                    return true;
                }
            }
            return false;
        }
    }

}
