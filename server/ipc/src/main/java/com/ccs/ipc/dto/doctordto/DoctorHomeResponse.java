package com.ccs.ipc.dto.doctordto;

import lombok.Data;

/**
 * 医生首页响应DTO
 *
 * @author WZH
 * @since 2026-01-27
 */
@Data
public class DoctorHomeResponse {
    /**
     * 医生信息
     */
    private DoctorInfoResponse doctorInfo;

    /**
     * 今日问诊数
     */
    private Long todayConsultationCount;

    /**
     * 进行中的问诊数
     */
    private Long ongoingConsultationCount;

    /**
     * 总患者数
     */
    private Long totalPatientCount;

    /**
     * 本月问诊数
     */
    private Long monthConsultationCount;
}
