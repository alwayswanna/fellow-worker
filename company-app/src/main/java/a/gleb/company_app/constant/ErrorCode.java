package a.gleb.company_app.constant;

import a.gleb.fellow_worker.common.FellowWorkerErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 4XX */
    FW_4002(FellowWorkerErrorCode.FW_4002, 422, "Argument validation failed"),
    /* 5XX */
    FW_5000(FellowWorkerErrorCode.FW_5000, 500, "An unexpected error has occurred");

    private final String code;
    private final int httpStatusCode;
    private final String messageTemplate;
}
