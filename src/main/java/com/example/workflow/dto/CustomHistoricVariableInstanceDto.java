package com.example.workflow.dto;

import lombok.Data;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.rest.dto.VariableValueDto;

import java.util.Date;

@Data
public class CustomHistoricVariableInstanceDto extends VariableValueDto {

    private String id;
    private String name;
    private String errorMessage;
    private String state;
    private Date createTime;
    private Date removalTime;

    public static CustomHistoricVariableInstanceDto fromHistoricVariableInstance(HistoricVariableInstance historicVariableInstance) {

        CustomHistoricVariableInstanceDto dto = new CustomHistoricVariableInstanceDto();

        dto.id = historicVariableInstance.getId();
        dto.name = historicVariableInstance.getName();
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
}
