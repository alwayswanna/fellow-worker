/*
 * Copyright (c) 1-1/12/23, 11:58 PM
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
public class ResumeMessageCreate {

    private ResumeResponseModel resume;

    private String base64Image;

    private String imageExtension;
}
