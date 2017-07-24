package com.kaltura.netkit.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 */

public class ErrorElement {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({})
    public @interface ErrorCode{
        int BadRequestErrorCode = 400;
        int NotFoundCode = 404;
        int ConnectionErrorCode = 408;

        int InternalServerErrorCode = 500;
        int ServiceUnavailableErrorCode = 503;
        int LoadErrorCode = 518;
        int CanceledRequestCode = 520;

        int SessionErrorCode = 601;
        int GeneralErrorCode = 666;
    }

    public static ErrorElement GeneralError = new ErrorElement("GeneralError", "Something went wrong", ErrorCode.GeneralErrorCode);
    public static ErrorElement NotFound = new ErrorElement("NotFound", "Resource not found", ErrorCode.NotFoundCode);
    public static ErrorElement LoadError = new ErrorElement("LoadError", "Failed to load data from source", ErrorCode.LoadErrorCode);
    public static ErrorElement ServiceUnavailableError = new ErrorElement("ServiceUnavailableError", "Requested service is unavailable", ErrorCode.ServiceUnavailableErrorCode);
    public static ErrorElement CanceledRequest = new ErrorElement("CanceledRequest", "Request was canceled", ErrorCode.CanceledRequestCode);
    public static ErrorElement ConnectionError = new ErrorElement("ConnectionError", "Failed to connect to source", ErrorCode.ConnectionErrorCode);
    public static ErrorElement BadRequestError = new ErrorElement("BadRequestError", "Invalid or missing request params", ErrorCode.BadRequestErrorCode);
    public static ErrorElement SessionError = new ErrorElement("SessionError", "Failed to obtain session", ErrorCode.SessionErrorCode);

    public String name;
    private String message;
    private String code;
    protected Object extra;

    public ErrorElement(String name, String message, int code) {
        this(message, code);
        this.name = name;
    }

    public ErrorElement(String message, String code, Object extra) {
        this.message = message;
        this.code = code;
        this.extra = extra;
    }

    public ErrorElement(String message, int code) {
        this(message, code + "");
    }

    public ErrorElement(String message, String code) {
        this(message, code, null);
    }

    public String getMessage() {
        return message;
    }

    /**
     * enable user to change default message with his own
     *
     * @param message
     * @return
     */
    public ErrorElement message(String message) {
        this.message = message;
        return this;
    }

    public ErrorElement addMessage(String message) {
        this.message += "; " + message;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Object getExtra() {
        return extra;
    }

    public static ErrorElement fromCode(int code, String message) {
        switch (code) {
            case 404:
                return ErrorElement.NotFound.message(message);
            case 400:
                return ErrorElement.BadRequestError.message(message);
            default:
                return new ErrorElement(message, code);
        }
    }

    public static ErrorElement fromException(Exception exception) {
        switch (exception.getClass().getSimpleName()) {
            case "SocketTimeoutException":
            case "UnknownHostException":
                return ErrorElement.ConnectionError;

            default:
                return ErrorElement.GeneralError;

        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(name != null){
            builder.append("Error: ").append(name).append("; ");
        }
        builder.append("code:").append(code).append(", Message:").append(message);
        return builder.toString();
    }
}
