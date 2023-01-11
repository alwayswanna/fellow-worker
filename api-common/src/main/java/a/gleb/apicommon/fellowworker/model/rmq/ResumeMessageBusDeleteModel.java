/*
 * Copyright (c) 1-1/11/23, 11:29 PM
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
public class ResumeMessageBusDeleteModel {

    private UUID deleteResumeId;
}
