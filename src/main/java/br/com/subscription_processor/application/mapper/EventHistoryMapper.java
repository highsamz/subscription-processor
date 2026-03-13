package br.com.subscription_processor.application.mapper;

import br.com.subscription_processor.application.dto.response.EventHistoryResponse;
import br.com.subscription_processor.domain.entity.EventHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventHistoryMapper {

    @Mapping(source = "eventType", target = "eventType")
    EventHistoryResponse toResponse(EventHistory eventHistory);
}