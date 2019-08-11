
package com.gmail.bishoybasily.k8s.crd.fidelyo;

import io.fabric8.kubernetes.client.CustomResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

// the custom kind/Resource
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class Fidelyo extends CustomResource {

    private FidelyoSpec spec;

}
