package com.kaltura.netkit.services.api.ott.phoenix;


import com.kaltura.netkit.utils.ErrorElement;
import com.kaltura.netkit.utils.RestrictionError;

/**
 * @hide
 */

public class PhoenixErrorHelper {

    /**
     * in case specific error codes should be parsed to predefined errors.
     *
     * @param code
     * @param message
     * @return
     */
    public static ErrorElement getErrorElement(String code, String message) {
        ErrorElement errorElement = getDefinedErrorElement(code, message);
        if (errorElement == null) {
            errorElement = new ErrorElement(code, message);
        }
        return errorElement;
    }

    public static ErrorElement getErrorElement(String code, String message, ErrorElement fallback) {
        ErrorElement errorElement = getDefinedErrorElement(code, message);
        if (errorElement == null) {
            return fallback != null ? fallback : new ErrorElement(code, message);
        }
        return errorElement;
    }

    public static ErrorElement getErrorElement(ErrorElement error) {
        ErrorElement errorElement = getDefinedErrorElement(error.getCode(), error.getMessage());
        if (errorElement == null) {
            return error;
        }
        return errorElement;
    }

    /**
     * parse phoenix specific errors to playkit errors.
     * errors with text code are messages that my be retrieved from the "getPlaybackContext" API.
     *
     * @param code
     * @param message
     * @return
     */
    private static ErrorElement getDefinedErrorElement(String code, String message) {
        switch (code) {
            case "2016":
                return new ErrorElement(message, code);

            case "500063": {
                int startIndexExternalCode = "externalCode: [".length();
                int endIndexExternalCode = message.indexOf("]", startIndexExternalCode);
                String externalCode = message.substring(startIndexExternalCode, endIndexExternalCode);
                switch (externalCode) {
                    case "10001":
                        return ErrorElement.SessionError.message("NoEntitlements");
                    case "10002":
                        return ErrorElement.SessionError.message("GeoBlock");
                }
            }

            case "500016":
                return ErrorElement.SessionError.message("session token has been expired");

            case "RecordingPlaybackNotAllowedForNonExistingEpgChannel":
            case "3039":
            case "4022":
            case "3050":
                return ErrorElement.NotFound.message("requested content was not found");

            case "NoFilesFound":
            case "3054":
                return ErrorElement.NotFound.message("no available sources for media");

            case "ServiceNotAllowed":
            case "DeviceTypeNotAllowed":
            case "1002":
            case "3003":
                return new RestrictionError("requested operation is not allowed", RestrictionError.Restriction.NotAllowed);

            case "RecordingPlaybackNotAllowedForNotEntitledEpgChannel":
            case "NotEntitled":
            case "3051":
            case "3032":
                return new RestrictionError("content is not entitled", RestrictionError.Restriction.NotEntitled);

            case "ConcurrencyLimitation":
            case "MediaConcurrencyLimitation":
            case "4000":
            case "4001":
                return new RestrictionError("restricted due to concurrency limitation", RestrictionError.Restriction.ConcurrencyLimitation);

            case "2001":
                return new RestrictionError("restricted due to suspended account ", RestrictionError.Restriction.Suspended);

            default:
                return null;
        }
    }
}
