package dev.guilhermesilva.atom_backend.mapper;

import dev.guilhermesilva.atom_backend.dto.request.ServiceTypeRequest;
import dev.guilhermesilva.atom_backend.dto.response.ServiceTypeResponse;
import dev.guilhermesilva.atom_backend.entity.ServiceType;
import org.springframework.stereotype.Component;

@Component
public class ServiceTypeMapper {

    public ServiceType toEntity(ServiceTypeRequest request) {
        return ServiceType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .durationInMinutes(request.getDurationInMinutes())
                .active(true)
                .build();
    }

    public ServiceTypeResponse toResponse(ServiceType serviceType) {
        return ServiceTypeResponse.builder()
                .id(serviceType.getId())
                .name(serviceType.getName())
                .description(serviceType.getDescription())
                .price(serviceType.getPrice())
                .durationInMinutes(serviceType.getDurationInMinutes())
                .active(serviceType.getActive())
                .createdAt(serviceType.getCreatedAt())
                .updatedAt(serviceType.getUpdatedAt())
                .build();
    }

    public void updateEntityFromRequest(ServiceType serviceType, ServiceTypeRequest request) {
        serviceType.setName(request.getName());
        serviceType.setDescription(request.getDescription());
        serviceType.setPrice(request.getPrice());
        serviceType.setDurationInMinutes(request.getDurationInMinutes());
    }
}