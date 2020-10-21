package com.example.workflow.dto;

import lombok.Data;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.camunda.bpm.engine.rest.dto.runtime.VariableInstanceDto;
import org.camunda.bpm.engine.runtime.VariableInstance;

import java.util.Date;
import java.util.Map;

/* Based in the class org.camunda.bpm.engine.rest.dto.VariableValueDto */
@Data
public class CustomVariableInstanceDto extends VariableValueDto {

    private String id;
    private String errorMessage;
    private String state;
    private Date createTime;
    private Date removalTime;
    private String type;
    private Object value;
    private Map<String, Object> valueInfo;

    public static CustomVariableInstanceDto fromHistoricVariableInstance(HistoricVariableInstance historicVariableInstance) {

        CustomVariableInstanceDto dto = new CustomVariableInstanceDto();

        dto.id = historicVariableInstance.getId();
        dto.state = historicVariableInstance.getState();
        dto.createTime = historicVariableInstance.getCreateTime();
        dto.removalTime = historicVariableInstance.getRemovalTime();

        if(historicVariableInstance.getErrorMessage() == null) {
            VariableValueDto.fromTypedValue(dto, historicVariableInstance.getTypedValue());
        }
        else {
            dto.errorMessage = historicVariableInstance.getErrorMessage();
            dto.type = VariableValueDto.toRestApiTypeName(historicVariableInstance.getTypeName());
        }

        return dto;
    }

    public static CustomVariableInstanceDto fromVariableInstance(VariableInstance variableInstance) {
        CustomVariableInstanceDto dto = new CustomVariableInstanceDto();

        dto.id = variableInstance.getId();
        if(variableInstance.getErrorMessage() == null) {
            VariableValueDto.fromTypedValue(dto, variableInstance.getTypedValue());
        }
        else {
            dto.errorMessage = variableInstance.getErrorMessage();
            dto.type = VariableValueDto.toRestApiTypeName(variableInstance.getTypeName());
        }

        return dto;
    }
}
