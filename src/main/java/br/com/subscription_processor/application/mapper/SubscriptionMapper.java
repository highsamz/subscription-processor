package br.com.subscription_processor.application.mapper;

import br.com.subscription_processor.application.dto.response.SubscriptionResponse;
import br.com.subscription_processor.domain.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(source = "status.name", target = "status")
    SubscriptionResponse toResponse(Subscription subscription);
}