package com.nanqiong.cppcaiagent.service;

import com.nanqiong.cppcaiagent.dto.CozeGenerateReportPayload;
import com.nanqiong.cppcaiagent.dto.ReportContentBlock;

import java.util.List;

public interface RemoteAiService {

    List<ReportContentBlock> generateReport(CozeGenerateReportPayload payload);
}
