/*
 * Copyright (c) 2017- Ftecx Corp.
 * All rights reserved.
 */
package com.cxytiandi.frame.util.activiti;

import com.cxytiandi.frame.util.string.StringUtil;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.impl.cmd.GetBpmnModelCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 导出高亮跟踪图，和完整流程图
 *
 * @author se7en
 * @since 2017/11/23 19:13
 */
public class ProcessInstanceDiagramCmd implements Command<InputStream> {
    private String processInstanceId;
    private String taskId;
    private Map<String, Object> hisVariables;
    private String filePath;

    public ProcessInstanceDiagramCmd(String processInstanceId, String taskId, Map<String, Object> hisVariables, String filePath) {
        this.processInstanceId = processInstanceId;
        this.taskId = taskId;
        this.hisVariables = hisVariables;
        this.filePath = filePath;

    }

    public InputStream execute(CommandContext commandContext) {
        File imageFile = new File(filePath);
        InputStream inputStream = null;
        String processDefinitionId = null;
        List<String> activityIds = null;
        ExecutionEntity executionEntity = null;
        Map<String, Object> variables = null;
        try {
            FileOutputStream outStream = new FileOutputStream(imageFile);
            if (StringUtil.isNotEmpty(taskId)) {
                ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
                executionEntity = executionEntityManager.findExecutionById(processInstanceId);
                activityIds = executionEntity.findActiveActivityIds();
                processDefinitionId = executionEntity.getProcessDefinitionId();
                variables = executionEntity.getVariables();
            } else {
                HistoricProcessInstanceEntity historicProcessInstance = commandContext.getHistoricProcessInstanceEntityManager().findHistoricProcessInstance(processInstanceId);
                processDefinitionId = historicProcessInstance.getProcessDefinitionId();
                variables = hisVariables;
            }

            GetBpmnModelCmd getBpmnModelCmd = new GetBpmnModelCmd(processDefinitionId);

            BpmnModel bpmnModel = getBpmnModelCmd.execute(commandContext);

            List<Process> processes = bpmnModel.getProcesses();

            for (Process process : processes) {
                Collection<FlowElement> flowElements = process.getFlowElements();
                for (FlowElement flowElement : flowElements) {
                    for (String key : variables.keySet()) {
                        if (StringUtil.isNotEmpty(flowElement.getName()) && flowElement.getName().contains(key)) {
                            flowElement.setName(variables.get(key).toString());
                        }
                    }
                }
            }
            if (StringUtil.isNotEmpty(taskId)) {
                inputStream = new DefaultProcessDiagramGenerator().generateDiagram(
                        bpmnModel, "png",
                        activityIds, Collections.emptyList(),
                        "宋体",
                        "宋体",
                        commandContext.getProcessEngineConfiguration().getClassLoader(), 1.0);
            } else {
                inputStream = new DefaultProcessDiagramGenerator().generateDiagram(
                        bpmnModel, "png",
                        "宋体",
                        "宋体",
                        commandContext.getProcessEngineConfiguration().getClassLoader(), 1.0);
            }

            int len = 0;
            byte[] b = new byte[1024];
            while ((len = inputStream.read(b, 0, 1024)) != -1) {
                outStream.write(b, 0, len);
            }

            //关闭输出流
            outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;

    }
}
