/*
 * Copyright (c) 2-3/1/23, 12:14 AM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.request.vacancy;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип работы (по месту)")
public enum TypeOfWorkPlacement {

    OFFICE,
    REMOTE,
}
