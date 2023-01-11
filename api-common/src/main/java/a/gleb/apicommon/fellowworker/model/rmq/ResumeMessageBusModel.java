/*
 * Copyright (c) 1-1/11/23, 11:29 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.rmq;

import a.gleb.apicommon.fellowworker.model.response.resume.ResumeResponseModel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeMessageBusModel {

    private ResumeResponseModel resume;

    private String base64Image;

    private String imageExtension;
}
