/*
 * Copyright (c) 1-1/12/23, 11:58 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.rmq;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeMessageDelete {

    private UUID deleteResumeId;
}
