package com.ccs.ipc.dto.statisticsdto;

import lombok.Data;

import java.io.Serializable;

/**
 * 按统计类型汇总项
 *
 * @author WZH
 * @since 2026-01-29
 */
@Data
public class StatTypeSum implements Serializable {

    private static final long serialVersionUID = 1L;

    private String statType;
    private Long total;
}
