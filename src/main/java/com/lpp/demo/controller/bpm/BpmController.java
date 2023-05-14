package com.lpp.demo.controller.bpm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpp.demo.dto.Result;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.Model;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @desc 类描述
 * @author: panpan.li@okg.com
 * @createTime: 2023年05月10日 19:49
 */
@RestController
@RequestMapping(value = "/bpm/v1")
public class BpmController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    // 部署流程定义
    @PostMapping(value = "/deploy")
    public Result<Deployment> deploy(@RequestParam String modelId) throws IOException {
        Deployment deployment;
        Model modelData = repositoryService.getModel(modelId);

        byte[] sourceBytes = repositoryService.getModelEditorSource(modelId);
        JsonNode editorNode = new ObjectMapper().readTree(sourceBytes);
        BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
        BpmnModel bpmnModel = bpmnJsonConverter.convertToBpmnModel(editorNode);

        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name("手动部署")
                .enableDuplicateFiltering().addBpmnModel(modelData.getName().concat(".bpmn20.xml"), bpmnModel);
        deployment = deploymentBuilder.deploy();
        return Result.success(deployment);
    }


    // 启动流程实例子
    @PostMapping(value = "/start")
    public Result<Map<String, Object>> start(String modelId, @RequestBody Map<String, Object> form) {
        Model model = repositoryService.getModel(modelId);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(model.getName(), form.get("businessKey").toString(), form);
        if (instance == null) {
            return Result.fail(1001, "创建失败，请重试");
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("businessKey", instance.getBusinessKey());
        map.put("processDefinitionKey", instance.getProcessDefinitionKey());
        map.put("processInstanceId", instance.getProcessInstanceId());
        return Result.success(map);
    }

    // 任务列表
    @GetMapping(value = "/task-list")
    public Result<List<Map<String, Object>>> taskList(String modelId, String userId) {

        Model model = repositoryService.getModel(modelId);

        // 根据流程的key、任务的负责人查询任务
        List<Map<String, Object>> list = taskService.createTaskQuery()
                .processDefinitionKey(model.getName())
                .taskAssignee(userId)
                .list()
                .stream()
                .map(task -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("taskId", task.getId());
                    map.put("taskDefinitionKey", task.getTaskDefinitionKey());
                    map.put("taskName", task.getName());
                    map.put("processInstanceId", task.getProcessInstanceId());
                    return map;
                })
                .collect(Collectors.toList());
        return Result.success(list);
    }

    // 完成任务
    @PostMapping(value = "/task-complete")
    public Result taskComplete(String taskId) {
        taskService.complete(taskId);
        return Result.success();
    }

}
