package com.attendance.system.dto.response;

import com.attendance.system.enums.CertificateClientStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicFeedbackContextResponse {
    private String jobCode;
    private String customerName;
    private String companyNameHint;
    private CertificateClientStatus certificateClientStatus;
    private boolean expired;
    private boolean revoked;
}
