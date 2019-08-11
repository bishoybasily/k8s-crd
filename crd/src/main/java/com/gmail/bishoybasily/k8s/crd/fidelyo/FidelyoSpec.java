package com.gmail.bishoybasily.k8s.crd.fidelyo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

// the kind specs we mentioned earlier,
// please ignore the deployment and the service for now, i'll come to this later
@Data
@Accessors(chain = true)
@JsonDeserialize
public class FidelyoSpec implements KubernetesResource {

    private String image;
    private Integer replicas;
    private Integer port;
    private String serviceType;
    private Set<EnvVar> envVars = new HashSet<>();

    private Service service;
    private Deployment deployment;

}
