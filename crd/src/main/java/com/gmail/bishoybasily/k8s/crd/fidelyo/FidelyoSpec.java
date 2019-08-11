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

@Data
@Accessors(chain = true)
@JsonDeserialize
public class FidelyoSpec implements KubernetesResource {

    private String image;
    private String version;
    private Integer replicas;
    private Integer port;
    private Deployment deployment;
    private Service service;
    private String serviceType;
    private Set<EnvVar> envVars = new HashSet<>();

}
